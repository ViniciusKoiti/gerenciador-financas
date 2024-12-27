package com.vinicius.gerenciamento_financeiro.port.out.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario entity);
    Optional<Usuario> findById(Long id);
    List<Usuario> findAll();
    void deleteById(Long id);

    Boolean existsByEmail(String email);
    Optional<Usuario> findByEmail(String email);
}
