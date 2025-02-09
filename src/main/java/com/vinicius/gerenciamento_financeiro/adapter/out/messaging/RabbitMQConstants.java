package com.vinicius.gerenciamento_financeiro.adapter.out.messaging;

public class RabbitMQConstants {

    public static final String FILA_TRANSACOES = "fila_transacoes";
    public static final String EXCHANGE_DELAYED_TRANSACOES_VENCIMENTO = "transacao_exchange_delayed";
    public static final String ROUTING_KEY_TRANSCACOES_VENCIMENTO = "transacao_routing_key";
    private RabbitMQConstants() {
    }
}
