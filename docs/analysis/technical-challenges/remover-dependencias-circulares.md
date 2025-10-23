# Guia Prático: Remoção de Dependências Circulares

## Contexto

Este documento detalha as violações de arquitetura hexagonal encontradas no projeto e fornece exemplos práticos de como corrigi-las.

**Data da Análise**: 23/10/2025
**Análise baseada em**: Branch `claude/identify-project-demands-011CUPMm5Wi9a9iu4zYoPNXt`

---

## Princípios da Arquitetura Hexagonal

### Regras de Dependência

```
┌─────────────────────────────────────────────────┐
│                                                 │
│  ╔═══════════════════════════════════════╗     │
│  ║           DOMAIN (Núcleo)             ║     │
│  ║  - Entidades                          ║     │
│  ║  - Value Objects                      ║     │
│  ║  - Exceções de domínio                ║     │
│  ║  - ZERO dependências externas         ║     │
│  ╚═══════════════════════════════════════╝     │
│              ↑                ↑                 │
│              │                │                 │
│  ┌───────────┴────┐    ┌─────┴──────────┐      │
│  │  PORT IN       │    │  PORT OUT      │      │
│  │  (Use Cases)   │    │  (Interfaces)  │      │
│  │  - Interfaces  │    │  - Repositories│      │
│  └───────┬────────┘    └────────┬───────┘      │
│          ↑                      ↑               │
│          │                      │               │
│  ┌───────┴────────┐    ┌────────┴───────┐      │
│  │ APPLICATION    │    │                │      │
│  │ (Orquestração) │    │                │      │
│  │ - Services     │    │                │      │
│  └────────────────┘    │                │      │
│          ↑             │                │      │
│          │             │                │      │
│  ┌───────┴────────┐   ┌┴────────────────┴──┐   │
│  │  ADAPTER IN    │   │  ADAPTER OUT       │   │
│  │  - Controllers │   │  - Persistence     │   │
│  │  - DTOs        │   │  - Messaging       │   │
│  │  - Mappers     │   │  - External APIs   │   │
│  └────────────────┘   └────────────────────┘   │
│                                                 │
└─────────────────────────────────────────────────┘

REGRAS:
✅ Domain pode: Não depender de NADA (Java puro)
✅ Ports podem: Depender APENAS de Domain
✅ Application pode: Depender de Ports e Domain
✅ Adapters podem: Depender de Ports, Domain e frameworks
❌ NUNCA: Application → Adapter
❌ NUNCA: Port → Adapter
❌ NUNCA: Adapter.Out → Adapter.In
```

---

## Violações Identificadas

### 📊 Resumo das Violações

| # | Tipo | Origem | Destino | Severidade | Arquivo |
|---|------|--------|---------|------------|---------|
| 1 | Application → Adapter | `application.service.transacao` | `adapter.out.messaging` | 🔴 Alta | `NotificarTransacaoService.java:3` |
| 2 | Port → Adapter | `port.out.categoria` | `adapter.out.persistence` | 🔴 Alta | `CategoriaRepository.java:3` |
| 3 | Adapter.Out → Adapter.In | `adapter.out.grafico` | `adapter.in.web.response` | 🟡 Média | `JpaGraficoRepository.java:3-5` |

---

## 🔴 VIOLAÇÃO #1: Application → Adapter

### Problema Identificado

**Arquivo**: `application/service/transacao/NotificarTransacaoService.java`

```java
package com.vinicius.gerenciamento_financeiro.application.service.transacao;

// ❌ VIOLAÇÃO: Application importando classe de Adapter
import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;

@Service
public class NotificarTransacaoService {
    private final NotificarUseCase notificarUseCase;

    public void notificarTransacaoAtrasada(Transacao transacao){
        // ❌ Usando constantes do adapter
        notificarUseCase.enviarNotificacaoComAtraso(
            RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO,
            RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO,
            "Transação programada...",
            delayMillis
        );
    }
}
```

**Por que é um problema?**
- A camada `application` NÃO deve conhecer detalhes de infraestrutura (RabbitMQ)
- Cria acoplamento direto com implementação específica
- Dificulta troca de tecnologia (ex: RabbitMQ → Kafka)
- Viola o princípio de inversão de dependência

---

### ✅ Solução: Mover Constantes para Domain

