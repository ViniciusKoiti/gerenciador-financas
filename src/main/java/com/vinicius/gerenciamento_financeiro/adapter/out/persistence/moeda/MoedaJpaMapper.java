package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity.MoedaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.Moeda;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import org.springframework.stereotype.Component;

@Component
public class MoedaJpaMapper {

    public MoedaJpaEntity toJpaEntity(Moeda moeda) {
        if (moeda == null) {
            return null;
        }

        return MoedaJpaEntity.builder()
                .id(moeda.getId() != null ? Long.valueOf(moeda.getId().getValor()) : null)
                .codigo(moeda.getCodigo())
                .simbolo(moeda.getSimbolo())
                .nome(moeda.getNome())
                .casasDecimais(moeda.getCasasDecimais())
                .ativo(moeda.isAtivo())
                .auditoria(mapAuditoriaToJpa(moeda.getAuditoria()))
                .build();
    }

    public Moeda toDomainEntity(MoedaJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Auditoria auditoria = mapAuditoriaToDomain(jpaEntity.getAuditoria());

        if (jpaEntity.getId() != null) {
            return Moeda.reconstituir(
                    jpaEntity.getCodigo(),
                    jpaEntity.getSimbolo(),
                    jpaEntity.getNome(),
                    jpaEntity.getCasasDecimais(),
                    jpaEntity.getAtivo(),
                    auditoria
            );
        } else {
            return Moeda.criar(
                    jpaEntity.getCodigo(),
                    jpaEntity.getSimbolo(),
                    jpaEntity.getNome(),
                    jpaEntity.getCasasDecimais()
            );
        }
    }

    public void updateJpaEntity(MoedaJpaEntity jpaEntity, Moeda moeda) {
        if (jpaEntity == null || moeda == null) {
            return;
        }

        jpaEntity.setCodigo(moeda.getCodigo());
        jpaEntity.setSimbolo(moeda.getSimbolo());
        jpaEntity.setNome(moeda.getNome());
        jpaEntity.setCasasDecimais(moeda.getCasasDecimais());
        jpaEntity.setAtivo(moeda.isAtivo());

        if (jpaEntity.getAuditoria() == null) {
            jpaEntity.setAuditoria(new AuditoriaJpa());
        }
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