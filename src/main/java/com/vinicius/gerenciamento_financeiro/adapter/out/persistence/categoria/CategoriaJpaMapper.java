package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoriaJpaMapper {

    public CategoriaJpaEntity toJpaEntity(Categoria categoria, UsuarioJpaEntity usuarioJpa) {
        if (categoria == null) {
            return null;
        }

        return CategoriaJpaEntity.builder()
                .id(categoria.getId() != null ? categoria.getId().getValue() : null)
                .nome(categoria.getNome())
                .descricao(categoria.getDescricao())
                .ativa(categoria.isAtiva())
                .icone(categoria.getIcone())
                .usuario(usuarioJpa)
                .auditoria(mapAuditoriaToJpa(categoria.getCriadoEm(), categoria.getAtualizadoEm()))
                // TODO: Mapear categoria pai e subcategorias se necessário
                .build();
    }
    public Categoria toDomainEntity(CategoriaJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        // Mapear subcategorias se existirem
        Set<CategoriaId> subcategoriaIds = jpaEntity.getSubcategorias() != null ?
                jpaEntity.getSubcategorias().stream()
                        .map(sub -> CategoriaId.of(sub.getId()))
                        .collect(Collectors.toSet()) :
                Set.of();

        CategoriaId categoriaPaiId = jpaEntity.getCategoriaJpaEntityPai() != null ?
                CategoriaId.of(jpaEntity.getCategoriaJpaEntityPai().getId()) : null;

        return Categoria.reconstituir(
                CategoriaId.of(jpaEntity.getId()),
                jpaEntity.getNome(),
                jpaEntity.getDescricao(),
                jpaEntity.getAtiva(),
                jpaEntity.getIcone(),
                UsuarioId.of(jpaEntity.getUsuario().getId()),
                jpaEntity.getAuditoria() != null ? jpaEntity.getAuditoria().getCriadoEm() : null,
                jpaEntity.getAuditoria() != null ? jpaEntity.getAuditoria().getAtualizadoEm() : null,
                categoriaPaiId,
                subcategoriaIds
        );
    }
    public void updateJpaEntity(CategoriaJpaEntity jpaEntity, Categoria categoria) {
        if (jpaEntity == null || categoria == null) {
            return;
        }

        jpaEntity.setNome(categoria.getNome());
        jpaEntity.setDescricao(categoria.getDescricao());
        jpaEntity.setAtiva(categoria.isAtiva());
        jpaEntity.setIcone(categoria.getIcone());

        if (jpaEntity.getAuditoria() == null) {
            jpaEntity.setAuditoria(new AuditoriaJpa());
        }
    }

    private AuditoriaJpa mapAuditoriaToJpa(java.time.LocalDateTime criadoEm, java.time.LocalDateTime atualizadoEm) {
        AuditoriaJpa auditoriaJpa = new AuditoriaJpa();
        auditoriaJpa.setCriadoEm(criadoEm);
        auditoriaJpa.setAtualizadoEm(atualizadoEm);
        return auditoriaJpa;
    }
}