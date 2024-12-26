package inMemoryTest;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper.TransacaoMapper;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.out.memory.MemoryTransacaoRepository;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.TransacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.vinicius.gerenciamento_financeiro.port.out.transacao.TransacaoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransacaoServiceTest {

    @Mock
    private TransacaoMapper mapper;

    @Test
    void deveAdicionarEObterTransacoes() {
        TransacaoRepository repository = new MemoryTransacaoRepository();
        TransacaoService service = new TransacaoService(repository, mapper);
        TransacaoPost t1 = new TransacaoPost("Salário", new BigDecimal("1000"), Transacao.Tipo.RECEITA, LocalDateTime.now());
        service.adicionarTransacao(t1);
        assertEquals(1, service.obterTodasTransacoes().size());
        verify(mapper).toEntity(t1);
    }

    @Test
    void deveCalcularSaldoCorretamente() {
        TransacaoRepository repository = new MemoryTransacaoRepository();
        TransacaoService service = new TransacaoService(repository, mapper);
        TransacaoPost t1 = new TransacaoPost("Salário", new BigDecimal("1000"), Transacao.Tipo.RECEITA, LocalDateTime.now());
        TransacaoPost t2 = new TransacaoPost("Aluguel", new BigDecimal("500"), Transacao.Tipo.DESPESA, LocalDateTime.now());
        Usuario usuario = new Usuario(1L);

        Transacao transacaoEntity1 = new Transacao(null, "Salário", new BigDecimal("1000"), Transacao.Tipo.RECEITA, LocalDateTime.now(),usuario);
        Transacao transacaoEntity2 = new Transacao(null, "Aluguel", new BigDecimal("500"), Transacao.Tipo.DESPESA, LocalDateTime.now(), usuario);
        when(mapper.toEntity(t1)).thenReturn(transacaoEntity1);
        when(mapper.toEntity(t2)).thenReturn(transacaoEntity2);
        service.adicionarTransacao(t1);
        service.adicionarTransacao(t2);
        BigDecimal saldo = service.calcularSaldo();
        assertEquals(new BigDecimal("500"), saldo);
        verify(mapper).toEntity(t1);
        verify(mapper).toEntity(t2);
    }
}
