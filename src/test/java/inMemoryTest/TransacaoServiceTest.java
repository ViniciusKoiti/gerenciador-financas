package inMemoryTest;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.adapter.out.memory.MemoryTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.adapter.out.transacao.JpaTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoService - Testes com Memory Repository")
public class TransacaoServiceTest {

    @Mock
    private TransacaoMapper mapper;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private NotificarTransacaoService notificarTransacaoService;

    @Mock
    private JwtService jwtService;

    @Mock
    private JpaTransacaoRepository jpaTransacaoRepository;

    private MemoryTransacaoRepository memoryRepository;
    private TransacaoService service;

    // Dados de teste
    private Usuario usuarioTeste;
    private Categoria categoriaTeste;
    private Auditoria auditoriaTeste;

    @BeforeEach
    void setUp() {
        memoryRepository = new MemoryTransacaoRepository();
        service = new TransacaoService(
                categoriaRepository,
                usuarioRepository,
                jwtService,
                memoryRepository,
                notificarTransacaoService,
                mapper
        );

        // Configura dados de teste
        usuarioTeste = new Usuario(1L);
        categoriaTeste = Categoria.builder()
                .id(1L)
                .nome("Categoria Teste")
                .usuario(usuarioTeste)
                .build();
        auditoriaTeste = new Auditoria();

    }

