package com.vinicius.gerenciamento_financeiro.port.in;

import com.vinicius.gerenciamento_financeiro.application.command.PaginacaoCommand;
import com.vinicius.gerenciamento_financeiro.application.command.cliente.BuscarClientesCommand;
import com.vinicius.gerenciamento_financeiro.application.command.result.PaginaResult;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;

public interface BuscarClienteUseCase {

    /**
     * Busca clientes com paginação e filtros
     */
    PaginaResult<Cliente> buscarPaginadoComFiltros(
            BuscarClientesCommand command,
            PaginacaoCommand paginacao,
            UsuarioId usuarioId
    );

    /**
     * Busca clientes com paginação (sem filtros)
     */
    PaginaResult<Cliente> buscarPaginado(PaginacaoCommand paginacao, UsuarioId usuarioId);

    /**
     * Busca todos os clientes com filtros (sem paginação)
     */
    List<Cliente> buscarComFiltros(BuscarClientesCommand command, UsuarioId usuarioId);

    /**
     * Busca cliente por ID
     */
    Optional<Cliente> buscarPorId(ClienteId clienteId, UsuarioId usuarioId);

    /**
     * Verifica se cliente existe
     */
    boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    /**
     * Busca rápida por texto
     */
    List<Cliente> buscarPorTexto(String texto, UsuarioId usuarioId);

    /**
     * Busca apenas clientes ativos
     */
    List<Cliente> buscarApenasAtivos(UsuarioId usuarioId);
}