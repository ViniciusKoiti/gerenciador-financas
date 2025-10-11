package com.vinicius.gerenciamento_financeiro.application.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.BusinessRuleViolationException;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.ContextoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
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
    private final UsuarioAutenticadoPort usuarioAutenticado;
    private final UsuarioRepository usuarioRepository;

    @Override
    public Categoria save(CategoriaPost request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não pode ser nula");
        }

        try {
            UsuarioId usuarioId = usuarioAutenticado.obterUsuarioAtual();
            ContextoUsuario contexto = usuarioAutenticado.obterContextoParaLogs();


            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new BusinessRuleViolationException("USUARIO_NAO_ENCONTRADO",
                            "Usuário não encontrado: " + usuarioId.getValue()));



            Categoria categoria = Categoria.criar(
                    request.name(),
                    request.description(),
                    request.icon(),
                    usuarioId
            );

            Categoria categoriaSalva = categoriaRepository.save(categoria);
            log.debug("Categoria criada com sucesso: ID {}", categoriaSalva.getId().getValue());

            return categoriaSalva;

        } catch (Exception e) {
            log.error("Erro ao criar categoria: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Categoria findById(String id) {
        try {
            CategoriaId categoriaId = CategoriaId.of(Long.valueOf(id));
            log.debug("Buscando categoria por ID: {}", categoriaId.getValue());

            return categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

        } catch (NumberFormatException e) {
            log.error("ID inválido fornecido: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }

    @Override
    public List<Categoria> findCategoriasByUser(Long usuarioIdRaw) {
        log.debug("Buscando categorias para usuário: {}", usuarioIdRaw);

        UsuarioId usuarioId = UsuarioId.of(usuarioIdRaw);
        List<Categoria> categorias = categoriaRepository.findByUsuarioId(usuarioId);

        return new ArrayList<>(categorias);
    }

    @Override
    public Page<Categoria> findAllPaginated(Pageable pageable) {
        log.debug("Buscando categorias paginadas: página {}", pageable.getPageNumber());

        UsuarioId usuarioId = usuarioAutenticado.obterUsuarioAtual();

        return categoriaRepository.findByUsuarioId(usuarioId, pageable);
    }

    @Override
    public void deletarCategoria(String id) {
        try {
            CategoriaId categoriaId = CategoriaId.of(Long.valueOf(id));
            log.debug("Deletando categoria: ID {}", categoriaId.getValue());

            // ✅ Verificar se categoria existe (usando domínio)
            categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));


            categoriaRepository.deleteById(categoriaId.getValue());
            log.info("Categoria deletada com sucesso: ID {}", categoriaId.getValue());

        } catch (NumberFormatException e) {
            log.error("ID inválido fornecido para deleção: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido: " + id);
        }
    }

    @Override
    public List<Categoria> findAll() {
        log.debug("Buscando todas as categorias");

        UsuarioId usuarioId = usuarioAutenticado.obterUsuarioAtual();

        List<Categoria> categorias = categoriaRepository.findByUsuarioId(usuarioId);

        return new ArrayList<>(categorias);
    }
}