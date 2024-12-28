package com.vinicius.gerenciamento_financeiro.domain.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {
    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse save(CategoriaPost entity) {
        return null;
    }

    public CategoriaResponse findById(Long id) {
        return null;
    }

    @Override
    public Page<CategoriaResponse> findAllPaginated(Pageable pageable) {
        return null;
    }

    @Override
    public void deletarCategoria(Long id) {

    }

    public List<CategoriaResponse> findAll() {
        return List.of();
    }

    public void d(Long id) {
    }
}
