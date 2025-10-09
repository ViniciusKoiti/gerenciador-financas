package com.vinicius.gerenciamento_financeiro.services.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.CategoriaId;
import com.vinicius.gerenciamento_financeiro.domain.model.cliente.ClienteId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.TransacaoId;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoMovimentacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.UsuarioId;
import com.vinicius.gerenciamento_financeiro.application.service.transacao.NotificarTransacaoService;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.junit.jupiter.api.DisplayName;
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
class NotificarTransacaoServiceTest{

    @Mock
    private NotificarUseCase notificarUseCase;

    @InjectMocks
    private NotificarTransacaoService notificarTransacaoService;

    @Test
    @DisplayName("✅ Deve enviar com atraso para transação futura")
    void deveEnviarNotificacaoComAtraso_QuandoTransacaoFutura() {
        // Arrange - Data futura
        ConfiguracaoTransacao configuracaoFutura = ConfiguracaoTransacao.comVencimento(
                LocalDate.now().plusDays(2)
        );

        Transacao transacaoFutura = Transacao.reconstituir(
                TransacaoId.of(1L), "Transação Futura", MontanteMonetario.ofBRL(new BigDecimal("100.00")), TipoMovimentacao.DESPESA,
                LocalDateTime.now(), UsuarioId.of(1L), CategoriaId.of(1L), ClienteId.of(1L),
                configuracaoFutura, Auditoria.criarNova(), "Vini"
        );

        // Act
        notificarTransacaoService.notificarTransacaoAtrasada(transacaoFutura);

        // Assert
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> mensagemCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificarUseCase).enviarNotificacaoComAtraso(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                mensagemCaptor.capture(),
                delayCaptor.capture()
        );

        String mensagemEsperada = "Transação programada para " + configuracaoFutura.getDataVencimento();
        assertEquals(mensagemEsperada, mensagemCaptor.getValue());
        assertTrue(delayCaptor.getValue() > 0, "Delay deve ser positivo para transação futura");
    }

    @Test
    @DisplayName("✅ Deve enviar imediatamente para transação vencida")
    void deveEnviarNotificacaoImediata_QuandoTransacaoVencida() {
        // Arrange - Data passada
        ConfiguracaoTransacao configuracaoVencida = ConfiguracaoTransacao.comVencimento(
                LocalDate.now().minusDays(1)  // Ontem = vencida
        );

        Transacao transacaoVencida = Transacao.reconstituir(
                TransacaoId.of(2L), "Transação Vencida", MontanteMonetario.ofBRL(new BigDecimal("200.00")), TipoMovimentacao.DESPESA,
                LocalDateTime.now(), UsuarioId.of(1L), CategoriaId.of(1L), ClienteId.of(1L),
                configuracaoVencida, Auditoria.criarNova(), "Vini"
        );

        // Act
        notificarTransacaoService.notificarTransacaoAtrasada(transacaoVencida);

        // Assert
        verify(notificarUseCase).enviarNotificacao(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                eq("Transação já venceu e será processada agora!")
        );
    }

    @Test
    @DisplayName("✅ Deve enviar imediatamente para configuração padrão (hoje)")
    void deveEnviarNotificacaoImediata_QuandoConfiguracaoPadrao() {
        // Arrange - Configuração padrão (dataVencimento = hoje)
        Transacao transacaoComConfiguracaoPadrao = Transacao.criarNova(
                "Transação com configuração padrão",
                MontanteMonetario.ofBRL(new BigDecimal("50.00")),
                TipoMovimentacao.RECEITA,
                LocalDateTime.now(),
                CategoriaId.of(1L),
                UsuarioId.of(1L)
        );

        // Act
        notificarTransacaoService.notificarTransacaoAtrasada(transacaoComConfiguracaoPadrao);

        // Assert - Como dataVencimento = hoje, será tratada como vencida
        verify(notificarUseCase).enviarNotificacao(
                eq(RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO),
                eq(RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO),
                eq("Transação já venceu e será processada agora!")
        );
    }

    @Test
    @DisplayName("✅ Deve verificar lógica de cálculo de delay")
    void deveCalcularDelayCorretamente() {
        LocalDate dataVencimento = LocalDate.now().plusDays(1); // Amanhã
        ConfiguracaoTransacao configuracao = ConfiguracaoTransacao.comVencimento(dataVencimento);

        Transacao transacao = Transacao.reconstituir(
                TransacaoId.of(3L), "Transação Teste Delay", MontanteMonetario.ofBRL(new BigDecimal("75.00")), TipoMovimentacao.DESPESA,
                LocalDateTime.now(), UsuarioId.of(1L), CategoriaId.of(1L), ClienteId.of(1L),
                configuracao, Auditoria.criarNova(), "Vini"
        );

        // Act
        notificarTransacaoService.notificarTransacaoAtrasada(transacao);

        // Assert
        ArgumentCaptor<Long> delayCaptor = ArgumentCaptor.forClass(Long.class);
        verify(notificarUseCase).enviarNotificacaoComAtraso(
                anyString(), anyString(), anyString(), delayCaptor.capture()
        );

        Long delayCalculado = delayCaptor.getValue();

        long umDiaEmMillis = 24 * 60 * 60 * 1000L;
        long margemErro = 60 * 1000L;


        assertTrue(delayCalculado < (umDiaEmMillis + margemErro),
                "Delay não deve exceder muito 1 dia");
    }
}
