package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;

public interface UsuarioService  {
    UsuarioResponse save(UsuarioPost usuarioPost);
    UsuarioResponse buscarPorId(Long id);
}
