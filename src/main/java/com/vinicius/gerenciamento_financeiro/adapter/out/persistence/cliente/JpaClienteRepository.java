package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.cliente.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaClienteRepository extends JpaRepository<ClienteJpaEntity, Long> {

    List<ClienteJpaEntity> findByUsuarioId(Long usuarioId);

    boolean existsByIdAndUsuarioId(Long clienteId, Long usuarioId);

    Optional<ClienteJpaEntity> findByIdAndUsuarioId(Long clienteId, Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    boolean existsByCpfAndUsuarioId(String cpf, Long usuarioId);

    boolean existsByEmailAndUsuarioId(String email, Long usuarioId);

    @Query("SELECT c FROM ClienteJpaEntity c WHERE c.usuario.id = :usuarioId AND c.nome LIKE %:nome%")
    List<ClienteJpaEntity> findByUsuarioIdAndNomeContaining(@Param("usuarioId") Long usuarioId, @Param("nome") String nome);

    @Query("SELECT c FROM ClienteJpaEntity c WHERE c.cpf = :cpf")
    Optional<ClienteJpaEntity> findByCpf(@Param("cpf") String cpf);

    @Query("SELECT c FROM ClienteJpaEntity c WHERE c.email = :email")
    Optional<ClienteJpaEntity> findByEmail(@Param("email") String email);

    @Query("SELECT c FROM ClienteJpaEntity c WHERE c.usuario.id = :usuarioId ORDER BY c.nome ASC")
    List<ClienteJpaEntity> findByUsuarioIdOrderByNome(@Param("usuarioId") Long usuarioId);
}