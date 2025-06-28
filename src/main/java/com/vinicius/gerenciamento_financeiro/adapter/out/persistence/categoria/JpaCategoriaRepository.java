package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCategoriaRepository extends JpaRepository<CategoriaJpaEntity, Long> {

    List<CategoriaJpaEntity> findCategoriasByUsuarioId(Long userId);

    boolean existsByUsuarioIdAndId(Long usuarioId, Long id);

    Optional<CategoriaJpaEntity> findByIdAndUsuario_id(Long categoriaId, Long usuarioId);

    Page<CategoriaJpaEntity> findByUsuario_id(Long usuarioId, Pageable pageable);

    long countByUsuario_id(Long usuarioId);

    long countByUsuario_idAndAtiva(Long usuarioId, boolean ativa);

    @Query("SELECT c FROM CategoriaJpaEntity c WHERE c.id = :categoriaId AND c.usuario.id = :usuarioId AND c.ativa = true")
    Optional<CategoriaJpaEntity> findByIdAndUsuarioIdAndAtiva(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM CategoriaJpaEntity c WHERE c.usuario.id = :usuarioId AND c.ativa = true ORDER BY c.nome ASC")
    Page<CategoriaJpaEntity> findByUsuarioIdAndAtivaOrderByNome(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT c FROM CategoriaJpaEntity c WHERE c.usuario.id = :usuarioId AND c.ativa = true ORDER BY c.nome ASC")
    List<CategoriaJpaEntity> findByUsuarioIdAndAtivaOrderByNome(@Param("usuarioId") Long usuarioId);
}