#### Passo 1: Criar Value Object no Domain

**Arquivo**: `domain/model/notificacao/CanalNotificacao.java` (NOVO)

```java
package com.vinicius.gerenciamento_financeiro.domain.model.notificacao;

/**
 * Value Object que representa um canal de notificação.
 * Conceito de domínio - independente de tecnologia.
 */
public record CanalNotificacao(
    String exchange,
    String routingKey,
    TipoCanal tipo
) {
    // Factory methods para canais conhecidos
    public static CanalNotificacao transacoesVencimento() {
        return new CanalNotificacao(
            "transacao_exchange_delayed",
            "transacao_routing_key",
            TipoCanal.TRANSACAO_VENCIMENTO
        );
    }

    public static CanalNotificacao transacoesGeral() {
        return new CanalNotificacao(
            "transacao_exchange",
            "transacao_routing",
            TipoCanal.TRANSACAO_GERAL
        );
    }

    public enum TipoCanal {
        TRANSACAO_VENCIMENTO,
        TRANSACAO_GERAL,
        CLIENTE_ATIVACAO
    }
}
```

#### Passo 2: Atualizar Port para Usar Domain

**Arquivo**: `port/in/NotificarUseCase.java` (ATUALIZADO)

```java
package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.notificacao.CanalNotificacao;

public interface NotificarUseCase {
    // ✅ Agora usa tipos de domínio
    void enviarNotificacao(CanalNotificacao canal, String mensagem);

    void enviarNotificacaoComAtraso(
        CanalNotificacao canal,
        String mensagem,
        long delayMillis
    );
}
```

#### Passo 3: Refatorar Application Service

**Arquivo**: `application/service/transacao/NotificarTransacaoService.java` (REFATORADO)

```java
package com.vinicius.gerenciamento_financeiro.application.service.transacao;

// ✅ Apenas imports de domain e port
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.notificacao.CanalNotificacao;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class NotificarTransacaoService {

    private final NotificarUseCase notificarUseCase;

    public NotificarTransacaoService(NotificarUseCase notificarUseCase) {
        this.notificarUseCase = notificarUseCase;
    }

    public void notificarTransacaoAtrasada(Transacao transacao){
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataVencimento = transacao.getConfiguracao()
            .getDataVencimento().atStartOfDay();
        long delayMillis = ChronoUnit.MILLIS.between(agora, dataVencimento);

        // ✅ Usa factory method do domain
        CanalNotificacao canal = CanalNotificacao.transacoesVencimento();

        if (delayMillis > 0) {
            notificarUseCase.enviarNotificacaoComAtraso(
                canal,
                "Transação programada para " + transacao.getConfiguracao().getDataVencimento(),
                delayMillis
            );
        } else {
            notificarUseCase.enviarNotificacao(
                canal,
                "Transação já venceu e será processada agora!"
            );
        }
    }
}
```

#### Passo 4: Atualizar Adapter de Messaging

**Arquivo**: `adapter/out/messaging/RabbitMQNotificador.java` (ATUALIZADO)

```java
package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

import com.vinicius.gerenciamento_financeiro.domain.model.notificacao.CanalNotificacao;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQNotificador implements NotificarUseCase {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void enviarNotificacao(CanalNotificacao canal, String mensagem) {
        log.info("Enviando notificação para canal: {}", canal.tipo());

        // ✅ Adaptador traduz domain → RabbitMQ
        rabbitTemplate.convertAndSend(
            canal.exchange(),
            canal.routingKey(),
            mensagem
        );
    }

    @Override
    public void enviarNotificacaoComAtraso(
        CanalNotificacao canal,
        String mensagem,
        long delayMillis
    ) {
        log.info("Agendando notificação com delay de {}ms para canal: {}",
            delayMillis, canal.tipo());

        rabbitTemplate.convertAndSend(
            canal.exchange(),
            canal.routingKey(),
            mensagem,
            message -> {
                message.getMessageProperties().setDelay((int) delayMillis);
                return message;
            }
        );
    }
}
```

#### Passo 5: Remover RabbitMQConstants (Opcional)

Se não houver outros usos, você pode deletar o arquivo `RabbitMQConstants.java` completamente, pois os valores agora estão no domain como factory methods.

---

### 📊 Benefícios da Refatoração

