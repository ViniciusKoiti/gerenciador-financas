package com.vinicius.gerenciamento_financeiro.port.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.CategoriaJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface CategoriaRepository {
    CategoriaJpaEntity save(CategoriaJpaEntity entity);
    void saveAll(List<CategoriaJpaEntity> listaDeCategoriaJpaEntities);
    Optional<CategoriaJpaEntity> findById(Long id);
    List<CategoriaJpaEntity> findAll();
    List<CategoriaJpaEntity> findByUsuarioId(Long usuarioId);

    Page<CategoriaJpaEntity> findAll(Pageable pageable);

    boolean existsByIdAndUsuarioId(Long categoriaId, Long usuarioId);

    void deleteById(Long id);

    Optional<CategoriaJpaEntity> findByIdAndUsuarioId(Long categoriaId, Long usuarioId);
    Page<CategoriaJpaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);
}
