package com.vinicius.gerenciamento_financeiro.adapter.out.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UsuarioPersistenceAdapter implements UsuarioRepository {
    private final JpaUsuarioRepository jpaUsuarioRepository;

    @Override
    public Usuario save(Usuario entity) {
        return jpaUsuarioRepository.save(entity);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Usuario> findAll() {
        return jpaUsuarioRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        jpaUsuarioRepository.deleteById(id);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return jpaUsuarioRepository.existsByEmail(email);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaUsuarioRepository.findByEmail(email);
    }
}
