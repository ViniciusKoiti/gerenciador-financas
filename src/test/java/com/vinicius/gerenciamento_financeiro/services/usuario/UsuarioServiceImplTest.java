package com.vinicius.gerenciamento_financeiro.services.usuario;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.UsuarioMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.LoginRequest;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.usuario.UsuarioPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.AuthenticationResponse;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.autenticacao.UsuarioResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.service.usuario.UsuarioServiceImpl;
import com.vinicius.gerenciamento_financeiro.port.in.LoginUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LoginUseCase loginUseCase;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void save_DeveRetornarUsuarioResponse_QuandoDadosValidados() {
        UsuarioPost usuarioPost = new UsuarioPost("João", "joao@email.com", "senha123");
        String senhaEncriptada = "senha_encriptada";

        Usuario usuarioNovo = Usuario.criarNovo("João", new Email("joao@email.com"), senhaEncriptada);
        Usuario usuarioSalvo = Usuario.reconstituir(
                1L,
                "João",
                "joao@email.com",
                senhaEncriptada,
                null,
                null
        );

        LoginRequest loginRequest = LoginRequest.builder()
                .email(usuarioPost.email())
                .senha(usuarioPost.senha())
                .build();

        UsuarioResponse usuarioResponse = new UsuarioResponse(1L, "João", "joao@email.com", null);
        AuthenticationResponse expectedResponse = new AuthenticationResponse("token-valido", usuarioResponse);

        when(usuarioRepository.existsByEmail(usuarioPost.email())).thenReturn(false);
        when(passwordEncoder.encode(usuarioPost.senha())).thenReturn(senhaEncriptada);
        when(usuarioMapper.toEntity(usuarioPost, senhaEncriptada)).thenReturn(usuarioNovo);
        when(usuarioRepository.save(usuarioNovo)).thenReturn(usuarioSalvo);
        when(loginUseCase.autenticar(loginRequest)).thenReturn(expectedResponse);

        AuthenticationResponse response = usuarioService.save(usuarioPost);

        assertThat(response).isNotNull();
        assertThat(response.token()).isEqualTo("token-valido");

        verify(passwordEncoder).encode(usuarioPost.senha());
        verify(usuarioRepository).save(usuarioNovo);
        verify(categoriaRepository).saveAll(any());
    }

    @Test
    void save_DeveLancarException_QuandoEmailJaExiste() {
        UsuarioPost usuarioPost = new UsuarioPost("João", "existente@email.com", "senha123");

        when(usuarioRepository.existsByEmail(usuarioPost.email())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.save(usuarioPost)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void findById_DeveLancarException_QuandoUsuarioNaoExiste() {
        Long id = 999L;
        UsuarioId usuarioId = UsuarioId.of(id);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.findById(id))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    void findById_DeveRetornarUsuario_QuandoUsuarioExiste() {
        Long id = 1L;
        UsuarioId usuarioId = UsuarioId.of(id);

        Usuario usuario = Usuario.reconstituir(
                id,
                "João",
                "joao@email.com",
                "senhaHash",
                null,
                null
        );

        UsuarioResponse expectedResponse = new UsuarioResponse(id, "João", "joao@email.com", null);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(expectedResponse);

        UsuarioResponse response = usuarioService.findById(id);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.nome()).isEqualTo("João");
        assertThat(response.email()).isEqualTo("joao@email.com");
    }
}