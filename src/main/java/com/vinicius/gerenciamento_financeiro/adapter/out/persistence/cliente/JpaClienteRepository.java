package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JpaClienteRepository extends JpaRepository<ClienteJpaEntity, Long> {
    List<ClienteJpaEntity> findByUsuarioId(Long usuarioId);
}
