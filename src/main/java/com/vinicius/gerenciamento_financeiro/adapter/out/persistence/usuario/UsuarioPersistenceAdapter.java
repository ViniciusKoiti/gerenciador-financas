package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaUsuarioRepository;
    private final UsuarioJpaMapper usuarioJpaMapper;

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        try {
            log.debug("Salvando usuário: {}", usuario.getEmail());

            UsuarioJpaEntity jpaEntity;

            if (usuario.isNovo()) {
                jpaEntity = usuarioJpaMapper.toJpaEntity(usuario);
                log.debug("Criando novo usuário: {}", usuario.getEmail());
            } else {
                jpaEntity = jpaUsuarioRepository.findById(usuario.getId().getValue())
                        .orElse(usuarioJpaMapper.toJpaEntity(usuario));

                usuarioJpaMapper.updateJpaEntity(jpaEntity, usuario);
                log.debug("Atualizando usuário existente: ID {}", usuario.getId().getValue());
            }

            UsuarioJpaEntity savedEntity = jpaUsuarioRepository.save(jpaEntity);
            Usuario usuarioSalvo = usuarioJpaMapper.toDomainEntity(savedEntity);

            log.debug("Usuário salvo com sucesso: ID {}", savedEntity.getId());
            return usuarioSalvo;

        } catch (Exception e) {
            log.error("Erro ao salvar usuário: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(UsuarioId id) {
        log.debug("Buscando usuário por ID: {}", id.getValue());

        return jpaUsuarioRepository.findById(id.getValue())
                .map(usuarioJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        log.debug("Buscando usuário por email: {}", email);

        return jpaUsuarioRepository.findByEmailIgnoreCase(email)
                .map(usuarioJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        log.debug("Buscando todos os usuários");

        return jpaUsuarioRepository.findAll()
                .stream()
                .map(usuarioJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(UsuarioId id) {
        log.debug("Deletando usuário: ID {}", id.getValue());

        if (!jpaUsuarioRepository.existsById(id.getValue())) {
            log.warn("Tentativa de deletar usuário inexistente: ID {}", id.getValue());
            return;
        }

        jpaUsuarioRepository.deleteById(id.getValue());
        log.info("Usuário deletado: ID {}", id.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        log.debug("Verificando se existe usuário com email: {}", email);

        return jpaUsuarioRepository.existsByEmail(email);
    }
    @Transactional(readOnly = true)
    public Optional<Usuario> findByIdWithCategorias(UsuarioId id) {
        log.debug("Buscando usuário com categorias: ID {}", id.getValue());

        return jpaUsuarioRepository.findByIdWithCategorias(id.getValue())
                .map(usuarioJpaMapper::toDomainEntity);
    }
    @Transactional(readOnly = true)
    public boolean isEmailInUseByOtherUser(String email, UsuarioId excludeUserId) {
        log.debug("Verificando se email {} está em uso por outro usuário (excluindo ID: {})",
                email, excludeUserId.getValue());

        return jpaUsuarioRepository.existsByEmailAndIdNot(email, excludeUserId.getValue());
    }
    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return jpaUsuarioRepository.countActiveUsers();
    }
}