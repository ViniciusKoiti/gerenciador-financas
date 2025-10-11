package com.vinicius.gerenciamento_financeiro.adapter.in.web.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.categoria.CategoriaRequestMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.categoria.CategoriaResponseMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de adapta��ao web para opera��oes de categoria.
 * Converte DTOs HTTP em tipos de dominio e delega para os casos de uso.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CategoriaWebService {

    private final CategoriaUseCase categoriaUseCase;
    private final CategoriaRequestMapper requestMapper;
    private final CategoriaResponseMapper responseMapper;
    private final UsuarioAutenticadoPort usuarioAutenticadoPort;

    @Transactional
    public CategoriaResponse criarCategoria(CategoriaPost request) {
        log.info("Criando nova categoria: {}", request.name());

        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
        Categoria categoria = requestMapper.toDomain(request, usuarioId);
        Categoria categoriaCriada = categoriaUseCase.criar(categoria);

        return responseMapper.toResponse(categoriaCriada);
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(String id) {
        log.debug("Buscando categoria por ID: {}", id);

        CategoriaId categoriaId = CategoriaId.of(Long.valueOf(id));
        Categoria categoria = categoriaUseCase.buscarPorId(categoriaId);

        return responseMapper.toResponse(categoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarTodas() {
        log.debug("Listando todas as categorias do usuario autenticado");

        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
        List<Categoria> categorias = categoriaUseCase.listarPorUsuario(usuarioId);

        return responseMapper.toResponseList(categorias);
    }

    @Transactional(readOnly = true)
    public Page<CategoriaResponse> listarPaginado(Pageable pageable) {
        log.debug("Listando categorias paginadas: pagina {}", pageable.getPageNumber());

        UsuarioId usuarioId = usuarioAutenticadoPort.obterUsuarioAtual();
        Page<Categoria> categorias = categoriaUseCase.listarPaginado(usuarioId, pageable);

        return categorias.map(responseMapper::toResponse);
    }

    @Transactional
    public void deletarCategoria(String id) {
        log.info("Deletando categoria: {}", id);

        CategoriaId categoriaId = CategoriaId.of(Long.valueOf(id));
        categoriaUseCase.remover(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarPorUsuario(Long usuarioIdRaw) {
        log.debug("Listando categorias para usuario: {}", usuarioIdRaw);

        UsuarioId usuarioId = UsuarioId.of(usuarioIdRaw);
        List<Categoria> categorias = categoriaUseCase.listarPorUsuario(usuarioId);

        return responseMapper.toResponseList(categorias);
    }
}
