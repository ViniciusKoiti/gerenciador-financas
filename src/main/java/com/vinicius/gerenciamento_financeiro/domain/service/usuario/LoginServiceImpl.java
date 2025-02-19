package com.vinicius.gerenciamento_financeiro.domain.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginUseCase {
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final UsuarioMapper mapper;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse autenticar(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.senha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        var token = jwtService.gerarToken(usuario, usuario.getId());
        return new AuthenticationResponse(token, mapper.toResponse(usuario));
    }

}
