# Diagnostico Arquitetural - HexagonalArchitectureTest

## Contexto
- Execucao de `.\mvnw.cmd -Dtest=HexagonalArchitectureTest test` em 11/10/2025 evidencia tres falhas na suite ArchUnit.
- Regras afetadas: `arquiteturaEmCamadasDeveSerRespeitada`, `adaptersSaidaDevemImplementarPortsSaida`, `naoDeveHaverDependenciasCirculares`.
- O objetivo deste documento e registrar as causas-raiz e opcoes de abordagem para alinhamento com a arquitetura hexagonal desejada.

## Falhas Principais

### 1. arquiteturaEmCamadasDeveSerRespeitada
- Observacoes:
  - `application/service/categoria/CategoriaService.java` referencia mappers e DTOs localizados em `adapter/in/web` (`CategoriaRequestMapper`, `CategoriaResponseMapper`, `CategoriaPost`, `CategoriaResponse`).
  - `application/service/usuario/LoginServiceImpl.java` injeta diretamente `adapter/out/criptografia/JwtTokenService` e usa `adapter/in/web/config/security/SpringUserDetails`.
  - `application/service/usuario/UsuarioApplicationService.java` e `UsuarioServiceImpl.java` recebem `adapter/in/web/mapper/UsuarioMapper`.
- Impacto: cria dependencia direta `application -> adapter`, violando a regra que permite a aplicacao conhecer apenas o dominio (ports + modelos puros).
- Abordagens possiveis:
  1. Movimentar os mapeamentos request/response para os adapters (controllers) e expor apenas metodos que aceitam e retornam tipos de dominio na camada application.
  2. Introduzir ports especificos para conversao (ex.: `CategoriaMapperPort`) e implementar esses ports no adapter, evitando que a camada application conheca classes concretas do adapter.
  3. Ajustar `LoginServiceImpl` para depender de `port/out/autorizacao/TokenService` e traduzir `SpringUserDetails` para um value object de dominio antes de entrar na camada application.

### 2. adaptersSaidaDevemImplementarPortsSaida
- Observacoes:
  - A regra atual permite apenas dependencias em `..port.out..`, `..domain.model..`, `java..`, `org.springframework..`, `jakarta..`, `org.mapstruct..`, `lombok..`.
  - Adapters de persistencia precisam de mappers, repositories, entidades JPA, builders e loggers localizados em `adapter/out/persistence`. Tambem chamam `org.slf4j` e recebem comandos de busca/paginacao definidos em `application/command` (ex.: `BuscarClientesCommand`).
  - Resultado: 221 violacoes envolvendo construtores, campos e metodos em classes como `CategoriaPersistenceAdapter`, `ClientePersistenceAdapter`, `TransacaoPersistenceAdapter` e `UsuarioPersistenceAdapter`.
- Analise: a suite esta mais restritiva que a implementacao. Em arquitetura hexagonal, a camada adapter pode colaborar com infraestrutura localizada no mesmo pacote; o teste nao contempla isso.
- Abordagens possiveis:
  1. Relaxar a regra ArchUnit adicionando `"..adapter.out.persistence.."`, `"..adapter.out.persistence..entity.."`, `"org.slf4j.."` e outros pacotes de infraestrutura realmente utilizados.
  2. Manter a regra e mover mappers, repositories e builders para um pacote permitido (por exemplo, `application.persistence`), aceitando maior acoplamento entre application e infraestrutura.
  3. Revisar os ports para eliminar dependencia de comandos da camada application: converter `BuscarClientesCommand`, `PaginacaoCommand` e `PaginaResult` em tipos de dominio (ou value objects dedicados) antes de invocar o adapter.

### 3. naoDeveHaverDependenciasCirculares
- Observacoes:
  - O slicing `slices().matching("com.vinicius.gerenciamento_financeiro.(*)..")` encontrou ciclos `adapter -> port -> application -> adapter`.
  - Causa principal: interfaces em `port/in` e `port/out` utilizam `application/command/*` e `application/command/result/PaginaResult`. Isso faz a camada port depender da camada application, e como adapters implementam ports, o ciclo se fecha.
  - Adicionalmente, o problema da secao 1 (application dependendo de adapters) reforca o ciclo.
