package com.vinicius.gerenciamento_financeiro.port.out.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import java.util.List;
import java.util.Optional;

public interface CategoriaRepository {
    Categoria save(Categoria entity);
    Optional<Categoria> findById(Long id);
    List<Categoria> findAll();
    void deleteById(Long id);
}
