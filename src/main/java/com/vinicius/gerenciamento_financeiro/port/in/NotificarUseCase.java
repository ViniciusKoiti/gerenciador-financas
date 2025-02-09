package com.vinicius.gerenciamento_financeiro.port.in;

public interface NotificarUseCase {

    void enviarNotificacao(String nomeFila, String mensagem);
}