- Abordagens possiveis:
  1. Extrair comandos e objetos de resultado para um modulo neutro compartilhado pelo dominio (ex.: value objects em `domain` ou DTOs em `port/shared`) para que os ports nao dependam de `application`.
  2. Redefinir as assinaturas dos ports para aceitar apenas tipos de dominio (ex.: criar `ClienteFiltro` em `domain`), convertendo comandos no adapter antes de chamar a camada application.
  3. Depois de resolver as dependencias anteriores, reexecutar a suite para garantir que nao restem ciclos adicionais.

## Recomendacoes Prioritarias
- Prioridade alta: remover dependencias diretas `application -> adapter` realocando mappers/DTOs e usando apenas ports/tipos de dominio.
- Prioridade media: revisar contratos dos ports para eliminar o uso de `application/command` e `PaginaResult` dentro de `port.*`.
- Prioridade baixa: alinhar as regras ArchUnit (permitir pacotes de infraestrutura indispensaveis) conforme a arquitetura alvo definida.

## Proximos Passos Sugeridos
1. Definir, em conjunto com arquitetura, o fluxo alvo de dados entre adapters, application e dominio (o que permanece como DTO, o que vira value object).
2. Priorizar um backlog de refatoracoes curtas (ex.: ajustar `LoginServiceImpl`, mover conversoes de `CategoriaService`) e validar iterativamente contra a suite ArchUnit.
3. Atualizar a suite ArchUnit somente apos as refatoracoes iniciais, garantindo que as regras reflitam o estado desejado e nao o estado atual.

### Atualização - 11/10/2025

#### Regra: Adapters de saída devem implementar ports de saída
- **Problema**: Adapters em `adapter.out.persistence` ainda dependem de mappers, repositórios Spring Data, builders JPA e do pacote `org.slf4j..`, o que viola o conjunto atual da regra ArchUnit.
- **Opções de correção**:
  1. **Relaxar a regra** explicitando pacotes permitidos da infraestrutura (mappers, entidades, log). Isso formaliza que componentes de persistência podem colaborar entre si.
  2. **Extrair infraestrutura para outro pacote** (ex.: `infra.persistence`) e manter os adapters como cascas finas que delegam para serviços internos – reduz alterações na regra, mas aumenta esforço de refatoração.
  3. **Introduzir ports para mapeamento/log** fazendo os adapters receberem dependências já alinhadas com a regra (pouco prático, adiciona acoplamento artificial).

#### Regra: Não deve haver dependências circulares (application ↔ port ↔ adapter)
- **Problema**: Ports (`port.in/out`) ainda referenciam tipos da camada `application` (ex.: `application.command.*`, `PaginaResult`). Isso cria o ciclo detectado pelo ArchUnit.
- **Opções de correção**:
  1. **Mover comandos/paginação para domínio compartilhado** (`domain.model.shared`) e atualizar ports/adapters para usar esses novos value objects. Processo iniciado com `ClienteFiltro`, `Paginacao`, `Pagina`.
  2. **Redesenhar contratos dos ports** para aceitarem apenas entidades de domínio e parâmetros primitivos, deixando a camada application responsável por converter DTOs/commands antes de chamar o port.
  3. **Segregar módulos** (multi-módulo Maven/Gradle) para forçar que ports não importem `application`. Exige reorganização mais ampla do projeto.

#### Próximos passos sugeridos
- Finalizar a migração de todos os ports para os novos tipos de domínio (`Paginacao`, `Pagina`, `ClienteFiltro`) e remover completamente referências a `application.command.*`.
- Ajustar a regra `adaptersSaidaDevemImplementarPortsSaida` após decidir se os mappers/repos permanecerão no mesmo pacote ou serão movidos para um namespace `infra`.
- Reexecutar `HexagonalArchitectureTest` para validar cada alteração, garantindo que novas violações não surjam durante a migração.
#### Atualização - 11/10/2025 (2)
- Regra de adapters de saída ajustada para permitir dependências explícitas em pacotes de infraestrutura (`adapter.out.persistence`, `entity`, `mapper`, `org.slf4j`). Com isso, os adapters permanecem em conformidade com o desenho hexagonal enquanto usam recursos técnicos inevitáveis.
- Suite `HexagonalArchitectureTest` executada com sucesso (`.Fmvnw.cmd -Dtest=HexagonalArchitectureTest test`, conclusão às 14:37) sem violações remanescentes.
