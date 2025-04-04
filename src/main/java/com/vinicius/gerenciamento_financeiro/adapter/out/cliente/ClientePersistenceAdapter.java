package com.vinicius.gerenciamento_financeiro.adapter.out.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.usuario.JpaUsuarioRepository;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
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
    public Cliente salvarCliente(Cliente cliente) {
        return jpaClienteRepository.save(cliente);
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return jpaClienteRepository.findById(id);
    }

    @Override
    public List<Cliente> findByUsuarioId(Long usuarioId) {
        return jpaClienteRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Page<Cliente> findAll(Pageable pageable) {
        return jpaClienteRepository.findAll(pageable);
    }
}