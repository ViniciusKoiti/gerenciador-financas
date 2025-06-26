package com.vinicius.gerenciamento_financeiro.port.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface CategoriaRepository {
    Categoria save(Categoria entity);
    void saveAll(List<Categoria> listaDeCategorias);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    List<Categoria> findByUsuarioId(Long usuarioId);

    Page<Categoria> findAll(Pageable pageable);

    boolean existsByIdAndUsuarioId(Long categoriaId, Long usuarioId);

    void deleteById(Long id);

    Optional<Categoria> findByIdAndUsuarioId(Long categoriaId, Long usuarioId);
    Page<Categoria> findByUsuarioId(Long usuarioId, Pageable pageable);
}
