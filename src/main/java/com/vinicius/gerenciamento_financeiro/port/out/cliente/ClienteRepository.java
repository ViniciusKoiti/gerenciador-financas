package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de persistência de Cliente.
 * Interface do domínio - trabalha apenas com objetos de domínio.
 */
public interface ClienteRepository {

    // ✅ OPERAÇÕES BÁSICAS

    /**
     * Salva um cliente (criar ou atualizar)
     */
    Cliente salvarCliente(Cliente cliente);

    /**
     * Busca cliente por ID (sem validação de usuário)
     */
    Optional<Cliente> findById(ClienteId id);

    /**
     * Busca todos os clientes de um usuário
     */
    List<Cliente> findByUsuarioId(UsuarioId usuarioId);

    /**
     * Busca clientes paginados (todos os usuários - uso administrativo)
     */
    Page<Cliente> findAll(Pageable pageable);

    /**
     * Remove cliente por ID
     */
    void deleteById(ClienteId id);

    /**
     * Verifica se cliente existe e pertence ao usuário
     */
    boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId);

    /**
     * Busca cliente por ID validando proprietário
     */
    Optional<Cliente> findByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId);

    // ✅ OPERAÇÕES COM FILTROS (NOVOS MÉTODOS)

    /**
     * Busca clientes do usuário com filtros aplicados
     *
     * @param usuarioId ID do usuário proprietário
     * @param filtro Filtros de domínio a serem aplicados
     * @return Lista de clientes filtrados
     */
    List<Cliente> findByUsuarioIdComFiltro(UsuarioId usuarioId, ClienteFiltro filtro);

    /**
     * Busca clientes do usuário com filtros e paginação
     *
     * @param usuarioId ID do usuário proprietário
     * @param filtro Filtros de domínio a serem aplicados
     * @param pageable Configuração de paginação
     * @return Página de clientes filtrados
     */
    Page<Cliente> findByUsuarioIdComFiltro(UsuarioId usuarioId, ClienteFiltro filtro, Pageable pageable);
}