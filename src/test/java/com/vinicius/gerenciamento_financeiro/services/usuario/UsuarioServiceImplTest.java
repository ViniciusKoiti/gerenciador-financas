package com.vinicius.gerenciamento_financeiro.services.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.usuario.UsuarioServiceImpl;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void save_DeveRetornarUsuarioResponse_QuandoDadosValidados(){
        UsuarioPost usuarioPost = new UsuarioPost("João", "joao@email.com", "senha123");
        String senhaEncriptada = "senha_encriptada";
        Usuario usuarioEsperado = Usuario.builder()
                .nome("João")
                .email("joao@email.com")
                .senha(senhaEncriptada)
                .build();
        UsuarioResponse usuarioResponse = new UsuarioResponse(1L, "João", "joao@email.com", "senha123");
        when(usuarioRepository.existsByEmail(usuarioPost.email())).thenReturn(false);
        when(passwordEncoder.encode(usuarioPost.senha())).thenReturn(senhaEncriptada);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioEsperado);
        when(usuarioMapper.toResponse(usuarioEsperado)).thenReturn(usuarioResponse);
        AuthenticationResponse response = usuarioService.save(usuarioPost);

        assertThat(response).isNotNull();

        verify(passwordEncoder).encode(usuarioPost.senha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void save_DeveLancarException_QuandoEmailJaExiste() {
        UsuarioPost usuarioPost = new UsuarioPost("João", "existente@email.com", "senha123");

        when(usuarioRepository.existsByEmail(usuarioPost.email())).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.save(usuarioPost))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email já cadastrado");

        verify(usuarioRepository, never()).save(any());
    }


    @Test
    void findById_DeveLancarException_QuandoUsuarioNaoExiste() {
        Long id = 999L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> usuarioService.findById(id))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }



}
