package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
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
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransacaoJpaMapper {

    public TransacaoJpaEntity toJpaEntity(
            Transacao transacao,
            UsuarioJpaEntity usuarioJpa,
            CategoriaJpaEntity categoriaJpa,
            MoedaJpaEntity moedaJpa) {

        if (transacao == null) {
            return null;
        }

        // Cria o MontanteMonetarioJpaEntity
        MontanteMonetarioJpaEntity montanteJpa = new MontanteMonetarioJpaEntity(
                transacao.getMontante().getValor(),
                moedaJpa
        );

        TransacaoJpaEntity jpaEntity = TransacaoJpaEntity.builder()
                .id(transacao.getId() != null ? transacao.getId().getValue() : null)
                .descricao(transacao.getDescricao())
                .valor(transacao.getValor())
                .montante(montanteJpa)
                .tipo(transacao.getTipo())
                .data(transacao.getData())
                .observacao(transacao.getObservacoes())
                .usuario(usuarioJpa)
                .categoria(categoriaJpa)
                .auditoria(mapAuditoriaToJpa(transacao.getAuditoria()))
                .build();

        mapConfiguracaoToJpa(transacao.getConfiguracao(), jpaEntity);

        return jpaEntity;
    }

    public Transacao toDomainEntity(TransacaoJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        ConfiguracaoTransacao configuracao = mapConfiguracaoToDomain(jpaEntity);
        Auditoria auditoria = mapAuditoriaToDomain(jpaEntity.getAuditoria());

        MontanteMonetario montante = MontanteMonetario.of(
                jpaEntity.getMontante().getValor(),
                MoedaId.of(jpaEntity.getMontante().getMoeda().getCodigo())
        );

        if (jpaEntity.getId() != null) {
            return Transacao.reconstituir(
                    jpaEntity.getId(),
                    jpaEntity.getDescricao(),
                    montante,
                    jpaEntity.getTipo(),
                    jpaEntity.getData(),
                    UsuarioId.of(jpaEntity.getUsuario().getId()),
                    CategoriaId.of(jpaEntity.getCategoria().getId()),
                    ClienteId.of(jpaEntity.getUsuario().getId()),
                    configuracao,
                    auditoria,
                    jpaEntity.getObservacao()
            );
        } else {
            return Transacao.criarNova(
                    jpaEntity.getDescricao(),
                    montante,
                    jpaEntity.getTipo(),
                    jpaEntity.getData(),
                    CategoriaId.of(jpaEntity.getCategoria().getId()),
                    UsuarioId.of(jpaEntity.getUsuario().getId()),
                    ClienteId.of(jpaEntity.getUsuario().getId()),
                    configuracao,
                    jpaEntity.getObservacao()
            );
        }
    }

    public void updateJpaEntity(
            TransacaoJpaEntity jpaEntity,
            Transacao transacao,
            MoedaJpaEntity moedaJpa) {

        if (jpaEntity == null || transacao == null) {
            return;
        }

        jpaEntity.setDescricao(transacao.getDescricao());
        jpaEntity.setValor(transacao.getValor());

        MontanteMonetarioJpaEntity montanteJpa = new MontanteMonetarioJpaEntity(
                transacao.getMontante().getValor(),
                moedaJpa
        );
        jpaEntity.setMontante(montanteJpa);

        jpaEntity.setTipo(transacao.getTipo());
        jpaEntity.setData(transacao.getData());

        mapConfiguracaoToJpa(transacao.getConfiguracao(), jpaEntity);

        if (jpaEntity.getAuditoria() == null) {
            jpaEntity.setAuditoria(new AuditoriaJpa());
        }
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