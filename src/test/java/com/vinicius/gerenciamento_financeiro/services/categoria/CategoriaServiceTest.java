package com.vinicius.gerenciamento_financeiro.services.categoria;


import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.categoria.CategoriaPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.categoria.CategoriaResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.categoria.CategoriaService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CategoriaMapper categoriaMapper;

    @Mock
    private JwtService jwtService;

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

        CategoriaJpaEntity categoriaJpaEntity = CategoriaJpaEntity.builder()
                .id(1L)
                .nome("Alimentação")
                .descricao("Gastos com alimentação")
                .ativa(true)
                .icone("food-icon")
                .build();


        CategoriaResponse expectedResponse = new CategoriaResponse(
                1L,
                "Alimentação Modificada",
                "Modificado",
                true,
                "new-icon",
                null
        );

        when(categoriaRepository.save(any(CategoriaJpaEntity.class))).thenReturn(categoriaJpaEntity);
        when(categoriaMapper.toResponse(any(CategoriaJpaEntity.class))).thenReturn(expectedResponse);
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(new Usuario(1L)));

        CategoriaResponse result = categoriaService.save(categoriaPost);

        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());
        verify(categoriaRepository).save(any(CategoriaJpaEntity.class));
    }

    @Test
    void save_QuandoCategoriaNula_LancaException() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> categoriaService.save(null)
        );
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("CategoriaJpaEntity não pode ser nula", exception.getReason());
        verifyNoInteractions(categoriaMapper, categoriaRepository);
    }

    @Test
    void findById_QuandoIdExiste_RetornaCategoriaResponse() {
        Long id = 1L;
        CategoriaJpaEntity categoriaJpaEntity = CategoriaJpaEntity.builder()
                .id(id)
                .nome("Alimentação")
                .build();
        CategoriaResponse expectedResponse = new CategoriaResponse(
                id,
                "Alimentação",
                "Descrição",
                true,
                "icon",
                null
        );
        when(categoriaRepository.findById(id)).thenReturn(Optional.of(categoriaJpaEntity));
        when(categoriaMapper.toResponse(categoriaJpaEntity)).thenReturn(expectedResponse);
        CategoriaResponse result = categoriaService.findById(id.toString());
        assertNotNull(result);
        assertEquals(expectedResponse.id(), result.id());
        assertEquals(expectedResponse.name(), result.name());
        verify(categoriaRepository).findById(id);
        verify(categoriaMapper).toResponse(categoriaJpaEntity);
        verifyNoMoreInteractions(categoriaRepository, categoriaMapper);
    }
}
