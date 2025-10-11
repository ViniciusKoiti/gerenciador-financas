package com.vinicius.gerenciamento_financeiro.adapter.in.web.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Credencial;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.SessaoUsuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orquestra opera��oes HTTP de usuario convertendo DTOs em entidades de dominio.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioWebService {

    private final UsuarioService usuarioService;
    private final LoginUseCase loginUseCase;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse registrarUsuario(UsuarioPost usuarioPost) {
        log.info("Registrando novo usuario: {}", usuarioPost.email());

        String hashSenha = passwordEncoder.encode(usuarioPost.senha());
        Usuario usuario = usuarioMapper.toEntity(usuarioPost, hashSenha);
        Usuario usuarioRegistrado = usuarioService.registrar(usuario);

        Credencial credencial = Credencial.criar(usuarioPost.email(), usuarioPost.senha());
        SessaoUsuario sessao = loginUseCase.autenticar(credencial);

        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(sessao.getUsuario());
        return new AuthenticationResponse(sessao.getToken(), usuarioResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        log.debug("Buscando usuario por ID: {}", id);

        UsuarioId usuarioId = UsuarioId.of(id);
        Usuario usuario = usuarioService.buscarPorId(usuarioId);

        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public AuthenticationResponse autenticar(LoginRequest loginRequest) {
        log.info("Autenticando usuario: {}", loginRequest.email());

        Credencial credencial = Credencial.criar(loginRequest.email(), loginRequest.senha());
        SessaoUsuario sessao = loginUseCase.autenticar(credencial);

        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(sessao.getUsuario());
        return new AuthenticationResponse(sessao.getToken(), usuarioResponse);
    }
}
