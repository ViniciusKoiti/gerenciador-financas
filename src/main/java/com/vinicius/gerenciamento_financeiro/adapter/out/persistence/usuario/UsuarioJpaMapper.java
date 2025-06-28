package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.auditoria.AuditoriaJpa;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UsuarioJpaMapper {

    public UsuarioJpaEntity toJpaEntity(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        return UsuarioJpaEntity.builder()
                .id(usuario.getId() != null ? usuario.getId().getValue() : null)
                .email(usuario.getEmail())
                .senha(usuario.getHashSenha())
                .nome(usuario.getNome())
                .auditoria(mapAuditoriaToJpa(usuario.getAuditoria()))
                .build();
    }
    public Usuario toDomainEntity(UsuarioJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Set<CategoriaId> categoriaIds = jpaEntity.getCategorias() != null ?
                jpaEntity.getCategorias().stream()
                        .map(cat -> CategoriaId.of(cat.getId()))
                        .collect(Collectors.toSet()) :
                Set.of();

        if (jpaEntity.getId() != null) {
            return Usuario.reconstituir(
                    jpaEntity.getId(),
                    jpaEntity.getNome(),
                    jpaEntity.getEmail(),
                    jpaEntity.getSenha(),
                    mapAuditoriaToDomain(jpaEntity.getAuditoria()),
                    categoriaIds
            );
        } else {
            // Usuário novo (sem ID)
            return Usuario.criarNovo(
                    jpaEntity.getNome(),
                    jpaEntity.getEmail(),
                    jpaEntity.getSenha()
            );
        }
    }

    /**
     * Atualiza uma entidade JPA existente com dados do domínio
     */
    public void updateJpaEntity(UsuarioJpaEntity jpaEntity, Usuario usuario) {
        if (jpaEntity == null || usuario == null) {
            return;
        }

        jpaEntity.setEmail(usuario.getEmail());
        jpaEntity.setNome(usuario.getNome());
        jpaEntity.setSenha(usuario.getHashSenha());

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
        if (auditoriaJpa == null) {
            return Auditoria.criarNova();
        }

        if (auditoriaJpa.getCriadoEm() != null) {
            return Auditoria.reconstituir(
                    auditoriaJpa.getCriadoEm(),
                    auditoriaJpa.getAtualizadoEm()
            );
        }

        return Auditoria.criarNova();
    }
}