package com.vinicius.gerenciamento_financeiro.port.out.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(UsuarioId id);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAll();

    void deleteById(UsuarioId id);

    Boolean existsByEmail(String email);
}
