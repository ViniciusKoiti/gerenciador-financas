package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificarTransacaoServiceTest {

    @Mock
    private NotificarUseCase notificarUseCase;

    @InjectMocks
    private NotificarTransacaoService notificarTransacaoService;

    private Transacao transacaoFutura;
    private Transacao transacaoVencida;

    @BeforeEach
    void setUp() {
        UsuarioId usuarioId = UsuarioId.of(1L);
        CategoriaId categoriaId = CategoriaId.of(1L);

        ConfiguracaoTransacao configuracaoFutura = ConfiguracaoTransacao.comVencimento(
                LocalDate.now().plusDays(2)
        );

        ConfiguracaoTransacao configuracaoVencida = ConfiguracaoTransacao.comVencimento(
                LocalDate.now().minusDays(1)
        );

        transacaoFutura = Transacao.reconstituir(
                1L,
                "Transação Futura",
                new BigDecimal("100.00"),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now(),
                usuarioId,
                categoriaId,
                configuracaoFutura,
                null
        );

        transacaoVencida = Transacao.reconstituir(
                2L,
                "Transação Vencida",
                new BigDecimal("200.00"),
                TipoMovimentacao.DESPESA,
                LocalDateTime.now().minusDays(2),
                usuarioId,
                categoriaId,
                configuracaoVencida,
                null
        );
    }

    @Test
    void deveEnviarNotificacaoComAtraso_QuandoTransacaoAindaNaoVenceu() {
        notificarTransacaoService.notificarTransacaoAtrasada(transacaoFutura);

        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificarUseCase).enviarNotificacaoComAtraso(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                mensagemCaptor.capture(),
                delayCaptor.capture()
        );

        String mensagemEsperada = "Transação programada para " + transacaoFutura.getConfiguracao().getDataVencimento();
        assertEquals(mensagemEsperada, mensagemCaptor.getValue());
        assertTrue(delayCaptor.getValue() > 0);
    }

    @Test
    void deveEnviarNotificacaoImediata_QuandoTransacaoJaVenceu() {
        notificarTransacaoService.notificarTransacaoAtrasada(transacaoVencida);

        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificarUseCase).enviarNotificacao(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                mensagemCaptor.capture()
        );

        assertEquals("Transação já venceu e será processada agora!", mensagemCaptor.getValue());
    }

    @Test
    void naoDeveEnviarNotificacao_QuandoConfiguracaoNula() {
        Transacao transacaoSemConfiguracao = Transacao.criarNova(
                "Transação sem configuração",
                new BigDecimal("50.00"),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                CategoriaId.of(1L),
                UsuarioId.of(1L)
        );

        notificarTransacaoService.notificarTransacaoAtrasada(transacaoSemConfiguracao);

        verify(notificarUseCase).enviarNotificacaoComAtraso(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                anyString(),
                anyLong()
        );
    }
}