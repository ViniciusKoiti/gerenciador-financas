package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification.ClienteSpecificationBuilder;
import com.vinicius.gerenciamento_financeiro.application.command.PaginacaoCommand;
import com.vinicius.gerenciamento_financeiro.application.command.cliente.BuscarClientesCommand;
import com.vinicius.gerenciamento_financeiro.application.command.result.PaginaResult;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adapter de persistência para Cliente.
 * Implementa a porta de saída ClienteRepository.
 * Responsável por converter entre modelos de domínio e entidades JPA.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;
    private final ClienteJpaMapper clienteJpaMapper;
    private final ClienteSpecificationBuilder specificationBuilder;

    @Override
    @Transactional
    public Cliente salvar(Cliente cliente) {
        try {
            log.debug("Salvando cliente: {}", cliente.getNome());

            ClienteJpaEntity jpaEntity;

            if (cliente.isNovo()) {
                // Cliente novo - cria entidade
                jpaEntity = clienteJpaMapper.toJpaEntity(cliente);
                log.debug("Criando novo cliente: {}", cliente.getNome());
            } else {
                // Cliente existente - busca e atualiza
                jpaEntity = jpaClienteRepository.findById(cliente.getClienteId().getValue())
                        .orElseThrow(() -> new IllegalStateException(
                                "Cliente com ID " + cliente.getClienteId().getValue() + " não encontrado para atualização"
                        ));

                clienteJpaMapper.updateJpaEntity(jpaEntity, cliente);
                log.debug("Atualizando cliente existente: ID {}", cliente.getClienteId().getValue());
            }

            ClienteJpaEntity savedEntity = jpaClienteRepository.save(jpaEntity);
            Cliente clienteSalvo = clienteJpaMapper.toDomainEntity(savedEntity);

            log.info("Cliente salvo com sucesso: ID {}", savedEntity.getId());
            return clienteSalvo;

        } catch (Exception e) {
            log.error("Erro ao salvar cliente: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(ClienteId clienteId) {
        log.debug("Buscando cliente por ID: {}", clienteId.getValue());

        return jpaClienteRepository.findById(clienteId.getValue())
                .map(clienteJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Buscando cliente {} para usuário: {}", clienteId.getValue(), usuarioId.getValue());

        return jpaClienteRepository.findByIdAndUsuarioId(
                        clienteId.getValue(),
                        usuarioId.getValue()
                )
                .map(clienteJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorIdEUsuario(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Verificando se cliente {} pertence ao usuário: {}",
                clienteId.getValue(), usuarioId.getValue());

        return jpaClienteRepository.existsByIdAndUsuarioId(
                clienteId.getValue(),
                usuarioId.getValue()
        );
    }

    @Override
    @Transactional
    public void deletar(ClienteId clienteId) {
        try {
            log.debug("Deletando cliente: ID {}", clienteId.getValue());

            if (!jpaClienteRepository.existsById(clienteId.getValue())) {
                log.warn("Tentativa de deletar cliente inexistente: ID {}", clienteId.getValue());
                return;
            }

            jpaClienteRepository.deleteById(clienteId.getValue());
            log.info("Cliente deletado: ID {}", clienteId.getValue());

        } catch (Exception e) {
            log.error("Erro ao deletar cliente {}: {}", clienteId.getValue(), e.getMessage(), e);
            throw e;
        }
    }

    // ========================================
    // BUSCAS COM FILTROS (SEM PAGINAÇÃO)
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorUsuario(UsuarioId usuarioId) {
        log.debug("Buscando todos os clientes para usuário: {}", usuarioId.getValue());

        return jpaClienteRepository.findByUsuarioId(usuarioId.getValue())
                .stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarComFiltros(BuscarClientesCommand command, UsuarioId usuarioId) {
        log.debug("Buscando clientes com filtros para usuário: {}", usuarioId.getValue());
        log.debug("Filtros: {}", command);

        // Constrói Specification a partir do Command
        Specification<ClienteJpaEntity> specification = specificationBuilder.build(
                usuarioId.getValue(),
                command
        );

        List<ClienteJpaEntity> entities = jpaClienteRepository.findAll(specification);

        log.debug("Encontrados {} clientes com filtros", entities.size());

        return entities.stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();
    }

    // ========================================
    // BUSCAS PAGINADAS
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public PaginaResult<Cliente> buscarPaginado(PaginacaoCommand paginacao, UsuarioId usuarioId) {
        log.debug("Buscando clientes paginados (sem filtros) para usuário: {} - página: {}",
                usuarioId.getValue(), paginacao.getPagina());

        PageRequest pageRequest = criarPageRequest(paginacao);

        Page<ClienteJpaEntity> page = jpaClienteRepository.findByUsuarioId(
                usuarioId.getValue(),
                pageRequest
        );

        // Converte Page → PaginaResult
        return converterParaPaginaResult(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginaResult<Cliente> buscarPaginadoComFiltros(
            BuscarClientesCommand command,
            PaginacaoCommand paginacao,
            UsuarioId usuarioId
    ) {
        log.debug("Buscando clientes paginados com filtros para usuário: {} - página: {}",
                usuarioId.getValue(), paginacao.getPagina());
        log.debug("Filtros: {}", command);

        PageRequest pageRequest = criarPageRequest(paginacao);

        Specification<ClienteJpaEntity> specification = specificationBuilder.build(
                usuarioId.getValue(),
                command
        );

        Page<ClienteJpaEntity> page = jpaClienteRepository.findAll(specification, pageRequest);

        log.debug("Encontrados {} clientes paginados (total: {})",
                page.getNumberOfElements(), page.getTotalElements());

        // Converte Page → PaginaResult
        return converterParaPaginaResult(page);
    }

    // ========================================
    // OPERAÇÕES ADMINISTRATIVAS
    // ========================================

    @Override
    @Transactional(readOnly = true)
    public PaginaResult<Cliente> buscarTodosPaginado(PaginacaoCommand paginacao) {
        log.debug("Buscando todos os clientes do sistema - página: {}", paginacao.getPagina());

        PageRequest pageRequest = criarPageRequest(paginacao);
        Page<ClienteJpaEntity> page = jpaClienteRepository.findAll(pageRequest);

        return converterParaPaginaResult(page);
    }

    // ========================================
    // MÉTODOS AUXILIARES (CONVERSÃO)
    // ========================================

    /**
     * Converte PaginacaoCommand em PageRequest do Spring Data
     */
    private PageRequest criarPageRequest(PaginacaoCommand paginacao) {
        Sort sort = Sort.unsorted();

        if (paginacao.getCampoOrdenacao() != null) {
            Sort.Direction direction = "DESC".equalsIgnoreCase(paginacao.getDirecaoOrdenacao())
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            sort = Sort.by(direction, paginacao.getCampoOrdenacao());
        }

        return PageRequest.of(
                paginacao.getPagina(),
                paginacao.getTamanhoPagina(),
                sort
        );
    }

    /**
     * Converte Page do Spring Data em PaginaResult do domínio
     */
    private PaginaResult<Cliente> converterParaPaginaResult(Page<ClienteJpaEntity> page) {
        List<Cliente> clientes = page.getContent()
                .stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();

        return PaginaResult.of(
                clientes,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }
    /**
     * Conta total de clientes de um usuário
     */
    @Transactional(readOnly = true)
    public long contarPorUsuario(UsuarioId usuarioId) {
        log.debug("Contando clientes do usuário: {}", usuarioId.getValue());
        return jpaClienteRepository.countByUsuarioId(usuarioId.getValue());
    }

    /**
     * Verifica se CPF já existe para o usuário
     */
    @Transactional(readOnly = true)
    public boolean existeCpfParaUsuario(String cpf, UsuarioId usuarioId) {
        log.debug("Verificando CPF {} para usuário: {}", cpf, usuarioId.getValue());
        return jpaClienteRepository.existsByCpfAndUsuarioId(cpf, usuarioId.getValue());
    }

    /**
     * Verifica se email já existe para o usuário
     */
    @Transactional(readOnly = true)
    public boolean existeEmailParaUsuario(String email, UsuarioId usuarioId) {
        log.debug("Verificando email {} para usuário: {}", email, usuarioId.getValue());
        return jpaClienteRepository.existsByEmailAndUsuarioId(email, usuarioId.getValue());
    }
}