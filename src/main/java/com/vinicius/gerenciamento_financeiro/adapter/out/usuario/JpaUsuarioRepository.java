package com.vinicius.gerenciamento_financeiro.adapter.out.usuario;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUsuarioRepository extends JpaRepository<Usuario, Long> {
}
