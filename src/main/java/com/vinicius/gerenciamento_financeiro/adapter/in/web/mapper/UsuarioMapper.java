package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    default UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId() != null ? usuario.getId().getValue() : null)
                .nome(usuario.getNome())
                .email(usuario.getEmail().getEndereco())
                .build();
    }

    default Usuario toEntity(UsuarioPost request, String hashSenha) {
        return Usuario.criarNovo(
                request.nome(),
                new Email(request.email()),
                hashSenha
        );
    }

    default Usuario atualizarUsuario(Usuario usuarioExistente, UsuarioPut put) {
        String novoNome = put.nome() != null ? put.nome() : usuarioExistente.getNome();
        Email novoEmail = put.email() != null ? new Email(put.email()) : usuarioExistente.getEmail();

        return usuarioExistente.atualizarDados(novoNome, novoEmail);
    }
}