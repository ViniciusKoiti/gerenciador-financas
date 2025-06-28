package com.vinicius.gerenciamento_financeiro.domain.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    public CategoriaResponse save(CategoriaPost entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não pode ser nula");
        }

        try {
            Long usuarioId = jwtService.getByAutenticaoUsuarioId();
            log.debug("Criando categoria para usuário: {}", usuarioId);

            Usuario usuario = usuarioRepository.findById(UsuarioId.of(usuarioId))
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

            UsuarioJpaEntity usuarioJpa = UsuarioJpaEntity.builder()
                    .id(usuarioId)
                    .email(usuario.getEmail())
                    .nome(usuario.getNome())
                    .senha(usuario.getHashSenha())
                    .build();

            CategoriaJpaEntity categoriaJpaEntity = new CategoriaJpaEntity(
                    entity.name(),
                    entity.description(),
                    entity.icon(),
                    usuarioJpa
            );

            CategoriaJpaEntity categoriaSalva = categoriaRepository.save(categoriaJpaEntity);
            log.debug("Categoria criada com sucesso: ID {}", categoriaSalva.getId());

            return categoriaMapper.toResponse(categoriaSalva);

        } catch (Exception e) {
            log.error("Erro ao criar categoria: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public CategoriaResponse findById(String id) {
        try {
            Long categoriaId = Long.valueOf(id);
            log.debug("Buscando categoria por ID: {}", categoriaId);

            return categoriaRepository.findById(categoriaId)
                    .map(categoriaMapper::toResponse)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

        } catch (NumberFormatException e) {
            log.error("ID inválido fornecido: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }

    @Override
    public List<CategoriaResponse> findCategoriasByUser(Long usuarioId) {
        log.debug("Buscando categorias para usuário: {}", usuarioId);

        List<CategoriaJpaEntity> categoriaJpaEntities = categoriaRepository.findByUsuarioId(usuarioId);

        return categoriaJpaEntities.stream()
                .map(categoriaMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CategoriaResponse> findAllPaginated(Pageable pageable) {
        log.debug("Buscando categorias paginadas: página {}", pageable.getPageNumber());

        return categoriaRepository.findAll(pageable)
                .map(categoriaMapper::toResponse);
    }

    @Override
    public void deletarCategoria(String id) {
        try {
            Long categoriaId = Long.valueOf(id);
            log.debug("Deletando categoria: ID {}", categoriaId);

            CategoriaJpaEntity categoriaJpaEntity = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

            // TODO: Verificar se categoria pode ser deletada (sem transações associadas)

            categoriaRepository.deleteById(categoriaJpaEntity.getId());
            log.info("Categoria deletada com sucesso: ID {}", categoriaId);

        } catch (NumberFormatException e) {
            log.error("ID inválido fornecido para deleção: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }

    @Override
    public List<CategoriaResponse> findAll() {
        log.debug("Buscando todas as categorias");

        List<CategoriaJpaEntity> categoriaJpaEntities = categoriaRepository.findAll();
        List<CategoriaResponse> categoriaResponses = new ArrayList<>();

        for (CategoriaJpaEntity categoriaJpaEntity : categoriaJpaEntities) {
            CategoriaResponse categoriaResponse = categoriaMapper.toResponse(categoriaJpaEntity);
            categoriaResponses.add(categoriaResponse);
        }

        return categoriaResponses;
    }
}