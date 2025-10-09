package com.vinicius.gerenciamento_financeiro.application.service.transacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
@Service
public class NotificarTransacaoService {

    private final NotificarUseCase notificarUseCase;

    public NotificarTransacaoService(NotificarUseCase notificarUseCase) {
        this.notificarUseCase = notificarUseCase;
    }

    public void notificarTransacaoAtrasada(Transacao transacao){
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataVencimento = transacao.getConfiguracao().getDataVencimento().atStartOfDay();
        long delayMillis = ChronoUnit.MILLIS.between(agora, dataVencimento);
        if (delayMillis > 0) {
            notificarUseCase.enviarNotificacaoComAtraso(
                    RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO,
                    RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO,
                    "Transação programada para " + transacao.getConfiguracao().getDataVencimento(),
                    delayMillis
            );
        } else {
            notificarUseCase.enviarNotificacao(
                    RabbitMQConstants.EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO,
                    RabbitMQConstants.ROUTING_KEY_TRANSCACOES_VENCIMENTO,
                    "Transação já venceu e será processada agora!"
            );
        }
    }
}