| Antes | Depois |
|-------|--------|
| ❌ Application depende de Adapter | ✅ Application depende apenas de Domain |
| ❌ Strings mágicas espalhadas | ✅ Value Object com semântica clara |
| ❌ Difícil trocar RabbitMQ por Kafka | ✅ Troca transparente - só mudar adapter |
| ❌ Testes precisam mockar constantes | ✅ Testes usam factory methods do domain |

---

## 🔴 VIOLAÇÃO #2: Port → Adapter

### Problema Identificado

**Arquivo**: `port/out/categoria/CategoriaRepository.java`

```java
package com.vinicius.gerenciamento_financeiro.port.out.categoria;

// ❌ VIOLAÇÃO: Port importando entidade JPA do Adapter
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;

public interface CategoriaRepository {
    Categoria save(Categoria entity);
    void saveAll(List<Categoria> listaDeCategoriaJpaEntities); // ❌ Nome errado!
    Optional<Categoria> findById(CategoriaId id);
    // ...
}
```

**Por que é um problema?**
- Ports devem ser **totalmente agnósticos** de implementação
- Import não é usado, mas cria acoplamento desnecessário
- Impede substituir JPA por outra tecnologia
- Viola separação de responsabilidades

---

### ✅ Solução: Remover Import e Limpar Assinaturas

**Arquivo**: `port/out/categoria/CategoriaRepository.java` (REFATORADO)

```java
package com.vinicius.gerenciamento_financeiro.port.out.categoria;

// ✅ Apenas imports de domain
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Port de saída para persistência de categorias.
 * Define o contrato sem conhecer a implementação (JPA, MongoDB, etc).
 */
public interface CategoriaRepository {

    /**
     * Salva ou atualiza uma categoria.
     * @param categoria entidade de domínio
     * @return categoria persistida com ID gerado
     */
    Categoria save(Categoria categoria);

    /**
     * Salva múltiplas categorias em lote.
     * @param categorias lista de entidades de domínio
     */
    void saveAll(List<Categoria> categorias); // ✅ Nome corrigido

    /**
     * Busca categoria por ID.
     */
    Optional<Categoria> findById(CategoriaId id);

    /**
     * Lista todas as categorias (use com cuidado - prefira paginação).
     */
    List<Categoria> findAll();

    /**
     * Lista categorias de um usuário específico.
     */
    List<Categoria> findByUsuarioId(UsuarioId usuarioId);

    /**
     * Lista todas as categorias com paginação.
     */
    Page<Categoria> findAll(Pageable pageable);

    /**
     * Verifica se categoria pertence ao usuário.
     */
    boolean existsByIdAndUsuarioId(CategoriaId categoriaId, UsuarioId usuarioId);

    /**
     * Remove categoria por ID.
     */
    void deleteById(Long id);

    /**
     * Busca categoria verificando propriedade do usuário.
     */
    Optional<Categoria> findByIdAndUsuarioId(CategoriaId categoriaId, UsuarioId usuarioId);

    /**
     * Lista categorias do usuário com paginação.
     */
    Page<Categoria> findByUsuarioId(UsuarioId usuarioId, Pageable pageable);
}
```

---

### 📝 Checklist de Port Limpo

Ao revisar um Port, garanta:

- [ ] **ZERO imports de `adapter.*`**
- [ ] **Apenas tipos de `domain.*` e bibliotecas padrão**
- [ ] **Documentação clara de cada método**
- [ ] **Nomes de parâmetros refletem conceitos de domínio** (não "entity", "jpa", "dto")
- [ ] **Assinaturas independentes de tecnologia** (pode usar `Pageable` do Spring pois é padrão)

---

## 🟡 VIOLAÇÃO #3: Adapter.Out → Adapter.In

### Problema Identificado

**Arquivo**: `adapter/out/grafico/JpaGraficoRepository.java`

```java
package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

// ❌ VIOLAÇÃO: Adapter de persistência usando DTOs de resposta HTTP
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface JpaGraficoRepository extends CrudRepository<TransacaoJpaEntity, Long> {

    // ❌ Query retorna DTO de resposta HTTP
    @Query("""
    SELECT new com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse(
        t.categoria.nome, SUM(t.valor))
    FROM TransacaoJpaEntity t
    WHERE t.usuario.id = :usuarioId
    GROUP BY t.categoria.nome
    """)
    List<GraficoResponse> gerarGraficoPorCategoria(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
}
```

