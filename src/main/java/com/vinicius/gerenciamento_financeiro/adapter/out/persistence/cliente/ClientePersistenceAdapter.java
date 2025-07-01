package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;
    private final ClienteJpaMapper clienteJpaMapper;

    @Override
    @Transactional
    public Cliente salvarCliente(Cliente cliente) {
        try {
            log.debug("Salvando cliente: {}", cliente.getNome());

            ClienteJpaEntity jpaEntity;

            if (cliente.isNovo()) {
                jpaEntity = clienteJpaMapper.toJpaEntity(cliente);
                log.debug("Criando novo cliente: {}", cliente.getNome());
            } else {
                jpaEntity = jpaClienteRepository.findById(cliente.getClienteId().getValue())
                        .orElse(clienteJpaMapper.toJpaEntity(cliente));

                clienteJpaMapper.updateJpaEntity(jpaEntity, cliente);
                log.debug("Atualizando cliente existente: ID {}", cliente.getClienteId().getValue());
            }

            ClienteJpaEntity savedEntity = jpaClienteRepository.save(jpaEntity);
            Cliente clienteSalvo = clienteJpaMapper.toDomainEntity(savedEntity);

            log.debug("Cliente salvo com sucesso: ID {}", savedEntity.getId());
            return clienteSalvo;

        } catch (Exception e) {
            log.error("Erro ao salvar cliente: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findById(ClienteId id) {
        log.debug("Buscando cliente por ID: {}", id.getValue());

        return jpaClienteRepository.findById(id.getValue())
                .map(clienteJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findByUsuarioId(UsuarioId usuarioId) {
        log.debug("Buscando clientes para usuário: {}", usuarioId.getValue());

        return jpaClienteRepository.findByUsuarioId(usuarioId.getValue())
                .stream()
                .map(clienteJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        log.debug("Buscando clientes paginados: página {}", pageable.getPageNumber());

        return jpaClienteRepository.findAll(pageable)
                .map(clienteJpaMapper::toDomainEntity);
    }

    @Override
    @Transactional
    public void deleteById(ClienteId id) {
        try {
            log.debug("Deletando cliente: ID {}", id.getValue());

            if (!jpaClienteRepository.existsById(id.getValue())) {
                log.warn("Tentativa de deletar cliente inexistente: ID {}", id.getValue());
                return;
            }

            jpaClienteRepository.deleteById(id.getValue());
            log.info("Cliente deletado: ID {}", id.getValue());

        } catch (Exception e) {
            log.error("Erro ao deletar cliente {}: {}", id.getValue(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Verificando se cliente {} pertence ao usuário: {}", clienteId.getValue(), usuarioId.getValue());

        return jpaClienteRepository.existsByIdAndUsuarioId(clienteId.getValue(), usuarioId.getValue());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId) {
        log.debug("Buscando cliente {} para usuário: {}", clienteId.getValue(), usuarioId.getValue());

        return jpaClienteRepository.findByIdAndUsuarioId(clienteId.getValue(), usuarioId.getValue())
                .map(clienteJpaMapper::toDomainEntity);
    }


}