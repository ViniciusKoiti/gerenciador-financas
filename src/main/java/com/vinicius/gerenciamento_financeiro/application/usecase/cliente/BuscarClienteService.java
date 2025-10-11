package com.vinicius.gerenciamento_financeiro.application.usecase.cliente;

import com.vinicius.gerenciamento_financeiro.application.command.PaginacaoCommand;
import com.vinicius.gerenciamento_financeiro.application.command.cliente.BuscarClientesCommand;
import com.vinicius.gerenciamento_financeiro.application.command.result.PaginaResult;
import com.vinicius.gerenciamento_financeiro.domain.exception.ClienteException;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.BuscarClienteUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BuscarClienteService implements BuscarClienteUseCase {

    private final ClienteRepository clienteRepository;

    @Override
    public PaginaResult<Cliente> buscarPaginadoComFiltros(
            BuscarClientesCommand command,
            PaginacaoCommand paginacao,
            UsuarioId usuarioId
    ) {
        log.debug("Buscando clientes com filtros (paginado): página {}, filtros: {}",
                paginacao.getPagina(), command);

        validarUsuarioId(usuarioId);



        PaginaResult<Cliente> resultado = clienteRepository.buscarPaginadoComFiltros(
                command,
                paginacao,
                usuarioId
        );

        log.debug("Encontrados {} clientes (total: {})",
                resultado.getConteudo().size(), resultado.getTotalElementos());

        return resultado;
    }

    @Override
    public PaginaResult<Cliente> buscarPaginado(PaginacaoCommand paginacao, UsuarioId usuarioId) {
        log.debug("Buscando clientes paginados sem filtros: página {}", paginacao.getPagina());

        return buscarPaginadoComFiltros(
                BuscarClientesCommand.semFiltros(),
                paginacao,
                usuarioId
        );
    }

    @Override
    public List<Cliente> buscarComFiltros(BuscarClientesCommand command, UsuarioId usuarioId) {
        log.debug("Buscando todos os clientes com filtros: {}", command);

        validarUsuarioId(usuarioId);

        List<Cliente> clientes = clienteRepository.buscarComFiltros(command, usuarioId);

        log.debug("Encontrados {} clientes", clientes.size());

        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Buscando cliente {} para usuário {}", clienteId.getValue(), usuarioId.getValue());

        validarClienteId(clienteId);
        validarUsuarioId(usuarioId);

        Optional<Cliente> cliente = clienteRepository.buscarPorId(clienteId);

        // Verifica se pertence ao usuário
        if (cliente.isPresent() && !cliente.get().pertenceAoUsuario(usuarioId)) {
            log.warn("Cliente {} não pertence ao usuário {}", clienteId, usuarioId);
            return Optional.empty();
        }

        return cliente;
    }

    @Override
    public boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Verificando existência do cliente {} para usuário {}",
                clienteId.getValue(), usuarioId.getValue());

        validarClienteId(clienteId);
        validarUsuarioId(usuarioId);

        return clienteRepository.existePorIdEUsuario(clienteId, usuarioId);
    }

    @Override
    public List<Cliente> buscarPorTexto(String texto, UsuarioId usuarioId) {
        log.debug("Busca rápida por texto: '{}'", texto);

        if (texto == null || texto.trim().length() < 2) {
            throw ClienteException.filtrosInvalidos("Texto de busca deve ter pelo menos 2 caracteres");
        }

        validarUsuarioId(usuarioId);

        BuscarClientesCommand command = BuscarClientesCommand.builder().buscarGeral(texto.trim()).build();

        List<Cliente> clientes = clienteRepository.buscarComFiltros(command, usuarioId);

        log.debug("Encontrados {} clientes para busca '{}'", clientes.size(), texto);

        return clientes;
    }

    @Override
    public List<Cliente> buscarApenasAtivos(UsuarioId usuarioId) {
        log.debug("Buscando apenas clientes ativos para usuário {}", usuarioId.getValue());

        validarUsuarioId(usuarioId);

        BuscarClientesCommand command = BuscarClientesCommand.ativos();

        List<Cliente> clientes = clienteRepository.buscarComFiltros(command, usuarioId);

        log.debug("Encontrados {} clientes ativos", clientes.size());

        return clientes;
    }

    // ========================================
    // Métodos privados de validação
    // ========================================

    private void validarClienteId(ClienteId clienteId) {
        if (clienteId == null) {
            throw ClienteException.dadosObrigatorios("ClienteId");
        }

        if (clienteId.getValue() == null || clienteId.getValue() <= 0) {
            throw ClienteException.filtrosInvalidos("ClienteId deve ser um número positivo");
        }
    }

    private void validarUsuarioId(UsuarioId usuarioId) {
        if (usuarioId == null) {
            throw ClienteException.dadosObrigatorios("UsuarioId");
        }

        if (usuarioId.getValue() == null || usuarioId.getValue() <= 0) {
            throw ClienteException.filtrosInvalidos("UsuarioId deve ser um número positivo");
        }
    }
}