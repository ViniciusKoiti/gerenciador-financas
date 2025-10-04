package com.vinicius.gerenciamento_financeiro.adapter.out.persistence.transacao.entity;

import com.vinicius.gerenciamento_financeiro.adapter.out.persistence.moeda.entity.MoedaJpaEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Embeddable
public class MontanteMonetarioJpaEntity {

    @Column(name = "valor", precision = 19, scale = 6, nullable = false)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "valor_moeda_id", nullable = false)
    private MoedaJpaEntity moeda;

    protected MontanteMonetarioJpaEntity() { }

    public MontanteMonetarioJpaEntity(BigDecimal amount, MoedaJpaEntity currencyCode) {
        this.valor = amount;
        this.moeda = currencyCode;
    }
    public BigDecimal getValor() {
        return valor;
    }

    public MoedaJpaEntity getMoeda() {
        return moeda;
    }
}
