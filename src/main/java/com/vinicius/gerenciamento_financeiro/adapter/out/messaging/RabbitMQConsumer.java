package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = RabbitMQConstants.FILA_TRANSACOES)
    public void consumirMensagem(String mensagem) {
        System.out.println("Mensagem recebida do RabbitMQ: " + mensagem);
    }
}
