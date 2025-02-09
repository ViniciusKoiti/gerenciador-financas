package com.vinicius.gerenciamento_financeiro.port.in;

public interface NotificarUseCase {

    void enviarNotificacao(String exchange, String routingKey, String mensagem);
    void enviarNotificacaoComAtraso(String exchange, String routingKey, String mensagem, long delayMillis);
}
