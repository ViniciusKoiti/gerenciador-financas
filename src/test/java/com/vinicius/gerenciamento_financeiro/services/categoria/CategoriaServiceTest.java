package com.vinicius.gerenciamento_financeiro.services.categoria;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.domain.exception.BusinessRuleViolationException;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.application.service.categoria.CategoriaService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.in.UsuarioAutenticadoPort;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @Mock
    private UsuarioAutenticadoPort usuarioAutenticado;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void save_QuandoCategoriaValida_RetornaCategoriaResponse() {
        CategoriaPost categoriaPost = new CategoriaPost(
                "Alimentação",
                "Gastos com alimentação",
                "food-icon",
                null
        );

        Usuario usuario = Usuario.reconstituir(
                1L,
                "João",
                "joao@teste.com",
                "senhaHash",
                null,
                null
        );

        Categoria categoria = Categoria.reconstituir(
                CategoriaId.of(1L),
                "Alimentação",
                "Gastos com alimentação",
                true,
                "food-icon",
                UsuarioId.of(1L),
                LocalDateTime.now(),
                null,
                null,
                Set.of()
        );

        CategoriaResponse expectedResponse = new CategoriaResponse(
                1L,
                "Alimentação",
                "Gastos com alimentação",
                true,
                "food-icon",
                null
        );

        when(usuarioAutenticado.obterUsuarioAtual()).thenReturn(UsuarioId.of(1L));
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuario));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);
        when(categoriaMapper.toResponse(any(Categoria.class))).thenReturn(expectedResponse);

        CategoriaResponse result = categoriaService.save(categoriaPost);

        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void save_QuandoCategoriaNula_LancaException() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> categoriaService.save(null)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Categoria não pode ser nula", exception.getReason());
        verifyNoInteractions(categoriaMapper, categoriaRepository);
    }

    @Test
    void findById_QuandoIdExiste_RetornaCategoriaResponse() {
        CategoriaId id = CategoriaId.of(1L);
        Categoria categoria = Categoria.reconstituir(
                CategoriaId.of(1L),
                "Alimentação",
                "Gastos com alimentação",
                true,
                "food-icon",
                UsuarioId.of(1L),
                LocalDateTime.now(),
                null,
                null,
                Set.of()
        );

        CategoriaResponse expectedResponse = new CategoriaResponse(
                id.getValue(),
                "Alimentação",
                "Gastos com alimentação",
                true,
                "food-icon",
                null
        );

        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoria));
        when(categoriaMapper.toResponse(categoria)).thenReturn(expectedResponse);

        CategoriaResponse result = categoriaService.findById(id.getValue().toString());

        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());
        verify(categoriaRepository).findById(id);
        verify(categoriaMapper).toResponse(categoria);
        verifyNoMoreInteractions(categoriaRepository, categoriaMapper);
    }

    @Test
    void save_QuandoUsuarioNaoEncontrado_LancaException() {
        CategoriaPost categoriaPost = new CategoriaPost(
                "Alimentação",
                "Gastos com alimentação",
                "food-icon",
                null
        );


        when(usuarioAutenticado.obterUsuarioAtual()).thenReturn(UsuarioId.of(999L));

        BusinessRuleViolationException exception = assertThrows(
                BusinessRuleViolationException.class,
                () -> categoriaService.save(categoriaPost)
        );

        assertEquals("Usuário não encontrado: 999", exception.getMessage());
        verify(categoriaRepository, never()).save(any());
    }
}