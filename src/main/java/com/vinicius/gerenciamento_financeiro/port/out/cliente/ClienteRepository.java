package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    ClienteJpaEntity salvarCliente(ClienteJpaEntity cliente);

    Optional<ClienteJpaEntity> findById(Long id);

    List<ClienteJpaEntity> findByUsuarioId(Long usuarioId);

    Page<ClienteJpaEntity> findAll(Pageable pageable);


}
