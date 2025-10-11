package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import org.springframework.stereotype.Component;

/**
 * Mapper para converter DTOs de Request para entidades de Domínio
 */
@Component
public class CategoriaRequestMapper {
    
    public Categoria toDomain(CategoriaPost request, UsuarioId usuarioId) {
        if (request == null) {
            throw new IllegalArgumentException("CategoriaPost não pode ser nulo");
        }
        
        return Categoria.criar(
            request.name(),
            request.description(),
            request.icon(),
            usuarioId
        );
    }
}