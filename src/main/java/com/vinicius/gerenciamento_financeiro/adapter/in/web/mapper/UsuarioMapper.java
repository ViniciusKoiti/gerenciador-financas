package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    default UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }

    default Usuario toEntity(UsuarioPost request) {
        return Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .build();
    }

    default Usuario atualizarUsuario(Usuario usuarioExistente, UsuarioPut put) {
        return Usuario.builder()
                .id(usuarioExistente.getId())
                .nome(put.nome() != null ? put.nome() : usuarioExistente.getNome())
                .email(put != null ? put.email() : usuarioExistente.getEmail())
                .senha(usuarioExistente.getSenha())
                .auditoria(usuarioExistente.getAuditoria())
                .build();
    }
}