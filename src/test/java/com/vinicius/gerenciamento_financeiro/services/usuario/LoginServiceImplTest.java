package com.vinicius.gerenciamento_financeiro.services.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.SpringUserDetails;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.application.service.usuario.LoginServiceImpl;
import com.vinicius.gerenciamento_financeiro.port.out.autorizacao.TokenService;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenService jwtService;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void autenticar_DeveRetornarToken_QuandoCredenciaisValidas() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "senha123");

        Usuario usuario = Usuario.reconstituir(
                1L,
                "Nome Teste",
                "test@email.com",
                "senhaHash",
                null,
                null);

        UsuarioResponse usuarioResponse = new UsuarioResponse(1L, "Nome Teste", "test@email.com", null);
        String expectedToken = "jwt-token";

        SpringUserDetails userDetails = new SpringUserDetails(usuario);

        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        // Mock the authentication manager to return the authenticated object
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authenticatedAuth);

        // Mock JWT service
        when(jwtService.gerarToken(usuario))
                .thenReturn(expectedToken);

        when(usuarioMapper.toResponse(usuario))
                .thenReturn(usuarioResponse);

        AuthenticationResponse response = loginService.autenticar(loginRequest);

        assertThat(response.token()).isEqualTo(expectedToken);
        assertThat(response.user()).isEqualTo(usuarioResponse);

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth instanceof UsernamePasswordAuthenticationToken &&
                                auth.getPrincipal().equals(loginRequest.email()) &&
                                auth.getCredentials().equals(loginRequest.senha())
                )
        );

        verify(jwtService).gerarToken(usuario);
        verify(usuarioMapper).toResponse(usuario);
    }
    @Test
    void autenticar_DeveLancarException_QuandoUsuarioNaoEncontrado() {
        LoginRequest loginRequest = new LoginRequest("inexistente@email.com", "senha123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> loginService.autenticar(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Bad credentials");
    }

    @Test
    void autenticar_DeveLancarException_QuandoCredenciaisInvalidas() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "senhaerrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> loginService.autenticar(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");
    }
}