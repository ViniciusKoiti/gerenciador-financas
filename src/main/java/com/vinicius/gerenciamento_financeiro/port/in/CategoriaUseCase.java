package com.vinicius.gerenciamento_financeiro.port.in;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaUseCase {

     Categoria save(CategoriaPost categoria);
     Categoria findById(String id);
     List<Categoria> findAll();

     List<Categoria> findCategoriasByUser(Long usuarioId);

     Page<Categoria> findAllPaginated(Pageable pageable);
     void deletarCategoria(String id);

}
