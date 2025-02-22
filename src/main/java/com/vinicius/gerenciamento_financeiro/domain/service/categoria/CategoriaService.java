package com.vinicius.gerenciamento_financeiro.domain.service.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.CategoriaUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService implements CategoriaUseCase {
    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public CategoriaResponse save(CategoriaPost entity) {
        if (entity == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não pode ser nula");
        }
        Long usuarioId = jwtService.getByAutenticaoUsuarioId();
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        Categoria categoria = new Categoria(entity.name(), entity.description(), entity.icon(), usuario);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
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

    public List<CategoriaResponse> findCategoriasByUser(Long usuarioId){
        List<Categoria> categorias = categoriaRepository.findByUsuarioId(usuarioId);
        return categorias.stream().map(categoriaMapper::toResponse).collect(Collectors.toList());
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
