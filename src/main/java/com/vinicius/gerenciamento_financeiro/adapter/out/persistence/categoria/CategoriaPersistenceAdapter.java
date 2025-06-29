package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoriaPersistenceAdapter implements CategoriaRepository {

    private final JpaCategoriaRepository repository;
    private final CategoriaJpaMapper categoriaJpaMapper;

    @Override
    public Categoria save(Categoria categoria) {
        try {
            log.debug("Salvando categoria: ID {}, Nome: {}",
                    categoria.getId() != null ? categoria.getId().getValue() : "nova",
                    categoria.getNome());

            CategoriaJpaEntity jpaEntity;

            if (categoria.isNova()) {
                UsuarioJpaEntity usuarioJpa = UsuarioJpaEntity.builder()
                        .id(categoria.getUsuarioId().getValue())
                        .build();
                jpaEntity = categoriaJpaMapper.toJpaEntity(categoria, usuarioJpa);
                log.debug("Criando nova categoria");
            } else {
                jpaEntity = repository.findById(categoria.getId().getValue())
                        .orElseThrow(() -> new IllegalStateException("Categoria não encontrada para atualização: " + categoria.getId().getValue()));

                categoriaJpaMapper.updateJpaEntity(jpaEntity, categoria);
                log.debug("Atualizando categoria existente: ID {}", categoria.getId().getValue());
            }

            CategoriaJpaEntity savedEntity = repository.save(jpaEntity);
            Categoria categoriaSalva = categoriaJpaMapper.toDomainEntity(savedEntity);

            log.debug("Categoria salva com sucesso: ID {}, Nome: {}",
                    savedEntity.getId(), savedEntity.getNome());

            return categoriaSalva;

        } catch (Exception e) {
            log.error("Erro ao salvar categoria: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void saveAll(List<Categoria> categorias) {
        try {
            List<CategoriaJpaEntity> categoriaJpaEntities = categorias.stream()
                    .map(categoria -> {
                        UsuarioJpaEntity usuarioJpa = UsuarioJpaEntity.builder()
                                .id(categoria.getUsuarioId().getValue())
                                .build();
                        return categoriaJpaMapper.toJpaEntity(categoria, usuarioJpa);
                    })
                    .collect(Collectors.toList());

            repository.saveAll(categoriaJpaEntities);
            log.debug("Salvas {} categorias em lote", categorias.size());

        } catch (Exception e) {
            log.error("Erro ao salvar categorias em lote: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Override
    public Optional<Categoria> findById(CategoriaId id) {
        log.debug("Buscando categoria por ID: {}", id.getValue());

        return repository.findById(id.getValue())
                .map(categoriaJpaMapper::toDomainEntity);
    }

    @Override
    public List<Categoria> findByUsuarioId(UsuarioId id) {
        log.debug("Buscando categoriaJpaEntities para usuário: {}", id);
        return repository.findByUsuarioIdAndAtivaOrderByNome(id.getValue())
                .stream()
                .map(categoriaJpaMapper::toDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Deprecated(since = "Utilize algum filtro para buscar todos")
    public List<Categoria> findAll() {
        log.debug("Buscando todas as categoriaJpaEntities");
        return repository.findAll().stream().map(categoriaJpaMapper::toDomainEntity).collect(Collectors.toList());
    }

    @Override
    public Page<Categoria> findAll(Pageable pageable) {
        log.debug("Buscando categoriaJpaEntities paginadas: página {}", pageable.getPageNumber());
        return repository.findAll(pageable).map(categoriaJpaMapper::toDomainEntity);
    }

    @Override
    public boolean existsByIdAndUsuarioId(CategoriaId categoriaId, UsuarioId usuarioId) {
        boolean exists = repository.existsByUsuarioIdAndId(usuarioId.getValue(), categoriaId.getValue());
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
    public Optional<Categoria> findByIdAndUsuarioId(CategoriaId categoriaId, UsuarioId usuarioId) {
        log.debug("Buscando categoriaJpaEntity {} para usuário: {}", categoriaId, usuarioId);
        return repository.findByIdAndUsuario_id(categoriaId.getValue(), usuarioId.getValue())
                .map(categoriaJpaMapper::toDomainEntity);
    }

    @Override
    public Page<Categoria> findByUsuarioId(UsuarioId usuarioId, Pageable pageable) {
        log.debug("Buscando categoriaJpaEntities paginadas para usuário: {}, página: {}", usuarioId, pageable.getPageNumber());
        return repository.findByUsuarioIdAndAtivaOrderByNome(usuarioId.getValue(), pageable)
                .map(categoriaJpaMapper::toDomainEntity);
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