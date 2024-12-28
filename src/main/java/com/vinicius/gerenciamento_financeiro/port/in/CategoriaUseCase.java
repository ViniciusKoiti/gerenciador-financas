package com.vinicius.gerenciamento_financeiro.port.in;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;

import java.util.List;

public interface CategoriaUseCase {

     CategoriaResponse save(Categoria categoria);
     CategoriaResponse findById(Long id);
     List<CategoriaResponse> findAll(Long id);
     void deletarCategoria(Long id);

}
