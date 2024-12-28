package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import jakarta.persistence.Embeddable;

@Embeddable
public class ConfiguracaoTransacao {

    private boolean pago;

    private boolean recorrente;

    private Integer periodicidade;

    private boolean ignorarLimiteCategoria;

    private boolean ignorarOrcamento;
}