    @Test
    @DisplayName("Deve adicionar transação e verificar se foi salva corretamente")
    void deveAdicionarTransacaoComSucesso() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTeste));
        // Arrange
        TransacaoPost transacaoPost = new TransacaoPost(
                "Salário",
                new BigDecimal("1000"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                1L
        );

        Transacao transacaoEsperada = criarTransacaoCompleta(
                1L, "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA
        );

        when(mapper.toEntity(eq(transacaoPost), eq(categoriaTeste), eq(usuarioTeste), any(Auditoria.class)))
                .thenReturn(transacaoEsperada);

        // Act
        service.adicionarTransacao(transacaoPost);

        // Assert
        verify(notificarTransacaoService).notificarTransacaoAtrasada(transacaoEsperada);
        assertEquals(1, memoryRepository.contarTodas());
        assertEquals(1, memoryRepository.contarTransacoesPorUsuario(1L));

        // Verifica que a transação foi salva corretamente
        List<Transacao> transacoesSalvas = memoryRepository.buscarTodasTransacoesPorUsuario(1L);
        assertFalse(transacoesSalvas.isEmpty());
        assertEquals("Salário", transacoesSalvas.get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve calcular saldo corretamente com múltiplas transações")
    void deveCalcularSaldoCorretamente() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTeste));

        TransacaoPost receita = new TransacaoPost(
                "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );
        TransacaoPost despesa = new TransacaoPost(
                "Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), 1L
        );

        Transacao transacaoReceita = criarTransacaoCompleta(
                1L, "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA
        );
        Transacao transacaoDespesa = criarTransacaoCompleta(
                2L, "Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA
        );

        when(mapper.toEntity(eq(receita), eq(categoriaTeste), eq(usuarioTeste), any(Auditoria.class)))
                .thenReturn(transacaoReceita);
        when(mapper.toEntity(eq(despesa), eq(categoriaTeste), eq(usuarioTeste), any(Auditoria.class)))
                .thenReturn(transacaoDespesa);

        // Act
        service.adicionarTransacao(receita);
        service.adicionarTransacao(despesa);
        BigDecimal saldo = service.calcularSaldo();

        // Assert
        assertEquals(new BigDecimal("500"), saldo);
        assertEquals(2, memoryRepository.contarTodas());

        List<Transacao> todasTransacoes = memoryRepository.buscarTodasTransacoesPorUsuario(1L);
        assertEquals(2, todasTransacoes.size());

        verify(mapper, times(2)).toEntity(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve isolar transações por usuário")
    void deveIsolararTransacoesPorUsuario() {

        Usuario usuario2 = new Usuario(2L);

        Transacao transacaoUsuario1 = criarTransacaoCompleta(
                1L, "Transação Usuário 1", new BigDecimal("100"), TipoMovimentacao.RECEITA
        );
        Transacao transacaoUsuario2 = criarTransacaoCompleta(
                2L, "Transação Usuário 2", new BigDecimal("200"), TipoMovimentacao.DESPESA
        );

        transacaoUsuario2 = Transacao.builder()
                .id(2L)
                .descricao("Transação Usuário 2")
                .valor(new BigDecimal("200"))
                .tipo(TipoMovimentacao.DESPESA)
                .data(LocalDateTime.now())
                .usuario(usuario2)
                .categoria(categoriaTeste)
                .auditoria(auditoriaTeste)
                .configuracao(criarConfiguracaoPadrao())
                .build();

        memoryRepository.salvarTransacao(transacaoUsuario1);
        memoryRepository.salvarTransacao(transacaoUsuario2);

        List<Transacao> transacoesUsuario1 = memoryRepository.buscarTodasTransacoesPorUsuario(1L);
        List<Transacao> transacoesUsuario2 = memoryRepository.buscarTodasTransacoesPorUsuario(2L);

        assertEquals(1, transacoesUsuario1.size());
        assertEquals(1, transacoesUsuario2.size());
        assertEquals("Transação Usuário 1", transacoesUsuario1.get(0).getDescricao());
        assertEquals("Transação Usuário 2", transacoesUsuario2.get(0).getDescricao());

        assertEquals(2, memoryRepository.contarTodas());
        assertEquals(1, memoryRepository.contarTransacoesPorUsuario(1L));
        assertEquals(1, memoryRepository.contarTransacoesPorUsuario(2L));
    }

    @Test
    @DisplayName("Deve buscar transações por categoria corretamente")
    void deveBuscarTransacoesPorCategoria() {


        Categoria categoria2 = Categoria.builder()
                .id(2L)
                .nome("Categoria 2")
                .usuario(usuarioTeste)
                .build();

        Transacao transacao1 = criarTransacaoParaCategoria(1L, "Transação Cat 1", categoriaTeste);
        Transacao transacao2 = criarTransacaoParaCategoria(2L, "Transação Cat 2", categoria2);


        memoryRepository.salvarTransacao(transacao1);
        memoryRepository.salvarTransacao(transacao2);

        List<Transacao> transacoesCategoria1 = memoryRepository.buscarTransacoesPorCategoriaIdEUsuario(1L, 1L);

        assertEquals(1, transacoesCategoria1.size());
        assertEquals("Transação Cat 1", transacoesCategoria1.get(0).getDescricao());
        assertEquals(1L, transacoesCategoria1.get(0).getCategoria().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);

        // Arrange
        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 999L
        );



        when(categoriaRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.adicionarTransacao(transacaoPost)
        );

        assertEquals("500 INTERNAL_SERVER_ERROR \"Erro ao processar transação: Categoria não encontrada.\"", exception.getMessage());
        assertEquals(0, memoryRepository.contarTodas());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTeste));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty()); // Usuário NÃO encontrado

        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.adicionarTransacao(transacaoPost)
        );

        assertTrue(exception.getMessage().contains("Usuário não encontrado"));
        assertEquals(0, memoryRepository.contarTodas());

        verify(mapper, never()).toEntity(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve verificar se notificação é chamada corretamente")
    void deveVerificarNotificacao() {
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaTeste));
        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );

        Transacao transacaoEsperada = criarTransacaoCompleta(
                1L, "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA
        );

        when(mapper.toEntity(any(), any(), any(), any())).thenReturn(transacaoEsperada);

        service.adicionarTransacao(transacaoPost);

        // Assert
        ArgumentCaptor<Transacao> transacaoCaptor = ArgumentCaptor.forClass(Transacao.class);
        verify(notificarTransacaoService).notificarTransacaoAtrasada(transacaoCaptor.capture());

        Transacao transacaoCapturada = transacaoCaptor.getValue();
        assertEquals("Teste", transacaoCapturada.getDescricao());
        assertEquals(new BigDecimal("100"), transacaoCapturada.getValor());
    }
    private Transacao criarTransacaoCompleta(Long id, String descricao, BigDecimal valor, TipoMovimentacao tipo) {
        return Transacao.builder()
                .id(id)
                .descricao(descricao)
                .valor(valor)
                .tipo(tipo)
                .data(LocalDateTime.now())
                .usuario(usuarioTeste)
                .categoria(categoriaTeste)
                .auditoria(auditoriaTeste)
                .configuracao(criarConfiguracaoPadrao())
                .build();
    }

    private Transacao criarTransacaoParaCategoria(Long id, String descricao, Categoria categoria) {
        return Transacao.builder()
                .id(id)
                .descricao(descricao)
                .valor(new BigDecimal("100"))
                .tipo(TipoMovimentacao.RECEITA)
                .data(LocalDateTime.now())
                .usuario(usuarioTeste)
                .categoria(categoria)
                .auditoria(auditoriaTeste)
                .configuracao(criarConfiguracaoPadrao())
                .build();
    }

    private ConfiguracaoTransacao criarConfiguracaoPadrao() {
        return ConfiguracaoTransacao.builder()
                .recorrente(false)
                .parcelado(false)
                .dataVencimento(LocalDate.now())
                .build();
    }
}