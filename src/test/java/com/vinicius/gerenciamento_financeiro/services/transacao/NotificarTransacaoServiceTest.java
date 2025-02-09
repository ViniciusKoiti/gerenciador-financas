package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Criando configurações para transações futuras e vencidas
        Transacao configuracaoFutura = new Transacao();
        ConfiguracaoTransacao configuracaoTransacaoFutura =  ConfiguracaoTransacao.builder().dataVencimento(LocalDate.from(LocalDateTime.now().plusDays(2))).build();

        Transacao configuracaoVencida = new Transacao();
        ConfiguracaoTransacao configuracaoTransacaoVencida = ConfiguracaoTransacao.builder().dataVencimento(LocalDate.from(LocalDateTime.now().minusDays(1))).build();


        transacaoFutura = Transacao.builder()
                .configuracao(configuracaoTransacaoFutura)
                .build();

        transacaoVencida = Transacao.builder()
                .configuracao(configuracaoTransacaoVencida)
                .build();
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
        assert(delayCaptor.getValue() > 0);
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
}
