package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static com.vinicius.gerenciamento_financeiro.adapter.out.messaging.RabbitMQConstants.*;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue queueTransaction() {
        return QueueBuilder.durable(FILA_TRANSACOES).build();
    }
    @Bean
    public CustomExchange exchangeTransactionDelayed(){
        return new CustomExchange(EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO, "x-delayed-message", true, false,
                Map.of("x-delayed-type", "direct"));
    }
    @Bean
    public Binding bindingTransactionDelayed(Queue filaTransacoes, CustomExchange exchangeDelayed) {
        return BindingBuilder.bind(filaTransacoes).to(exchangeDelayed).with(ROUTING_KEY_TRANSCACOES_VENCIMENTO).noargs();
    }
}
