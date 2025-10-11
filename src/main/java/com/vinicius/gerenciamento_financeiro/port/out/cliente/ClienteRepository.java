package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.application.command.PaginacaoCommand;
import com.vinicius.gerenciamento_financeiro.application.command.cliente.BuscarClientesCommand;
import com.vinicius.gerenciamento_financeiro.application.command.result.PaginaResult;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;

import java.util.List;
import java.util.Optional;

/**
 * Port de saída para operações de persistência de Cliente.
 * Trabalha APENAS com objetos de domínio e abstrações da aplicação.
 * NÃO deve conhecer detalhes de frameworks (JPA, Spring Data, etc).
 */
public interface ClienteRepository {

    // ========================================
    // OPERAÇÕES BÁSICAS
    // ========================================

    /**
     * Salva um cliente (criar ou atualizar)
     * @param cliente Cliente a ser salvo
     * @return Cliente salvo com ID atualizado
     */
    Cliente salvar(Cliente cliente);

    /**
     * Busca cliente por ID
     * @param clienteId ID do cliente
     * @return Cliente ou Optional.empty()
     */
    Optional<Cliente> buscarPorId(ClienteId clienteId);

    /**
     * Busca cliente por ID validando que pertence ao usuário
     * @param clienteId ID do cliente
     * @param usuarioId ID do usuário proprietário
     * @return Cliente ou Optional.empty()
     */
    Optional<Cliente> buscarPorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    /**
     * Verifica se cliente existe e pertence ao usuário
     * @param clienteId ID do cliente
     * @param usuarioId ID do usuário proprietário
     * @return true se existe e pertence ao usuário
     */
    boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId);

    /**
     * Remove cliente por ID
     * @param clienteId ID do cliente a ser removido
     */
    void deletar(ClienteId clienteId);

    // ========================================
    // BUSCAS COM FILTROS (SEM PAGINAÇÃO)
    // ========================================

    /**
     * Busca todos os clientes de um usuário
     * @param usuarioId ID do usuário
     * @return Lista de clientes
     */
    List<Cliente> buscarPorUsuario(UsuarioId usuarioId);

    /**
     * Busca clientes aplicando filtros
     * @param command Comando com filtros de busca
     * @param usuarioId ID do usuário proprietário
     * @return Lista de clientes filtrados
     */
    List<Cliente> buscarComFiltros(BuscarClientesCommand command, UsuarioId usuarioId);

    // ========================================
    // BUSCAS PAGINADAS
    // ========================================

    /**
     * Busca clientes com paginação (sem filtros)
     * @param paginacao Parâmetros de paginação
     * @param usuarioId ID do usuário proprietário
     * @return Resultado paginado
     */
    PaginaResult<Cliente> buscarPaginado(PaginacaoCommand paginacao, UsuarioId usuarioId);

    /**
     * Busca clientes com paginação e filtros
     * @param command Comando com filtros de busca
     * @param paginacao Parâmetros de paginação
     * @param usuarioId ID do usuário proprietário
     * @return Resultado paginado e filtrado
     */
    PaginaResult<Cliente> buscarPaginadoComFiltros(
            BuscarClientesCommand command,
            PaginacaoCommand paginacao,
            UsuarioId usuarioId
    );


    /**
     * Busca todos os clientes do sistema (uso administrativo)
     * Use com cuidado - pode retornar muitos registros
     * @param paginacao Parâmetros de paginação
     * @return Resultado paginado de todos os clientes
     */
    PaginaResult<Cliente> buscarTodosPaginado(PaginacaoCommand paginacao);
}