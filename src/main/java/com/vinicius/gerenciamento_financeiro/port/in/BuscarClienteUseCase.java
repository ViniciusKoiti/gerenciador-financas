package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Pagina;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Paginacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;

public interface BuscarClienteUseCase {

    Pagina<Cliente> buscarPaginadoComFiltros(
            ClienteFiltro filtro,
            Paginacao paginacao,
            UsuarioId usuarioId
    );

    Pagina<Cliente> buscarPaginado(Paginacao paginacao, UsuarioId usuarioId);

    List<Cliente> buscarComFiltros(ClienteFiltro filtro, UsuarioId usuarioId);

    Optional<Cliente> buscarPorId(ClienteId clienteId, UsuarioId usuarioId);

    boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    List<Cliente> buscarPorTexto(String texto, UsuarioId usuarioId);

    List<Cliente> buscarApenasAtivos(UsuarioId usuarioId);
}