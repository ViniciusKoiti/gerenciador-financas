package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;

public interface UsuarioService  {
    AuthenticationResponse save(UsuarioPost usuarioPost);
    UsuarioResponse findById(Long id);
}
