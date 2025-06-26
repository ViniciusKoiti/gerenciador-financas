package com.vinicius.gerenciamento_financeiro.adapter.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.Categoria;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoriaPersistenceAdapter implements CategoriaRepository {

    private final JpaCategoriaRepository repository;

    @Override
    public Categoria save(Categoria entity) {
        try {
            Categoria savedCategoria = repository.save(entity);
            log.debug("Categoria salva com sucesso: ID {}, Nome: {}", savedCategoria.getId(), savedCategoria.getNome());
            return savedCategoria;
        } catch (Exception e) {
            log.error("Erro ao salvar categoria: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void saveAll(List<Categoria> listaCategoria) {
        try {
            repository.saveAll(listaCategoria);
            log.debug("Salvas {} categorias em lote", listaCategoria.size());
        } catch (Exception e) {
            log.error("Erro ao salvar categorias em lote: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        log.debug("Buscando categoria por ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public List<Categoria> findByUsuarioId(Long id) {
        log.debug("Buscando categorias para usuário: {}", id);
        return repository.findByUsuarioIdAndAtivaOrderByNome(id);
    }

    @Override
    public List<Categoria> findAll() {
        log.debug("Buscando todas as categorias");
        return repository.findAll();
    }

    @Override
    public Page<Categoria> findAll(Pageable pageable) {
        log.debug("Buscando categorias paginadas: página {}", pageable.getPageNumber());
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsByIdAndUsuarioId(Long categoriaId, Long usuarioId) {
        boolean exists = repository.existsByUsuarioIdAndId(usuarioId, categoriaId);
        log.debug("Categoria {} pertence ao usuário {}: {}", categoriaId, usuarioId, exists);
        return exists;
    }

    @Override
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
            log.debug("Categoria deletada: ID {}", id);
        } catch (Exception e) {
            log.error("Erro ao deletar categoria {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    @Override
    public Optional<Categoria> findByIdAndUsuarioId(Long categoriaId, Long usuarioId) {
        log.debug("Buscando categoria {} para usuário: {}", categoriaId, usuarioId);
        return repository.findByIdAndUsuario_id(categoriaId, usuarioId);
    }

    @Override
    public Page<Categoria> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.debug("Buscando categorias paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());
        return repository.findByUsuarioIdAndAtivaOrderByNome(usuarioId, pageable);
    }

    public Optional<Categoria> findByIdAndUsuarioIdAndAtiva(Long categoriaId, Long usuarioId) {
        log.debug("Buscando categoria ativa {} para usuário: {}", categoriaId, usuarioId);
        return repository.findByIdAndUsuarioIdAndAtiva(categoriaId, usuarioId);
    }

    public long contarCategoriasPorUsuario(Long usuarioId) {
        return repository.countByUsuario_id(usuarioId);
    }

    public long contarCategoriasAtivasPorUsuario(Long usuarioId) {
        return repository.countByUsuario_idAndAtiva(usuarioId, true);
    }
    public List<Categoria> findCategoriasAtivasPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdAndAtivaOrderByNome(usuarioId);
    }
}