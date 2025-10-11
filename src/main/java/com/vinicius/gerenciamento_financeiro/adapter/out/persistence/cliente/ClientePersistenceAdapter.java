package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.specification.ClienteSpecificationBuilder;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteFiltro;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Pagina;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Paginacao;
import com.vinicius.gerenciamento_financeiro.domain.model.shared.Paginacao.OrdenacaoDirecao;
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
                jpaEntity = clienteJpaMapper.toJpaEntity(cliente);
                log.debug("Criando novo cliente: {}", cliente.getNome());
            } else {
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

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorUsuario(UsuarioId usuarioId) {
        log.debug("Buscando clientes do usuário: {}", usuarioId.getValue());

        return jpaClienteRepository.findByUsuarioId(usuarioId.getValue())
                .stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarComFiltros(ClienteFiltro filtro, UsuarioId usuarioId) {
        ClienteFiltro filtroEfetivo = filtro != null ? filtro : ClienteFiltro.vazio();

        log.debug("Buscando clientes com filtros (lista): usuário {}, filtros {}",
                usuarioId.getValue(), filtroEfetivo);

        Specification<ClienteJpaEntity> specification = specificationBuilder.build(
                usuarioId.getValue(),
                filtroEfetivo
        );

        return jpaClienteRepository.findAll(specification).stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Cliente> buscarPaginado(Paginacao paginacao, UsuarioId usuarioId) {
        Paginacao paginacaoEfetiva = paginacao != null ? paginacao : Paginacao.padrao();

        log.debug("Buscando clientes paginados (sem filtros) para usuário: {} - página: {}",
                usuarioId.getValue(), paginacaoEfetiva.getPagina());

        PageRequest pageRequest = criarPageRequest(paginacaoEfetiva);

        Page<ClienteJpaEntity> page = jpaClienteRepository.findByUsuarioId(
                usuarioId.getValue(),
                pageRequest
        );

        return converterParaPagina(page);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Cliente> buscarPaginadoComFiltros(
            ClienteFiltro filtro,
            Paginacao paginacao,
            UsuarioId usuarioId
    ) {
        ClienteFiltro filtroEfetivo = filtro != null ? filtro : ClienteFiltro.vazio();
        Paginacao paginacaoEfetiva = paginacao != null ? paginacao : Paginacao.padrao();

        log.debug("Buscando clientes paginados com filtros para usuário: {} - página: {}",
                usuarioId.getValue(), paginacaoEfetiva.getPagina());
        log.debug("Filtros: {}", filtroEfetivo);

        PageRequest pageRequest = criarPageRequest(paginacaoEfetiva);

        Specification<ClienteJpaEntity> specification = specificationBuilder.build(
                usuarioId.getValue(),
                filtroEfetivo
        );

        Page<ClienteJpaEntity> page = jpaClienteRepository.findAll(specification, pageRequest);

        log.debug("Encontrados {} clientes paginados (total: {})",
                page.getNumberOfElements(), page.getTotalElements());

        return converterParaPagina(page);
    }

    @Override
    @Transactional(readOnly = true)
    public Pagina<Cliente> buscarTodosPaginado(Paginacao paginacao) {
        Paginacao paginacaoEfetiva = paginacao != null ? paginacao : Paginacao.padrao();

        log.debug("Buscando todos os clientes do sistema - página: {}", paginacaoEfetiva.getPagina());

        PageRequest pageRequest = criarPageRequest(paginacaoEfetiva);
        Page<ClienteJpaEntity> page = jpaClienteRepository.findAll(pageRequest);

        return converterParaPagina(page);
    }

    private PageRequest criarPageRequest(Paginacao paginacao) {
        Sort sort = Sort.unsorted();

        if (paginacao.getCampoOrdenacao() != null && !paginacao.getCampoOrdenacao().isBlank()) {
            OrdenacaoDirecao direcao = paginacao.direcaoOrPadrao();
            Sort.Direction direction = direcao == OrdenacaoDirecao.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, paginacao.getCampoOrdenacao());
        }

        return PageRequest.of(
                paginacao.getPagina(),
                paginacao.getTamanhoPagina(),
                sort
        );
    }

    private Pagina<Cliente> converterParaPagina(Page<ClienteJpaEntity> page) {
        List<Cliente> clientes = page.getContent()
                .stream()
                .map(clienteJpaMapper::toDomainEntity)
                .toList();

        return Pagina.of(
                clientes,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public long contarPorUsuario(UsuarioId usuarioId) {
        log.debug("Contando clientes do usuário: {}", usuarioId.getValue());
        return jpaClienteRepository.countByUsuarioId(usuarioId.getValue());
    }

    @Transactional(readOnly = true)
    public boolean existeCpfParaUsuario(String cpf, UsuarioId usuarioId) {
        log.debug("Verificando CPF {} para usuário: {}", cpf, usuarioId.getValue());
        return jpaClienteRepository.existsByCpfAndUsuarioId(cpf, usuarioId.getValue());
    }

    @Transactional(readOnly = true)
    public boolean existeEmailParaUsuario(String email, UsuarioId usuarioId) {
        log.debug("Verificando email {} para usuário: {}", email, usuarioId.getValue());
        return jpaClienteRepository.existsByEmailAndUsuarioId(email, usuarioId.getValue());
    }
}