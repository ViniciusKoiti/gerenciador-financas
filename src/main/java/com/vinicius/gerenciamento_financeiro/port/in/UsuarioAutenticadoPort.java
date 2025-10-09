package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.ContextoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

public interface UsuarioAutenticadoPort {

    UsuarioId obterUsuarioAtual();

    boolean temPermissao(String recurso, String acao);
    boolean ehProprietario(UsuarioId proprietarioId);


    ContextoUsuario obterContextoParaLogs();

    boolean ehProprietarioDaCategoria(CategoriaId categoriaId);
}