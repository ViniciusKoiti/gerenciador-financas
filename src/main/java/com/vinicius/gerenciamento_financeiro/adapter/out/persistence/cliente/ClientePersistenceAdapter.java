package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import com.vinicius.gerenciamento_financeiro.port.out.cliente.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepository {

    private final JpaClienteRepository jpaClienteRepository;

    @Override
    public ClienteJpaEntity salvarCliente(ClienteJpaEntity cliente) {
        return jpaClienteRepository.save(cliente);
    }

    @Override
    public Optional<ClienteJpaEntity> findById(Long id) {
        return jpaClienteRepository.findById(id);
    }

    @Override
    public List<ClienteJpaEntity> findByUsuarioId(Long usuarioId) {
        return jpaClienteRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Page<ClienteJpaEntity> findAll(Pageable pageable) {
        return jpaClienteRepository.findAll(pageable);
    }
}