package com.vinicius.gerenciamento_financeiro.services.notificacao;

import com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQNotificador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQNotificadorTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQNotificador rabbitMQNotificador;

    private final String exchange = "exchange.test";
    private final String routingKey = "routing.test";
    private final String mensagem = "Teste de mensagem";

    @Test
    void deveEnviarNotificacaoCorretamente() {
        rabbitMQNotificador.enviarNotificacao(exchange, routingKey, mensagem);
        verify(rabbitTemplate).convertAndSend(eq(exchange), eq(mensagem));
    }

    @Test
    void deveEnviarNotificacaoComAtrasoCorretamente() {
        long delayMillis = 5000L;
        rabbitMQNotificador.enviarNotificacaoComAtraso(exchange, routingKey, mensagem, delayMillis);
        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).send(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());
        assertEquals(exchange, exchangeCaptor.getValue());
        assertEquals(routingKey, routingKeyCaptor.getValue());
        Message sentMessage = messageCaptor.getValue();
        assertEquals(mensagem, new String(sentMessage.getBody(), StandardCharsets.UTF_8));
        assertEquals(delayMillis, sentMessage.getMessageProperties().getHeaders().get("x-delay"));
    }
}
