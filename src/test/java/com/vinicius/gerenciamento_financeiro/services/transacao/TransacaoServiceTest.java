package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.memory.MemoryTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {
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

    @Test
    void deveAdicionarTransacaoComSucesso() {
        Long usuarioId = 1L;
        Long categoriaId = 2L;
        Usuario usuario = new Usuario(usuarioId);
        Categoria categoria = new Categoria(categoriaId, "Salário", "Descrição","Icone", usuario);
        TransacaoPost transacaoPost = new TransacaoPost("descricao", BigDecimal.valueOf(100), TipoMovimentacao.RECEITA, LocalDateTime.now(), categoriaId);
        Auditoria auditoria = new Auditoria();
        Transacao transacao = new Transacao(10L, "Descricao", BigDecimal.valueOf(100), TipoMovimentacao.RECEITA, LocalDateTime.now(),usuario);
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(usuarioId);
        when(categoriaRepository.findById(categoriaId)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(transacaoMapper.toEntity(eq(transacaoPost), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacao);
        transacaoService.adicionarTransacao(transacaoPost);
        verify(transacaoRepository, times(1)).salvarTransacao(transacao);
        verify(notificarTransacaoService, times(1)).notificarTransacaoAtrasada(transacao);
    }

    @Test
    void deveCalcularSaldoCorretamente() {
        TransacaoService service = new TransacaoService(categoriaRepository, usuarioRepository, jwtService, transacaoRepository, notificarTransacaoService, transacaoMapper);
        TransacaoPost t1 = new TransacaoPost("Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L);
        TransacaoPost t2 = new TransacaoPost("Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), 1L);
        Usuario usuario = new Usuario(1L);
        Categoria categoria = Categoria.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        Transacao transacaoEntity1 = new Transacao(null, "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(),usuario);
        Transacao transacaoEntity2 = new Transacao(null, "Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), usuario);
        when(transacaoMapper.toEntity(eq(t1), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacaoEntity1);
        when(transacaoMapper.toEntity(eq(t2), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacaoEntity2);
        List<Transacao> listaTransacoes = Arrays.asList(transacaoEntity1, transacaoEntity2);
        when(transacaoRepository.buscarTodasTransacoes()).thenReturn(listaTransacoes);
        service.adicionarTransacao(t1);
        service.adicionarTransacao(t2);
        BigDecimal saldo = service.calcularSaldo();
        assertEquals(new BigDecimal("500"), saldo);
        verify(transacaoMapper).toEntity(eq(t1), eq(categoria), eq(usuario), any(Auditoria.class));
        verify(transacaoMapper).toEntity(eq(t2), eq(categoria), eq(usuario), any(Auditoria.class));
    }

    @Test
    void deveBuscarTransacoesPorCategoriaId() {
        Long categoriaId = 2L;
        Usuario usuario = new Usuario(1L);
        List<Transacao> transacoes = List.of(
                Transacao.builder()
                        .id(1L)
                        .descricao("Compra no mercado")
                        .valor(BigDecimal.valueOf(50))
                        .tipo(TipoMovimentacao.DESPESA)
                        .data(LocalDateTime.now())
                        .usuario(usuario)
                        .categoria(new Categoria(2L, "Alimentação", "Descricao", "Descricao", usuario))
                        .auditoria(new Auditoria())
                        .build(),
                Transacao.builder()
                        .id(2L)
                        .descricao("Salário recebido")
                        .valor(BigDecimal.valueOf(30))
                        .tipo(TipoMovimentacao.RECEITA)
                        .data(LocalDateTime.now())
                        .usuario(new Usuario(1L))
                        .categoria(new Categoria(2L, "Alimentação", "Teste", "teste", usuario))
                        .auditoria(new Auditoria())
                        .build()
        );


        List<TransacaoResponse> transacoesResponses = List.of(
                new TransacaoResponse(
                        1L,
                        "Despesa Alimentação",
                        BigDecimal.valueOf(50),
                        TipoMovimentacao.RECEITA.toString(),
                        LocalDateTime.now(),
                        false,
                        null,
                        LocalDateTime.now()
                ),
                new TransacaoResponse(
                        2L,
                        "Receita Alimentação",
                        BigDecimal.valueOf(30),
                        TipoMovimentacao.RECEITA.toString(),
                        LocalDateTime.now(),
                        true,
                        null,
                        LocalDateTime.now()
                )
        );

        // Configurando o mock para retornar a lista de transações
        when(transacaoRepository.buscarTransacoesPorCategoriaId(categoriaId)).thenReturn(transacoes);
        when(transacaoMapper.toResponse(any(Transacao.class))).thenReturn(transacoesResponses.get(0),transacoesResponses.get(1));

        List<TransacaoResponse> resultado = transacaoService.buscarTransacoesPorCategoriaId(categoriaId);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(transacoesResponses.get(0).id(), resultado.get(0).id());
        assertEquals(transacoesResponses.get(1).id(), resultado.get(1).id());

        verify(transacaoRepository, times(1)).buscarTransacoesPorCategoriaId(categoriaId);
        verify(transacaoMapper, times(2)).toResponse(any(Transacao.class));
    }
}