**Por que é um problema?**
- Adapter de **saída** (persistence) depende de adapter de **entrada** (web)
- DTOs HTTP não deveriam estar em queries de banco
- Cria ciclo: `adapter.out ← → adapter.in`
- Mistura responsabilidades: persistência conhece estrutura de API

---

### ✅ Solução: Criar Domain Models para Analytics

#### Passo 1: Criar Value Objects no Domain

**Arquivo**: `domain/model/grafico/GraficoCategoria.java` (JÁ EXISTE - usar)

```java
package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import java.math.BigDecimal;

/**
 * Representa dados de gráfico agregados por categoria.
 * Conceito puro de domínio.
 */
public record GraficoCategoria(
    String nomeCategoria,
    BigDecimal valorTotal
) {
    public GraficoCategoria {
        if (nomeCategoria == null || nomeCategoria.isBlank()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }
        if (valorTotal == null) {
            throw new IllegalArgumentException("Valor total é obrigatório");
        }
    }
}
```

**Arquivo**: `domain/model/grafico/EvolucaoFinanceira.java` (JÁ EXISTE - usar)

```java
package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import java.math.BigDecimal;

public record EvolucaoFinanceira(
    String periodo,        // Ex: "01/2025"
    BigDecimal receitas,
    BigDecimal despesas
) {
    public BigDecimal saldo() {
        return receitas.subtract(despesas);
    }
}
```

**Arquivo**: `domain/model/grafico/ResumoFinanceiro.java` (JÁ EXISTE - usar)

```java
package com.vinicius.gerenciamento_financeiro.domain.model.grafico;

import java.math.BigDecimal;

public record ResumoFinanceiro(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldoFinal
) {
    public static ResumoFinanceiro vazio() {
        return new ResumoFinanceiro(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
    }
}
```

#### Passo 2: Criar Port de Saída para Gráficos

**Arquivo**: `port/out/grafico/GraficoRepository.java` (CRIAR)

```java
package com.vinicius.gerenciamento_financeiro.port.out.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Port para consultas analíticas e geração de gráficos.
 */
public interface GraficoRepository {

    List<GraficoCategoria> buscarTotalPorCategoria(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    );

    List<GraficoCategoria> buscarTotalPorCategoriaDespesa(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    );

    List<GraficoCategoria> buscarTotalPorCategoriaReceita(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    );

    List<EvolucaoFinanceira> buscarEvolucaoFinanceira(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    );

    ResumoFinanceiro buscarResumoFinanceiro(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    );
}
```

#### Passo 3: Refatorar JpaGraficoRepository

**Arquivo**: `adapter/out/grafico/JpaGraficoRepositorySpring.java` (RENOMEAR)

