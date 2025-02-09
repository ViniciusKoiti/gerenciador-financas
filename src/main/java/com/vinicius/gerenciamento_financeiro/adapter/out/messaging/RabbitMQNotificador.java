package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMQNotificador implements NotificarUseCase {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQNotificador(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarNotificacao(String nomeFila, String mensagem) {
        rabbitTemplate.convertAndSend(nomeFila, mensagem);
    }
}
