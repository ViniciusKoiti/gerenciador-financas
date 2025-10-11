//package com.vinicius.gerenciamento_financeiro.application.service.cliente;
//
//import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente.ClienteFiltroMapper;
//import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.cliente.ClienteResponseMapper;
//import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente.ClienteFiltroRequest;
//import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente.ClienteResponse;
//import com.vinicius.gerenciamento_financeiro.domain.exception.ClienteException;
//import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
//import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
//import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
//import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
//import com.vinicius.gerenciamento_financeiro.port.in.BuscarClienteUseCase;
//import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
//import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
///**
// * Implementação do caso de uso de busca de clientes.
// * Responsável por aplicar regras de negócio e coordenar as operações de busca.
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class BuscarClienteUseCaseImpl implements BuscarClienteUseCase {
//
//    private final ClienteRepository clienteRepository;
//    private final UsuarioAutenticadoPort usuarioAutenticadoPort;
//    private final ClienteResponseMapper responseMapper;
//    private final ClienteFiltroMapper filtroMapper;
//
//    @Override
//    public Page<ClienteResponse> findPageClienteComFiltros(ClienteFiltroRequest filtroRequest, Pageable pageable) {
//        log.debug("Buscando clientes com filtros: página {}, tamanho {}",
//                pageable.getPageNumber(), pageable.getPageSize());
//        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
//        ClienteFiltro filtro = filtroMapper.toDomain(filtroRequest);
//        log.debug("Filtros aplicados - temFiltros: {}, temBuscaGeral: {}",
//                filtro.temFiltros(), filtro.temBuscaGeral());
//        Page<Cliente> clientes = clienteRepository.findByUsuarioIdComFiltro(usuarioId, filtro, pageable);
//
//        log.debug("Encontrados {} clientes (total: {})",
//                clientes.getNumberOfElements(), clientes.getTotalElements());
//
//        return clientes.map(responseMapper::toResponse);
//    }
//
//    @Override
//    public Page<ClienteResponse> findPageCliente(Pageable pageable) {
//        log.debug("Buscando clientes paginados: página {}, tamanho {}",
//                pageable.getPageNumber(), pageable.getPageSize());
//        return findPageClienteComFiltros(null, pageable);
//    }
//    @Override
//    public List<ClienteResponse> findAllClienteComFiltros(ClienteFiltroRequest filtroRequest) {
//        log.debug("Buscando todos os clientes com filtros");
//
//        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
//        var filtro = filtroMapper.toDomain(filtroRequest);
//
//        List<Cliente> clientes = clienteRepository.findByUsuarioIdComFiltro(usuarioId, filtro);
//
//        log.debug("Encontrados {} clientes com filtros", clientes.size());
//
//        return clientes.stream()
//                .map(responseMapper::toResponse)
//                .toList();
//    }
//
//    public Optional<ClienteResponse> findById(ClienteId clienteId) {
//        log.debug("Busca interna de cliente por ID: {}", clienteId.getValue());
//
//        validarClienteId(clienteId);
//
//        return clienteRepository.findById(clienteId)
//                .map(responseMapper::toResponse);
//    }
//    public boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
//        log.debug("Verificando existência do cliente {} para usuário {}",
//                clienteId.getValue(), usuarioId.getValue());
//
//        validarClienteId(clienteId);
//        validarUsuarioId(usuarioId);
//
//        return clienteRepository.existsByIdAndUsuarioId(clienteId, usuarioId);
//    }
//    public Optional<ClienteResponse> findByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
//        log.debug("Buscando cliente {} para usuário {}", clienteId.getValue(), usuarioId.getValue());
//
//        validarClienteId(clienteId);
//        validarUsuarioId(usuarioId);
//
//        return clienteRepository.findByIdAndUsuarioId(clienteId, usuarioId)
//                .map(responseMapper::toResponse);
//    }
//    public List<ClienteResponse> findByUsuarioId(UsuarioId usuarioId) {
//        log.debug("Buscando clientes do usuário: {}", usuarioId.getValue());
//
//        validarUsuarioId(usuarioId);
//
//        List<Cliente> clientes = clienteRepository.findByUsuarioId(usuarioId);
//
//        log.debug("Encontrados {} clientes para o usuário {}", clientes.size(), usuarioId.getValue());
//
//        return clientes.stream()
//                .map(responseMapper::toResponse)
//                .toList();
//    }
//
//    public List<ClienteResponse> buscarPorTexto(String texto) {
//        log.debug("Busca rápida por texto: {}", texto);
//
//        if (texto == null || texto.trim().length() < 2) {
//            throw ClienteException.filtrosInvalidos("Texto de busca deve ter pelo menos 2 caracteres");
//        }
//
//        var filtro = filtroMapper.comBuscaGeral(texto);
//        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
//
//        List<Cliente> clientes = clienteRepository.findByUsuarioIdComFiltro(usuarioId, filtro);
//
//        return clientes.stream()
//                .map(responseMapper::toResponse)
//                .toList();
//    }
//
//    public List<ClienteResponse> buscarApenasAtivos() {
//        log.debug("Buscando apenas clientes ativos");
//
//        var filtro = filtroMapper.comStatusAtivo(true);
//        var usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
//
//        List<Cliente> clientes = clienteRepository.findByUsuarioIdComFiltro(usuarioId, filtro);
//
//        return clientes.stream()
//                .map(responseMapper::toResponse)
//                .toList();
//    }
//
//    private void validarClienteId(ClienteId clienteId) {
//        if (clienteId == null) {
//            throw ClienteException.dadosObrigatorios("ClienteId");
//        }
//
//        if (clienteId.getValue() == null || clienteId.getValue() <= 0) {
//            throw ClienteException.filtrosInvalidos("ClienteId deve ser um número positivo");
//        }
//    }
//
//    private void validarUsuarioId(UsuarioId usuarioId) {
//        if (usuarioId == null) {
//            throw ClienteException.dadosObrigatorios("UsuarioId");
//        }
//
//        if (usuarioId.getValue() == null || usuarioId.getValue() <= 0) {
//            throw ClienteException.filtrosInvalidos("UsuarioId deve ser um número positivo");
//        }
//    }
//}