package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.response.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "senha", ignore = true)
    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    Usuario toEntity(UsuarioPost request);
}
