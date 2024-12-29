package com.vinicius.gerenciamento_financeiro.services.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.usuario.LoginServiceImpl;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void autenticar_DeveRetornarToken_QuandoCredenciaisValidas() {
        LoginRequest loginRequest = new LoginRequest("test@email.com", "senha123");
        Usuario usuario = Usuario.builder().id(1L).build();

        UsuarioResponse usuarioResponse = new UsuarioResponse(1L, "Nome", "test@email.com", "senha123");
        String expectedToken = "jwt-token";

        when(usuarioRepository.findByEmail(loginRequest.email()))
                .thenReturn(Optional.of(usuario));
        when(jwtService.gerarToken(usuario, usuario.getId()))
                .thenReturn(expectedToken);
        when(usuarioMapper.toResponse(usuario))
                .thenReturn(usuarioResponse);
        AuthenticationResponse response = loginService.autenticar(loginRequest);
        assertThat(response.token()).isEqualTo(expectedToken);
        assertThat(response.usuario()).isEqualTo(usuarioResponse);

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals(loginRequest.email()) &&
                                auth.getCredentials().equals(loginRequest.senha())
                )
        );
    }

    @Test
    void autenticar_DeveLancarException_QuandoUsuarioNaoEncontrado() {
        LoginRequest loginRequest = new LoginRequest("inexistente@email.com", "senha123");
        when(usuarioRepository.findByEmail(loginRequest.email()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> loginService.autenticar(loginRequest))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void autenticarDeveLancarException_QuandoCredenciaisInvalidas(){
        LoginRequest loginRequest = new LoginRequest("test@email.com", "senhaerrada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        assertThatThrownBy(() -> loginService.autenticar(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Credenciais inválidas");
    }

}
