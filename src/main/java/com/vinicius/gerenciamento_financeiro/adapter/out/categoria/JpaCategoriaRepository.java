package com.vinicius.gerenciamento_financeiro.adapter.out.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface JpaCategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findCategoriasByUsuarioId(Long userId);
}
