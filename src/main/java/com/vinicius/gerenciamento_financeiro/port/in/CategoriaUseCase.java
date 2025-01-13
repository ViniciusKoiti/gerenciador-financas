package com.vinicius.gerenciamento_financeiro.port.in;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoriaUseCase {

     CategoriaResponse save(CategoriaPost categoria);
     CategoriaResponse findById(String id);
     List<CategoriaResponse> findAll();

     List<CategoriaResponse> findCategoriasByUser(Long usuarioId);

     Page<CategoriaResponse> findAllPaginated(Pageable pageable);
     void deletarCategoria(String id);

}
