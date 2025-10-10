package com.vinicius.gerenciamento_financeiro.port.out.autorizacao;

import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;

public interface TokenService {

    String extrairUsername(String token);
    Long extrairUsuarioId(String token);
    boolean validarToken(String token, String username);
    String gerarToken(Usuario usuario);
}
