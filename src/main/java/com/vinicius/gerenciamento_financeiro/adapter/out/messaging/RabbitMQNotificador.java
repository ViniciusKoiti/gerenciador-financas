package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.port.in.NotificarUseCase;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
@Service
public class RabbitMQNotificador implements NotificarUseCase {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQNotificador(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarNotificacao(String exchange, String routingKey, String mensagem) {
        rabbitTemplate.convertAndSend(exchange, mensagem);
    }

    public void enviarNotificacaoComAtraso(String exchange, String routingKey, String mensagem, long delayMillis) {
        MessageProperties properties = new MessageProperties();
        properties.setHeader("x-delay", delayMillis);

        Message message = new Message(mensagem.getBytes(StandardCharsets.UTF_8), properties);

        rabbitTemplate.send(exchange, routingKey, message);
        System.out.println("📢 Notificação programada para " + delayMillis + "ms | Exchange: " + exchange + " | Routing Key: " + routingKey);
    }


}
