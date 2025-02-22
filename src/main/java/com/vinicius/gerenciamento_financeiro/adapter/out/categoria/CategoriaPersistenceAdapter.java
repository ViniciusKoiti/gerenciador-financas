package com.vinicius.gerenciamento_financeiro.adapter.out.categoria;

import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoriaPersistenceAdapter implements CategoriaRepository {
    private final JpaCategoriaRepository repository;

    @Override
    public Categoria save(Categoria entity) {
        return repository.save(entity);
    }

    public void saveAll(List<Categoria> listaCategoria) { repository.saveAll(listaCategoria);}

    @Override
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Categoria> findByUsuarioId(Long id) {
        return repository.findCategoriasByUsuarioId(id);
    }

    @Override

    public List<Categoria> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<Categoria> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsByIdAndUsuarioId(Long categoriaId, Long usuarioId) {
        return repository.existsByUsuarioIdAndId(categoriaId,usuarioId);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
