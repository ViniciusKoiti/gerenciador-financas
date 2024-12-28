package com.vinicius.gerenciamento_financeiro.domain.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {
    private final CategoriaRepository categoriaRepository;

    public CategoriaResponse save(Categoria entity) {
        return CategoriaResponse.fromEntity(entity);
    }

    public CategoriaResponse findById(Long id) {
        return null;
    }

    @Override
    public List<CategoriaResponse> findAll(Long id) {
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
