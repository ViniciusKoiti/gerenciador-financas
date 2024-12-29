package com.vinicius.gerenciamento_financeiro.domain.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaResponse save(CategoriaPost entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não pode ser nula");
        }

        return categoriaMapper.toResponse(categoriaRepository.save(categoriaMapper.toEntity(entity)));
    }
    public CategoriaResponse findById(String id){


        try {
            return categoriaRepository.findById(Long.valueOf(id))
                    .map(categoriaMapper::toResponse)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }


    @Override
    public Page<CategoriaResponse> findAllPaginated(Pageable pageable) {
        return categoriaRepository
                .findAll(pageable)
                .map(categoriaMapper::toResponse);
    }

    @Override
    public void deletarCategoria(String id) {
        try {
            Categoria categoria = categoriaRepository.findById(Long.valueOf(id))
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

            categoriaRepository.deleteById(categoria.getId());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }
    @Override
    public List<CategoriaResponse> findAll() {
        List<Categoria> categorias = categoriaRepository.findAll();
        List<CategoriaResponse> categoriaResponses = new ArrayList<>();
        for (Categoria categoria :
                categorias) {
            CategoriaResponse categoriaResponse = categoriaMapper.toResponse(categoria);
            categoriaResponses.add(categoriaResponse);
        }
        return categoriaResponses;
    }
}