```java
package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository Spring Data JPA para queries de gráficos.
 * Retorna DTOs internos de projeção (não domain, não response).
 */
@Repository
public interface JpaGraficoRepositorySpring extends CrudRepository<TransacaoJpaEntity, Long> {

    /**
     * DTO interno para projeção de gráfico por categoria.
     */
    record GraficoProjecao(String nomeCategoria, java.math.BigDecimal valorTotal) {}

    @Query("""
    SELECT new com.vinicius.gerenciamento_financeiro.adapter.out.grafico.JpaGraficoRepositorySpring$GraficoProjecao(
        t.categoria.nome, SUM(t.valor))
    FROM TransacaoJpaEntity t
    WHERE t.usuario.id = :usuarioId
      AND t.data >= :dataInicio
      AND t.data <= :dataFim
    GROUP BY t.categoria.nome
    ORDER BY SUM(t.valor) DESC
    """)
    List<GraficoProjecao> gerarGraficoPorCategoria(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    // DTO para evolução financeira
    record EvolucaoProjecao(
        String periodo,
        java.math.BigDecimal receitas,
        java.math.BigDecimal despesas
    ) {}

    @Query("""
    SELECT new com.vinicius.gerenciamento_financeiro.adapter.out.grafico.JpaGraficoRepositorySpring$EvolucaoProjecao(
        CONCAT(FUNCTION('MONTH', t.data), '/', FUNCTION('YEAR', t.data)),
        SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.RECEITA THEN t.valor ELSE 0 END),
        SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.DESPESA THEN t.valor ELSE 0 END))
    FROM TransacaoJpaEntity t
    WHERE t.usuario.id = :usuarioId
      AND t.data >= :dataInicio
      AND t.data <= :dataFim
    GROUP BY FUNCTION('YEAR', t.data), FUNCTION('MONTH', t.data)
    ORDER BY FUNCTION('YEAR', t.data), FUNCTION('MONTH', t.data)
    """)
    List<EvolucaoProjecao> gerarEvolucaoFinanceiraMensal(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );

    // DTO para resumo financeiro
    record ResumoProjecao(
        java.math.BigDecimal totalReceitas,
        java.math.BigDecimal totalDespesas,
        java.math.BigDecimal saldoFinal
    ) {}

    @Query("""
    SELECT new com.vinicius.gerenciamento_financeiro.adapter.out.grafico.JpaGraficoRepositorySpring$ResumoProjecao(
        SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.RECEITA THEN t.valor ELSE 0 END),
        SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.DESPESA THEN t.valor ELSE 0 END),
        (SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.RECEITA THEN t.valor ELSE 0 END) -
         SUM(CASE WHEN t.tipo = com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacaoEntity.DESPESA THEN t.valor ELSE 0 END)))
    FROM TransacaoJpaEntity t
    WHERE t.usuario.id = :usuarioId
        AND (CAST(:dataInicio AS TIMESTAMP) IS NULL OR t.data >= :dataInicio)
        AND (CAST(:dataFim AS TIMESTAMP) IS NULL OR t.data <= :dataFim)
    """)
    ResumoProjecao gerarResumoFinanceiro(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicio") LocalDateTime dataInicio,
        @Param("dataFim") LocalDateTime dataFim
    );
}
```

#### Passo 4: Criar Adapter que Implementa Port

**Arquivo**: `adapter/out/grafico/GraficoPersistenceAdapter.java` (REFATORAR)

```java
package com.vinicius.gerenciamento_financeiro.adapter.out.grafico;

import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.grafico.GraficoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Adapter que implementa o port GraficoRepository usando JPA.
 * Converte projeções JPA → Domain models.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GraficoPersistenceAdapter implements GraficoRepository {

    private final JpaGraficoRepositorySpring jpaRepository;

    @Override
    public List<GraficoCategoria> buscarTotalPorCategoria(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    ) {
        log.debug("Buscando gráfico por categoria para usuário: {}", usuarioId.getValue());

        List<JpaGraficoRepositorySpring.GraficoProjecao> projecoes =
            jpaRepository.gerarGraficoPorCategoria(
                usuarioId.getValue(),
                toLocalDateTime(dataInicio),
                toLocalDateTime(dataFim)
            );

        // ✅ Mapeia projeção JPA → Domain
        return projecoes.stream()
            .map(p -> new GraficoCategoria(p.nomeCategoria(), p.valorTotal()))
            .toList();
    }

    @Override
    public List<EvolucaoFinanceira> buscarEvolucaoFinanceira(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    ) {
        log.debug("Buscando evolução financeira para usuário: {}", usuarioId.getValue());

        List<JpaGraficoRepositorySpring.EvolucaoProjecao> projecoes =
            jpaRepository.gerarEvolucaoFinanceiraMensal(
                usuarioId.getValue(),
                toLocalDateTime(dataInicio),
                toLocalDateTime(dataFim)
            );

        return projecoes.stream()
            .map(p -> new EvolucaoFinanceira(p.periodo(), p.receitas(), p.despesas()))
            .toList();
    }

    @Override
    public ResumoFinanceiro buscarResumoFinanceiro(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    ) {
        log.debug("Buscando resumo financeiro para usuário: {}", usuarioId.getValue());

        JpaGraficoRepositorySpring.ResumoProjecao projecao =
            jpaRepository.gerarResumoFinanceiro(
                usuarioId.getValue(),
                toLocalDateTime(dataInicio),
                toLocalDateTime(dataFim)
            );

        if (projecao == null) {
            return ResumoFinanceiro.vazio();
        }

        return new ResumoFinanceiro(
            projecao.totalReceitas(),
            projecao.totalDespesas(),
            projecao.saldoFinal()
        );
    }

    // Métodos auxiliares
    private LocalDateTime toLocalDateTime(ZonedDateTime zdt) {
        return zdt != null ? zdt.toLocalDateTime() : null;
    }

    @Override
    public List<GraficoCategoria> buscarTotalPorCategoriaDespesa(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    ) {
        // Similar ao buscarTotalPorCategoria, mas com filtro de despesas
        // TODO: Implementar query específica
        throw new UnsupportedOperationException("A implementar");
    }

    @Override
    public List<GraficoCategoria> buscarTotalPorCategoriaReceita(
        UsuarioId usuarioId,
        ZonedDateTime dataInicio,
        ZonedDateTime dataFim
    ) {
        // Similar ao buscarTotalPorCategoria, mas com filtro de receitas
        // TODO: Implementar query específica
        throw new UnsupportedOperationException("A implementar");
    }
}
```

