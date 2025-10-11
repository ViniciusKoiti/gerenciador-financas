package com.vinicius.gerenciamento_financeiro.application.service.cliente;

import com.vinicius.gerenciamento_financeiro.domain.exception.ClienteException;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Pagina;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Paginacao;
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
    public Pagina<Cliente> buscarPaginadoComFiltros(
            ClienteFiltro filtro,
            Paginacao paginacao,
            UsuarioId usuarioId
    ) {
        ClienteFiltro filtroEfetivo = filtro != null ? filtro : ClienteFiltro.vazio();
        Paginacao paginacaoEfetiva = paginacao != null ? paginacao : Paginacao.padrao();

        log.debug("Buscando clientes com filtros (paginado): pÃ¡gina {}, filtros: {}",
                paginacaoEfetiva.getPagina(), filtroEfetivo);

        validarUsuarioId(usuarioId);

        Pagina<Cliente> resultado = clienteRepository.buscarPaginadoComFiltros(
                filtroEfetivo,
                paginacaoEfetiva,
                usuarioId
        );

        log.debug("Encontrados {} clientes (total: {})",
                resultado.getConteudo().size(), resultado.getTotalElementos());

        return resultado;
    }

    @Override
    public Pagina<Cliente> buscarPaginado(Paginacao paginacao, UsuarioId usuarioId) {
        log.debug("Buscando clientes paginados sem filtros: pÃ¡gina {}",
                paginacao != null ? paginacao.getPagina() : 0);

        return buscarPaginadoComFiltros(
                ClienteFiltro.vazio(),
                paginacao,
                usuarioId
        );
    }

    @Override
    public List<Cliente> buscarComFiltros(ClienteFiltro filtro, UsuarioId usuarioId) {
        ClienteFiltro filtroEfetivo = filtro != null ? filtro : ClienteFiltro.vazio();

        log.debug("Buscando todos os clientes com filtros: {}", filtroEfetivo);

        validarUsuarioId(usuarioId);

        List<Cliente> clientes = clienteRepository.buscarComFiltros(filtroEfetivo, usuarioId);

        log.debug("Encontrados {} clientes", clientes.size());

        return clientes;
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Buscando cliente {} para usuÃ¡rio {}", clienteId.getValue(), usuarioId.getValue());

        validarClienteId(clienteId);
        validarUsuarioId(usuarioId);

        Optional<Cliente> cliente = clienteRepository.buscarPorId(clienteId);

        if (cliente.isPresent() && !cliente.get().pertenceAoUsuario(usuarioId)) {
            log.warn("Cliente {} nÃ£o pertence ao usuÃ¡rio {}", clienteId, usuarioId);
            return Optional.empty();
        }

        return cliente;
    }

    @Override
    public boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Verificando existÃªncia do cliente {} para usuÃ¡rio {}",
                clienteId.getValue(), usuarioId.getValue());

        validarClienteId(clienteId);
        validarUsuarioId(usuarioId);

        return clienteRepository.existePorIdEUsuario(clienteId, usuarioId);
    }

    @Override
    public List<Cliente> buscarPorTexto(String texto, UsuarioId usuarioId) {
        log.debug("Busca rÃ¡pida por texto: '{}'", texto);

        if (texto == null || texto.trim().length() < 2) {
            throw ClienteException.filtrosInvalidos("Texto de busca deve ter pelo menos 2 caracteres");
        }

        validarUsuarioId(usuarioId);

        ClienteFiltro filtro = ClienteFiltro.builder()
                .buscaGeral(texto.trim())
                .build();

        List<Cliente> clientes = clienteRepository.buscarComFiltros(filtro, usuarioId);

        log.debug("Encontrados {} clientes para busca '{}'", clientes.size(), texto);

        return clientes;
    }

    @Override
    public List<Cliente> buscarApenasAtivos(UsuarioId usuarioId) {
        log.debug("Buscando apenas clientes ativos para usuÃ¡rio {}", usuarioId.getValue());

        validarUsuarioId(usuarioId);

        ClienteFiltro filtro = ClienteFiltro.builder()
                .ativo(true)
                .build();

        List<Cliente> clientes = clienteRepository.buscarComFiltros(filtro, usuarioId);

        log.debug("Encontrados {} clientes ativos", clientes.size());

        return clientes;
    }

    private void validarClienteId(ClienteId clienteId) {
        if (clienteId == null) {
            throw ClienteException.dadosObrigatorios("ClienteId");
        }

        if (clienteId.getValue() == null || clienteId.getValue() <= 0) {
            throw ClienteException.filtrosInvalidos("ClienteId deve ser um nÃºmero positivo");
        }
    }

    private void validarUsuarioId(UsuarioId usuarioId) {
        if (usuarioId == null) {
            throw ClienteException.dadosObrigatorios("UsuarioId");
        }

        if (usuarioId.getValue() == null || usuarioId.getValue() <= 0) {
            throw ClienteException.filtrosInvalidos("UsuarioId deve ser um nÃºmero positivo");
        }
    }
}