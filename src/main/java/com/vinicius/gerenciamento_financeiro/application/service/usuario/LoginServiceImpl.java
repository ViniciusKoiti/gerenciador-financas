package com.vinicius.gerenciamento_financeiro.application.service.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtTokenService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.SpringUserDetails;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginUseCase {
    private final JwtTokenService jwtService;
    private final UsuarioMapper mapper;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse autenticar(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(),
                        loginRequest.senha()
                )
        );

        SpringUserDetails userDetails = (SpringUserDetails) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();

        String token = jwtService.gerarToken(usuario);
        return new AuthenticationResponse(token, mapper.toResponse(usuario));
    }
}