#### Passo 5: Atualizar Controller para Mapear Domain → Response

**Arquivo**: `adapter/in/web/controller/grafico/GraficoController.java` (ATUALIZADO)

```java
package com.vinicius.gerenciamento_financeiro.adapter.in.web.controller.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.ApiResponseSistema;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.grafico.GraficoResponseMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import com.vinicius.gerenciamento_financeiro.port.in.GerarGraficoUseCase;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/graficos")
@RequiredArgsConstructor
public class GraficoController {

    private final GerarGraficoUseCase gerarGraficoUseCase;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
    private final GraficoResponseMapper responseMapper; // ✅ Mapper domain → DTO

    @GetMapping("/categoria")
    public ApiResponseSistema<List<GraficoResponse>> obterGraficoPorCategoria(
        @RequestParam(required = false) ZonedDateTime dataInicio,
        @RequestParam(required = false) ZonedDateTime dataFim
    ) {
        // ✅ Use case retorna domain models
        List<GraficoCategoria> graficos = gerarGraficoUseCase
            .gerarGraficoTotalPorCategoria(dataInicio, dataFim);

        // ✅ Mapper converte domain → response DTO
        List<GraficoResponse> response = graficos.stream()
            .map(responseMapper::toResponse)
            .toList();

        return ApiResponseSistema.sucesso(response);
    }

    @GetMapping("/resumo")
    public ApiResponseSistema<ResumoFinanceiroResponse> obterResumoFinanceiro(
        @RequestParam(required = false) ZonedDateTime dataInicio,
        @RequestParam(required = false) ZonedDateTime dataFim
    ) {
        ResumoFinanceiro resumo = gerarGraficoUseCase
            .gerarResumoFinanceiro(dataInicio, dataFim);

        ResumoFinanceiroResponse response = responseMapper.toResponse(resumo);

        return ApiResponseSistema.sucesso(response);
    }
}
```

#### Passo 6: Implementar Mapper Domain → Response

**Arquivo**: `adapter/in/web/mapper/grafico/GraficoResponseMapper.java` (ATUALIZADO)

```java
package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.grafico;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.GraficoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.ResumoFinanceiroResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.grafico.TransacaoPorPeriodoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.EvolucaoFinanceira;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.GraficoCategoria;
import com.vinicius.gerenciamento_financeiro.domain.model.grafico.ResumoFinanceiro;
import org.springframework.stereotype.Component;

@Component
public class GraficoResponseMapper {

    /**
     * Converte domain → DTO de resposta HTTP.
     */
    public GraficoResponse toResponse(GraficoCategoria domain) {
        return new GraficoResponse(
            domain.nomeCategoria(),
            domain.valorTotal()
        );
    }

    public ResumoFinanceiroResponse toResponse(ResumoFinanceiro domain) {
        return new ResumoFinanceiroResponse(
            domain.totalReceitas(),
            domain.totalDespesas(),
            domain.saldoFinal()
        );
    }

    public TransacaoPorPeriodoResponse toResponse(EvolucaoFinanceira domain) {
        return new TransacaoPorPeriodoResponse(
            domain.periodo(),
            domain.receitas(),
            domain.despesas()
        );
    }
}
```

---

### 📊 Fluxo Correto Após Refatoração

