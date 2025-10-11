package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

/**
 * Port de entrada para operacoes de Usuario.
 * Define contratos usando APENAS tipos de dominio.
 */
public interface UsuarioService  {
    /**
     * Registra um novo usuario no sistema
     */
    Usuario registrar(Usuario usuario);

    /**
     * Busca usuario por ID
     */
    Usuario buscarPorId(UsuarioId id);

    /**
     * Busca usuario por email
     */
    Usuario buscarPorEmail(Email email);

    /**
     * Atualiza dados do usuario
     */
    Usuario atualizar(Usuario usuario);
}