package com.vinicius.gerenciamento_financeiro.port.out.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente salvarCliente(Cliente cliente);

    Optional<Cliente> findById(Long id);

    List<Cliente> findByUsuarioId(Long usuarioId);

    Page<Cliente> findAll(Pageable pageable);


}
