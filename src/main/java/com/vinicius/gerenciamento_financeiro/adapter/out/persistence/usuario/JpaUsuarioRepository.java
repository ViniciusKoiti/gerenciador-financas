package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioJpaEntity, Long> {

    boolean existsByEmail(String email);
    Optional<UsuarioJpaEntity> findByEmail(String email);
    @Query("SELECT u FROM UsuarioJpaEntity u WHERE LOWER(u.email) = LOWER(:email)")
    Optional<UsuarioJpaEntity> findByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM UsuarioJpaEntity u")
    long countActiveUsers();
    @Query("SELECT DISTINCT u FROM UsuarioJpaEntity u LEFT JOIN FETCH u.categorias WHERE u.id = :id")
    Optional<UsuarioJpaEntity> findByIdWithCategorias(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM UsuarioJpaEntity u WHERE LOWER(u.email) = LOWER(:email) AND u.id != :excludeId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);
}