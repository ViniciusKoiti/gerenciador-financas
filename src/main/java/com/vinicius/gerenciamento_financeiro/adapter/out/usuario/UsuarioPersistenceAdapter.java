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
    @Override
    public Usuario save(Usuario entity) {
        return null;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Usuario> findAll() {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
