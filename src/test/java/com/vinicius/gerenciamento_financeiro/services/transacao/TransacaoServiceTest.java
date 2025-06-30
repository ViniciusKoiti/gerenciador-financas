package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @InjectMocks
    private TransacaoService transacaoService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private NotificarTransacaoService notificarTransacaoService;

    @Mock
    private TransacaoMapper transacaoMapper;

    private Usuario usuario;
    private UsuarioId usuarioId;
    private CategoriaId categoriaId;

    @BeforeEach
    void setUp() {
        usuarioId = UsuarioId.of(1L);
        categoriaId = CategoriaId.of(1L);

        usuario = Usuario.criarNovo("João", new Email("joao@teste.com"), "senhaHash");
        usuario = Usuario.reconstituir(
                1L,
                "João",
                "joao@teste.com",
                "senhaHash",
                null,
                null
        );
    }

    @Test
    void deveAdicionarTransacaoComSucesso() {

        TransacaoPost transacaoPost = new TransacaoPost(
                "Salário",
                BigDecimal.valueOf(1000),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                categoriaId.getValue()
        );

        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId.getValue());
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId))
                .thenReturn(true);

        transacaoService.adicionarTransacao(transacaoPost);

        verify(transacaoRepository, times(1)).salvarTransacao(any(Transacao.class));
        verify(notificarTransacaoService, times(1)).notificarTransacaoAtrasada(any(Transacao.class));
    }

    @Test
    void deveCalcularSaldoCorretamente() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId.getValue());

        Transacao receita = Transacao.criarNova(
                "Salário",
                new BigDecimal("1000"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                categoriaId,
                usuarioId
        );

        Transacao despesa = Transacao.criarNova(
                "Aluguel",
                new BigDecimal("500"),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                categoriaId,
                usuarioId
        );

        List<Transacao> transacoes = Arrays.asList(receita, despesa);
        when(transacaoRepository.buscarTodasTransacoesPorUsuario(usuarioId.getValue()))
                .thenReturn(transacoes);

        BigDecimal saldo = transacaoService.calcularSaldo();

        assertEquals(new BigDecimal("500"), saldo);
    }

    @Test
    void deveBuscarTransacoesPorCategoriaId() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId.getValue());
        when(categoriaRepository.existsByIdAndUsuarioId(categoriaId, usuarioId))
                .thenReturn(true);

        Transacao transacao1 = Transacao.criarNova(
                "Compra no mercado",
                BigDecimal.valueOf(50),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                categoriaId,
                usuarioId
        );

        Transacao transacao2 = Transacao.criarNova(
                "Jantar",
                BigDecimal.valueOf(30),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                categoriaId,
                usuarioId
        );

        List<Transacao> transacoes = Arrays.asList(transacao1, transacao2);

        when(transacaoRepository.buscarTransacoesPorCategoriaIdEUsuario(
                eq(categoriaId.getValue()), eq(usuarioId.getValue())
        )).thenReturn(transacoes);

        when(transacaoMapper.toResponse(any(Transacao.class)))
                .thenReturn(TransacaoResponse.fromEntity(transacao1))
                .thenReturn(TransacaoResponse.fromEntity(transacao2));

        List<TransacaoResponse> resultado = transacaoService.buscarTransacoesPorCategoriaId(categoriaId.getValue());

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(transacaoRepository, times(1))
                .buscarTransacoesPorCategoriaIdEUsuario(categoriaId.getValue(), usuarioId.getValue());
        verify(transacaoMapper, times(2)).toResponse(any(Transacao.class));
    }

    @Test
    void deveAtualizarCategoriaTransacao() {
        Long transacaoId = 1L;
        Long novaCategoriaId = 3L;

        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId.getValue());

        Transacao transacaoExistente = Transacao.reconstituir(
                transacaoId,
                "Transação existente",
                new BigDecimal("100"),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                usuarioId,
                categoriaId,
                null,
                null
        );

        when(transacaoRepository.buscarTransacaoPorIdEUsuario(transacaoId, usuarioId.getValue()))
                .thenReturn(Optional.of(transacaoExistente));
        when(categoriaRepository.existsByIdAndUsuarioId(CategoriaId.of(novaCategoriaId), usuarioId))
                .thenReturn(true);

        transacaoService.atualizarTransacaoCategoria(transacaoId, novaCategoriaId);

        verify(transacaoRepository).salvarTransacao(any(Transacao.class));
    }
}