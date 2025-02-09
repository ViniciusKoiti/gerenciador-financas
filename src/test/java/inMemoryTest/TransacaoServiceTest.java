package inMemoryTest;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.config.security.JwtService;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.out.memory.MemoryTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import com.vinicius.gerenciamento_financeiro.port.out.categoria.CategoriaRepository;
import com.vinicius.gerenciamento_financeiro.port.out.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.CategoriaMapper.transacaoMapper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
    @Test
    void deveAdicionarEObterTransacoes() {
        TransacaoRepository repository = new MemoryTransacaoRepository();
        TransacaoService service = new TransacaoService(categoriaRepository, usuarioRepository, jwtService, repository, notificarTransacaoService, mapper);
        Usuario usuario = new Usuario(1L);
        Transacao transacao = Transacao.builder().id(1L).build();
        Categoria categoria = Categoria.builder().id(1L).build();
        Auditoria auditoria = new Auditoria();
        TransacaoPost t1 = new TransacaoPost("Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L);
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.ofNullable(categoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(mapper.toEntity(eq(t1), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacao);

        service.adicionarTransacao(t1);
        verify(notificarTransacaoService).notificarTransacaoAtrasada(transacao); // Garante que a notificação foi chamada
        assertEquals(1, service.obterTodasTransacoes().size());
    }

    @Test
    void deveCalcularSaldoCorretamente() {
        TransacaoRepository repository = new MemoryTransacaoRepository();
        TransacaoService service = new TransacaoService(categoriaRepository, usuarioRepository, jwtService, repository, notificarTransacaoService, mapper);
        TransacaoPost t1 = new TransacaoPost("Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(), 1L);
        TransacaoPost t2 = new TransacaoPost("Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), 1L);
        Usuario usuario = new Usuario(1L);
        Categoria categoria = Categoria.builder().id(1L).build();
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(jwtService.getByAutenticaoUsuarioId()).thenReturn(1L);
        Transacao transacaoEntity1 = new Transacao(null, "Salário", new BigDecimal("1000"), TipoMovimentacao.RECEITA, LocalDateTime.now(),usuario);
        Transacao transacaoEntity2 = new Transacao(null, "Aluguel", new BigDecimal("500"), TipoMovimentacao.DESPESA, LocalDateTime.now(), usuario);
        when(mapper.toEntity(eq(t1), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacaoEntity1);
        when(mapper.toEntity(eq(t2), eq(categoria), eq(usuario), any(Auditoria.class)))
                .thenReturn(transacaoEntity2);
        service.adicionarTransacao(t1);
        service.adicionarTransacao(t2);
        BigDecimal saldo = service.calcularSaldo();
        assertEquals(new BigDecimal("500"), saldo);
        verify(mapper).toEntity(eq(t1), eq(categoria), eq(usuario), any(Auditoria.class));
        verify(mapper).toEntity(eq(t2), eq(categoria), eq(usuario), any(Auditoria.class));
    }



}
