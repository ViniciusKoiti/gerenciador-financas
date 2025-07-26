package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente salvarCliente(Cliente cliente);

    Optional<Cliente> findById(ClienteId id);

    List<Cliente> findByUsuarioId(UsuarioId usuarioId);

    Page<Cliente> findAll(Pageable pageable);

    void deleteById(ClienteId id);

    boolean existsByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId);
    Optional<Cliente> findByIdAndUsuarioId(ClienteId clienteId, UsuarioId usuarioId);




}
