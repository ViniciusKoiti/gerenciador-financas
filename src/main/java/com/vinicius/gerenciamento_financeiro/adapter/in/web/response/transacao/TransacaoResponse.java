package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoResponse {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private String tipo;
    private LocalDateTime data;

    // Construtor padrão
    public TransacaoResponse() {
    }

    public TransacaoResponse(Long id, String descricao, BigDecimal valor, String tipo, LocalDateTime data) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.tipo = tipo;
        this.data = data;
    }

}
