package com.vinicius.gerenciamento_financeiro.adapter.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findCategoriasByUsuarioId(Long userId);
    boolean existsByUsuarioIdAndId(Long usuarioId, Long id);
    Optional<Categoria> findByIdAndUsuario_id(Long categoriaId, Long usuarioId);
    Page<Categoria> findByUsuario_id(Long usuarioId, Pageable pageable);
    @Query("SELECT c FROM Categoria c WHERE c.id = :categoriaId AND c.usuario.id = :usuarioId AND c.ativa = true")
    Optional<Categoria> findByIdAndUsuarioIdAndAtiva(@Param("categoriaId") Long categoriaId, @Param("usuarioId") Long usuarioId);

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND c.ativa = true ORDER BY c.nome ASC")
    Page<Categoria> findByUsuarioIdAndAtivaOrderByNome(@Param("usuarioId") Long usuarioId, Pageable pageable);

    @Query("SELECT c FROM Categoria c WHERE c.usuario.id = :usuarioId AND c.ativa = true ORDER BY c.nome ASC")
    List<Categoria> findByUsuarioIdAndAtivaOrderByNome(@Param("usuarioId") Long usuarioId);

    long countByUsuario_id(Long usuarioId);
    long countByUsuario_idAndAtiva(Long usuarioId, boolean ativa);
}