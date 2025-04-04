package com.vinicius.gerenciamento_financeiro.adapter.out.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.cliente.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JpaClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByUsuarioId(Long usuarioId);
}