```
┌─────────────────┐
│   Controller    │  (Adapter IN)
│  GraficoCtrl    │
└────────┬────────┘
         │ chama
         ▼
┌─────────────────┐
│   Use Case      │  (Application)
│ GerarGrafico    │
└────────┬────────┘
         │ chama
         ▼
┌─────────────────┐
│      Port       │  (Interface)
│ GraficoRepo     │
└────────┬────────┘
         │ impl
         ▼
┌─────────────────┐
│    Adapter      │  (Adapter OUT)
│ GraficoPersist  │
│   Adapter       │
└────────┬────────┘
         │ usa
         ▼
┌─────────────────┐       ┌──────────────┐
│ JpaGraficoRepo  │──────→│  Projeção    │
│    (Spring)     │       │   Interna    │
└─────────────────┘       └──────────────┘
         │ consulta
         ▼
┌─────────────────┐
│    Database     │
└─────────────────┘

CONVERSÕES:
1. Controller: Domain → Response DTO (via mapper)
2. Adapter: Projeção JPA → Domain (GraficoPersistenceAdapter)
3. Use Case: Trabalha 100% com Domain
```

---

## 🎯 Estratégias Gerais de Refatoração

### 1. **Dependency Inversion Principle (DIP)**

**Problema**: Módulo de alto nível depende de módulo de baixo nível.

**Solução**: Ambos dependem de abstração.

```
❌ ERRADO:
Application → Adapter (concreto)

✅ CORRETO:
Application → Port (abstração) ← Adapter (implementa)
```

### 2. **Domain-Driven Design (DDD)**

**Princípio**: Conceitos de negócio devem estar no domain.

**Perguntas-chave**:
- Esse conceito existe independente de HTTP/JPA/RabbitMQ?
- Um especialista de negócio entenderia esse termo?
- Posso explicar isso sem falar de tecnologia?

**Se SIM para todas**: Deve estar no domain!

### 3. **Padrão Adapter**

Cada adapter deve fazer UMA coisa:
- `adapter.in` = Traduz mundo externo → domain
- `adapter.out` = Traduz domain → mundo externo

Nunca:
- ❌ `adapter.in` conversando com `adapter.out`
- ❌ `adapter` expondo DTOs internos para fora

---

## 🛠️ Ferramentas de Validação

### ArchUnit Tests

O projeto já tem testes arquiteturais. Após refatorações, execute:

```bash
./mvnw -Dtest=HexagonalArchitectureTest test
```

**Regras validadas**:
- ✅ Domain não depende de nada
- ✅ Ports dependem apenas de domain
- ✅ Application não depende de adapters
- ✅ Sem ciclos entre camadas

---

## 📝 Checklist de Refatoração

### Antes de Começar
- [ ] Identifique o import problemático
- [ ] Entenda por que ele existe
- [ ] Verifique se há testes dependendo disso

### Durante Refatoração
- [ ] Crie conceito no domain (se necessário)
- [ ] Atualize port para usar domain
- [ ] Refatore application service
- [ ] Atualize adapters
- [ ] Crie/atualize mappers

### Após Refatoração
- [ ] Execute ArchUnit tests
- [ ] Execute testes unitários
- [ ] Execute testes de integração
- [ ] Commit com mensagem clara (ex: `refactor: remove circular dependency application→adapter`)

---

## 🎓 Aprendizados-Chave

1. **Ports são contratos puros**: Sem imports de `adapter.*`, apenas `domain.*`

2. **Application é orquestrador**: Coordena domain + ports, nunca conhece adapters

3. **Adapters traduzem**: Mundo externo ↔ Domain, cada um na sua direção

4. **Domain é o núcleo**: Se você precisa de uma constante/conceito em vários lugares, provavelmente pertence ao domain

5. **DTOs têm lugar certo**:
   - **Request/Response DTOs** → `adapter.in` (HTTP)
   - **Projeções JPA** → Internas ao `adapter.out` (não vazam)
   - **Domain Models** → `domain.model` (usados em toda aplicação)

---

## 📚 Referências

- [Hexagonal Architecture (Alistair Cockburn)](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture (Robert C. Martin)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design (Eric Evans)](https://www.domainlanguage.com/ddd/)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html)

---

**Documento criado em**: 2025-10-23
**Última atualização**: 2025-10-23
**Autor**: Claude (Análise automatizada)
**Status**: ✅ Pronto para uso
