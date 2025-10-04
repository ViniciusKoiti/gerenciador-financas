package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity.MoedaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.MontanteMonetarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.TransacaoJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransacaoJpaMapper {

    private final EntityManager entityManager;

    public TransacaoJpaMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public TransacaoJpaEntity toJpaEntity(Transacao transacao) {
        if (transacao == null) return null;

        UsuarioJpaEntity usuarioRef = entityManager.getReference(
                UsuarioJpaEntity.class,
                transacao.getUsuarioId().getValue()
        );

        CategoriaJpaEntity categoriaRef = entityManager.getReference(
                CategoriaJpaEntity.class,
                transacao.getCategoriaId().getValue()
        );

        MoedaJpaEntity moedaRef = entityManager.getReference(
                MoedaJpaEntity.class,
                transacao.getMontante().getMoedaId().getValor()
        );

        MontanteMonetarioJpaEntity montanteJpa = new MontanteMonetarioJpaEntity(
                transacao.getMontante().getValor(),
                moedaRef
        );

        ClienteJpaEntity clienteRef = null;
        if (transacao.getClienteId() != null) {
            clienteRef = entityManager.getReference(
                    ClienteJpaEntity.class,
                    transacao.getClienteId().getValue()
            );
        }

        return TransacaoJpaEntity.builder()
                .id(transacao.getId() != null ? transacao.getId().getValue() : null)
                .descricao(transacao.getDescricao())
                .valor(transacao.getValor())
                .montante(montanteJpa)
                .tipo(transacao.getTipo())
                .data(transacao.getData())
                .observacao(transacao.getObservacoes())
                .usuario(usuarioRef)
                .categoria(categoriaRef)
                .cliente(clienteRef)
                .auditoria(mapAuditoriaToJpa(transacao.getAuditoria()))
                .build();
    }

    public Transacao toDomainEntity(TransacaoJpaEntity jpa) {
        if (jpa == null) return null;

        MontanteMonetario montante = MontanteMonetario.of(
                jpa.getMontante().getValor(),
                MoedaId.of(jpa.getMontante().getMoeda().getCodigo())
        );

        ClienteId clienteId = jpa.getCliente() != null
                ? ClienteId.of(jpa.getCliente().getId())
                : null;

        return Transacao.reconstituir(
                TransacaoId.of(jpa.getId()), // ✅ Value Object, não Long
                jpa.getDescricao(),
                montante,
                jpa.getTipo(),
                jpa.getData(),
                UsuarioId.of(jpa.getUsuario().getId()),
                CategoriaId.of(jpa.getCategoria().getId()),
                clienteId, // ✅ ClienteId correto ou null
                mapConfiguracaoToDomain(jpa),
                mapAuditoriaToDomain(jpa.getAuditoria()),
                jpa.getObservacao()
        );
    }


    private void mapConfiguracaoToJpa(ConfiguracaoTransacao config, TransacaoJpaEntity jpaEntity) {
        if (config == null) {
            return;
        }

        jpaEntity.setPago(config.getPago());
        jpaEntity.setRecorrente(config.getRecorrente());
        jpaEntity.setPeriodicidade(config.getPeriodicidade());
        jpaEntity.setTipoRecorrencia(config.getTipoRecorrencia());
        jpaEntity.setIgnorarLimiteCategoria(config.getIgnorarLimiteCategoria());
        jpaEntity.setIgnorarOrcamento(config.getIgnorarOrcamento());
        jpaEntity.setParcelado(config.getParcelado());
        jpaEntity.setDataPagamento(config.getDataPagamento());
        jpaEntity.setDataVencimento(config.getDataVencimento());
    }

    private ConfiguracaoTransacao mapConfiguracaoToDomain(TransacaoJpaEntity jpaEntity) {
        return new ConfiguracaoTransacao.Builder()
                .pago(jpaEntity.getPago())
                .recorrente(jpaEntity.getRecorrente())
                .periodicidade(jpaEntity.getPeriodicidade())
                .tipoRecorrencia(jpaEntity.getTipoRecorrencia())
                .ignorarLimiteCategoria(jpaEntity.getIgnorarLimiteCategoria())
                .ignorarOrcamento(jpaEntity.getIgnorarOrcamento())
                .parcelado(jpaEntity.getParcelado())
                .dataPagamento(jpaEntity.getDataPagamento())
                .dataVencimento(jpaEntity.getDataVencimento())
                .build();
    }

    private AuditoriaJpa mapAuditoriaToJpa(Auditoria auditoria) {
        if (auditoria == null) {
            return new AuditoriaJpa();
        }

        AuditoriaJpa auditoriaJpa = new AuditoriaJpa();
        auditoriaJpa.setCriadoEm(auditoria.getCriadoEm());
        auditoriaJpa.setAtualizadoEm(auditoria.getAtualizadoEm());
        return auditoriaJpa;
    }

    private Auditoria mapAuditoriaToDomain(AuditoriaJpa auditoriaJpa) {
        if (auditoriaJpa == null || auditoriaJpa.getCriadoEm() == null) {
            return Auditoria.criarNova();
        }

        return Auditoria.reconstituir(
                auditoriaJpa.getCriadoEm(),
                auditoriaJpa.getAtualizadoEm()
        );
    }
}