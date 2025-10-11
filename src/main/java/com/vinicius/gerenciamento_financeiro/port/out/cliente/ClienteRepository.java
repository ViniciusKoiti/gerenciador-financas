package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Pagina;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Paginacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Porta de saída para operações de persistência de Cliente.
 * Trabalha APENAS com objetos de domínio e abstrações de infraestrutura.
 * Não deve conhecer detalhes de frameworks (JPA, Spring Data, etc).
 */
public interface ClienteRepository {

    // ========================================
    // OPERAÇÕES BÁSICAS
    // ========================================

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(ClienteId clienteId);

    Optional<Cliente> buscarPorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    void deletar(ClienteId clienteId);

    // ========================================
    // BUSCAS COM FILTROS (SEM PAGINAÇÃO)
    // ========================================

    List<Cliente> buscarPorUsuario(UsuarioId usuarioId);

    List<Cliente> buscarComFiltros(ClienteFiltro filtro, UsuarioId usuarioId);

    // ========================================
    // BUSCAS PAGINADAS
    // ========================================

    Pagina<Cliente> buscarPaginado(Paginacao paginacao, UsuarioId usuarioId);

    Pagina<Cliente> buscarPaginadoComFiltros(
            ClienteFiltro filtro,
            Paginacao paginacao,
            UsuarioId usuarioId
    );

    Pagina<Cliente> buscarTodosPaginado(Paginacao paginacao);
}