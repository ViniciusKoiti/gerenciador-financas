package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.enums.TipoRecorrencia;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Embeddable
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfiguracaoTransacao {

    private Boolean pago;

    private Boolean recorrente;

    private Integer periodicidade;

    private Boolean ignorarLimiteCategoria;

    private TipoRecorrencia tipoRecorrencia;

    private Boolean ignorarOrcamento;
    private Boolean parcelado;

    private LocalDateTime dataPagamento;
    private LocalDate dataVencimento;

    public ConfiguracaoTransacao(boolean pago, boolean recorrente, Integer periodicidade,TipoRecorrencia tipoRecorrencia,  boolean ignorarLimiteCategoria, boolean ignorarOrcamento) {
        this.pago = pago;
        this.recorrente = recorrente;
        this.periodicidade = periodicidade;
        this.tipoRecorrencia = tipoRecorrencia;
        this.ignorarLimiteCategoria = ignorarLimiteCategoria;
        this.ignorarOrcamento = ignorarOrcamento;
    }


}
