package inMemoryTest;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.out.memory.MemoryTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.categoria.entity.CategoriaJpaEntity;
import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.usuario.entity.UsuarioJpaEntity;
import com.vinicius.gerenciamento_financeiro.domain.exception.ResourceNotFoundException;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private MemoryTransacaoRepository memoryRepository;
    private TransacaoService service;

    private Usuario usuarioTeste;
    private CategoriaJpaEntity categoriaJpaEntityTeste;
    private UsuarioJpaEntity usuarioJpaEntityTeste;

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

        usuarioTeste = Usuario.reconstituir(
                1L,
                "Usuario Teste",
                "teste@email.com",
                "senha123",
                Auditoria.criarNova(),
                Set.of()
        );

        usuarioJpaEntityTeste = UsuarioJpaEntity.builder()
                .id(1L)
                .email("teste@email.com")
                .nome("Usuario Teste")
                .senha("senha123")
                .build();

        categoriaJpaEntityTeste = CategoriaJpaEntity.builder()
                .id(1L)
                .nome("Categoria Teste")
                .descricao("Descrição teste")
                .ativa(true)
                .icone("icone-teste")
                .usuario(usuarioJpaEntityTeste)
                .build();
    }

    @Test
    @DisplayName("Deve adicionar transação e verificar se foi salva corretamente")
    void deveAdicionarTransacaoComSucesso() {


        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaJpaEntityTeste));
        when(categoriaRepository.existsByIdAndUsuarioId(1L, 1L)).thenReturn(true);

        TransacaoPost transacaoPost = new TransacaoPost(
                "Salário",
                new BigDecimal("1000"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                1L
        );

        Transacao transacaoEsperada = Transacao.criarNova(
                "Salário",
                new BigDecimal("1000"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                CategoriaId.of(1L),
                UsuarioId.of(1L)
        );

        when(mapper.toEntity(eq(transacaoPost), eq(CategoriaId.of(1L)), eq(UsuarioId.of(1L))))
                .thenReturn(transacaoEsperada);

        // Act
        service.adicionarTransacao(transacaoPost);

        verify(notificarTransacaoService).notificarTransacaoAtrasada(any(Transacao.class));



        assertEquals(1, memoryRepository.contarTodas());
        assertEquals(1, memoryRepository.contarTransacoesPorUsuario(1L));

        List<Transacao> transacoesSalvas = memoryRepository.buscarTodasTransacoesPorUsuario(1L);
        assertFalse(transacoesSalvas.isEmpty());
        assertEquals("Salário", transacoesSalvas.get(0).getDescricao());
    }

    @Test
    @DisplayName("Deve calcular saldo corretamente com múltiplas transações")
    void deveCalcularSaldoCorretamente() {
        // Arrange
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.existsByIdAndUsuarioId(1L, 1L)).thenReturn(true);

        TransacaoPost receita = new TransacaoPost(
                "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );
        TransacaoPost despesa = new TransacaoPost(
                "Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), 1L
        );



        // Act
        service.adicionarTransacao(receita);
        service.adicionarTransacao(despesa);
        BigDecimal saldo = service.calcularSaldo();

        assertEquals(new BigDecimal("500"), saldo);
        assertEquals(2, memoryRepository.contarTodas());

        List<Transacao> todasTransacoes = memoryRepository.buscarTodasTransacoesPorUsuario(1L);
        assertEquals(2, todasTransacoes.size());



        verify(notificarTransacaoService, times(2)).notificarTransacaoAtrasada(any(Transacao.class));

        assertEquals("Salário", todasTransacoes.get(0).getDescricao());
        assertEquals("Aluguel", todasTransacoes.get(1).getDescricao());
    }

    @Test
    @DisplayName("Deve isolar transações por usuário")
    void deveIsolararTransacoesPorUsuario() {
        // Cria usuários diferentes
        Usuario usuario2 = Usuario.reconstituir(
                2L, "Usuario 2", "usuario2@email.com", "senha123", Auditoria.criarNova(), null
        );

        Transacao transacaoUsuario1 = criarTransacaoDominio(
                1L, "Transação Usuário 1", new BigDecimal("100"), TipoMovimentacao.RECEITA
        );

        Transacao transacaoUsuario2 = Transacao.reconstituir(
                2L,
                "Transação Usuário 2",
                new BigDecimal("200"),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                UsuarioId.of(2L),
                CategoriaId.of(1L),
                ConfiguracaoTransacao.padrao(),
                Auditoria.criarNova()
        );

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
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoriaJpaEntityTeste));
        when(categoriaRepository.existsByIdAndUsuarioId(1L, 1L)).thenReturn(true);


        // Cria transações para categorias diferentes
        Transacao transacao1 = criarTransacaoParaCategoria(1L, "Transação Cat 1", CategoriaId.of(1L));
        Transacao transacao2 = criarTransacaoParaCategoria(2L, "Transação Cat 2", CategoriaId.of(2L));

        memoryRepository.salvarTransacao(transacao1);
        memoryRepository.salvarTransacao(transacao2);

        // Act
        List<Transacao> transacoesCategoria1 = memoryRepository.buscarTransacoesPorCategoriaIdEUsuario(1L, 1L);

        // Assert
        assertEquals(1, transacoesCategoria1.size());
        assertEquals("Transação Cat 1", transacoesCategoria1.get(0).getDescricao());
        assertEquals(1L, transacoesCategoria1.get(0).getCategoriaId().getValue());
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        // Arrange
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.existsByIdAndUsuarioId(999L, 1L)).thenReturn(false);

        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 999L
        );

        // Act & Assert
        Exception exception = assertThrows(
                Exception.class, // Pode ser InsufficientPermissionException ou ResourceNotFoundException
                () -> service.adicionarTransacao(transacaoPost)
        );

        assertEquals(0, memoryRepository.contarTodas());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.empty());

        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.adicionarTransacao(transacaoPost)
        );

        assertTrue(exception.getMessage().contains("Usuario com ID 1 não encontrado"));
        assertEquals(0, memoryRepository.contarTodas());

        verify(mapper, never()).toEntity(any(), any(), any());
    }

    @Test
    @DisplayName("Deve verificar se notificação é chamada corretamente")
    void deveVerificarNotificacao() {
        // Arrange
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(usuarioRepository.findById(UsuarioId.of(1L))).thenReturn(Optional.of(usuarioTeste));
        when(categoriaRepository.existsByIdAndUsuarioId(1L, 1L)).thenReturn(true);

        TransacaoPost transacaoPost = new TransacaoPost(
                "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L
        );

        Transacao transacaoEsperada = criarTransacaoDominio(
                1L, "Teste", new BigDecimal("100"), TipoMovimentacao.RECEITA
        );


        // Act
        service.adicionarTransacao(transacaoPost);

        // Assert
        ArgumentCaptor<Transacao> transacaoCaptor = ArgumentCaptor.forClass(Transacao.class);
        verify(notificarTransacaoService).notificarTransacaoAtrasada(transacaoCaptor.capture());

        Transacao transacaoCapturada = transacaoCaptor.getValue();
        assertEquals("Teste", transacaoCapturada.getDescricao());
        assertEquals(new BigDecimal("100"), transacaoCapturada.getValor());
    }

    // ========== MÉTODOS AUXILIARES ==========

    private Transacao criarTransacaoDominio(Long id, String descricao, BigDecimal valor, TipoMovimentacao tipo) {
        if (id != null) {
            return Transacao.reconstituir(
                    id,
                    descricao,
                    valor,
                    tipo,
                    LocalDateTime.now(),
                    UsuarioId.of(1L),
                    CategoriaId.of(1L),
                    ConfiguracaoTransacao.padrao(),
                    Auditoria.criarNova()
            );
        } else {
            return Transacao.criarNova(
                    descricao,
                    valor,
                    tipo,
                    LocalDateTime.now(),
                    CategoriaId.of(1L),
                    UsuarioId.of(1L)
            );
        }
    }

    private Transacao criarTransacaoParaCategoria(Long id, String descricao, CategoriaId categoriaId) {
        return Transacao.reconstituir(
                id,
                descricao,
                new BigDecimal("100"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                UsuarioId.of(1L),
                categoriaId,
                ConfiguracaoTransacao.padrao(),
                Auditoria.criarNova()
        );
    }
}