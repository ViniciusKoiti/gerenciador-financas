package com.vinicius.gerenciamento_financeiro.port.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
public interface CategoriaRepository {
    Categoria save(Categoria entity);
    void saveAll(List<Categoria> listaDeCategoriaJpaEntities);
    Optional<Categoria> findById(CategoriaId id);
    List<Categoria> findAll();
    List<Categoria> findByUsuarioId(UsuarioId usuarioId);

    Page<Categoria> findAll(Pageable pageable);

    boolean existsByIdAndUsuarioId(CategoriaId categoriaId,UsuarioId usuarioId);

    void deleteById(Long id);

    Optional<Categoria> findByIdAndUsuarioId(CategoriaId categoriaId, UsuarioId usuarioId);
    Page<Categoria> findByUsuarioId(UsuarioId usuarioId, Pageable pageable);
}
