package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TransacaoResponse(
        Long id,
        String description,
        BigDecimal amount,
        String type,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,
        Boolean paid,
        ConfiguracaoTransacaoResponse config,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdDate
) {

    @Builder
    public record ConfiguracaoTransacaoResponse(
            Boolean recorrente,
            Integer periodicidade,
            Boolean parcelado,
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate dataVencimento,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime dataPagamento
    ) {

        public static ConfiguracaoTransacaoResponse fromEntity(ConfiguracaoTransacao configuracao) {
            if (configuracao == null) {
                return null;
            }

            return ConfiguracaoTransacaoResponse.builder()
                    .recorrente(configuracao.getRecorrente())
                    .periodicidade(configuracao.getPeriodicidade())
                    .parcelado(configuracao.getParcelado())
                    .dataVencimento(configuracao.getDataVencimento())
                    .dataPagamento(configuracao.getDataPagamento())
                    .build();
        }
    }

    public static TransacaoResponse fromEntity(Transacao transacao) {
        if (transacao == null) {
            return null;
        }

        return TransacaoResponse.builder()
                .id(transacao.getId() != null ? transacao.getId().getValue() : null)
                .description(transacao.getDescricao())
                .amount(transacao.getValor())
                .type(transacao.getTipo().toString())
                .date(transacao.getData())
                .paid(transacao.getConfiguracao() != null ? transacao.getConfiguracao().getPago() : false)
                .config(ConfiguracaoTransacaoResponse.fromEntity(transacao.getConfiguracao()))
                .createdDate(transacao.getAuditoria() != null ? transacao.getAuditoria().getCriadoEm() : null)
                .build();
    }

    @Deprecated
    public static TransacaoResponse of(
            Long id,
            String descricao,
            BigDecimal valor,
            String tipo,
            LocalDateTime data,
            Boolean pago,
            ConfiguracaoTransacaoResponse configuracao,
            LocalDateTime dataCriacao
    ) {
        return new TransacaoResponse(
                id, descricao, valor, tipo, data,
                pago, configuracao, dataCriacao
        );
    }
}