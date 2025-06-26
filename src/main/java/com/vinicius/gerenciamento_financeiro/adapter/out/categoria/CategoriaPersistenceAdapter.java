package com.vinicius.gerenciamento_financeiro.adapter.out.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.categoria.entity.CategoriaJpaEntity;
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
    public CategoriaJpaEntity save(CategoriaJpaEntity entity) {
        try {
            CategoriaJpaEntity savedCategoriaJpaEntity = repository.save(entity);
            log.debug("CategoriaJpaEntity salva com sucesso: ID {}, Nome: {}", savedCategoriaJpaEntity.getId(), savedCategoriaJpaEntity.getNome());
            return savedCategoriaJpaEntity;
        } catch (Exception e) {
            log.error("Erro ao salvar categoriaJpaEntity: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void saveAll(List<CategoriaJpaEntity> listaCategoriaJpaEntity) {
        try {
            repository.saveAll(listaCategoriaJpaEntity);
            log.debug("Salvas {} categoriaJpaEntities em lote", listaCategoriaJpaEntity.size());
        } catch (Exception e) {
            log.error("Erro ao salvar categoriaJpaEntities em lote: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Optional<CategoriaJpaEntity> findById(Long id) {
        log.debug("Buscando categoriaJpaEntity por ID: {}", id);
        return repository.findById(id);
    }

    @Override
    public List<CategoriaJpaEntity> findByUsuarioId(Long id) {
        log.debug("Buscando categoriaJpaEntities para usuário: {}", id);
        return repository.findByUsuarioIdAndAtivaOrderByNome(id);
    }

    @Override
    public List<CategoriaJpaEntity> findAll() {
        log.debug("Buscando todas as categoriaJpaEntities");
        return repository.findAll();
    }

    @Override
    public Page<CategoriaJpaEntity> findAll(Pageable pageable) {
        log.debug("Buscando categoriaJpaEntities paginadas: página {}", pageable.getPageNumber());
        return repository.findAll(pageable);
    }

    @Override
    public boolean existsByIdAndUsuarioId(Long categoriaId, Long usuarioId) {
        boolean exists = repository.existsByUsuarioIdAndId(usuarioId, categoriaId);
        log.debug("CategoriaJpaEntity {} pertence ao usuário {}: {}", categoriaId, usuarioId, exists);
        return exists;
    }

    @Override
    public void deleteById(Long id) {
        try {
            repository.deleteById(id);
            log.debug("CategoriaJpaEntity deletada: ID {}", id);
        } catch (Exception e) {
            log.error("Erro ao deletar categoriaJpaEntity {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
    @Override
    public Optional<CategoriaJpaEntity> findByIdAndUsuarioId(Long categoriaId, Long usuarioId) {
        log.debug("Buscando categoriaJpaEntity {} para usuário: {}", categoriaId, usuarioId);
        return repository.findByIdAndUsuario_id(categoriaId, usuarioId);
    }

    @Override
    public Page<CategoriaJpaEntity> findByUsuarioId(Long usuarioId, Pageable pageable) {
        log.debug("Buscando categoriaJpaEntities paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());
        return repository.findByUsuarioIdAndAtivaOrderByNome(usuarioId, pageable);
    }

    public Optional<CategoriaJpaEntity> findByIdAndUsuarioIdAndAtiva(Long categoriaId, Long usuarioId) {
        log.debug("Buscando categoriaJpaEntity ativa {} para usuário: {}", categoriaId, usuarioId);
        return repository.findByIdAndUsuarioIdAndAtiva(categoriaId, usuarioId);
    }

    public long contarCategoriasPorUsuario(Long usuarioId) {
        return repository.countByUsuario_id(usuarioId);
    }

    public long contarCategoriasAtivasPorUsuario(Long usuarioId) {
        return repository.countByUsuario_idAndAtiva(usuarioId, true);
    }
    public List<CategoriaJpaEntity> findCategoriasAtivasPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdAndAtivaOrderByNome(usuarioId);
    }
}