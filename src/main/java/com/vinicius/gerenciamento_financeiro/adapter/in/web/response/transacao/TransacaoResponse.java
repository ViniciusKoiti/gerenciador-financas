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
        BigDecimal value,
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
            return ConfiguracaoTransacaoResponse.builder()
                    .recorrente(configuracao.isRecorrente())
                    .periodicidade(configuracao.getPeriodicidade())
                    .parcelado(configuracao.isParcelado())
                    .dataVencimento(configuracao.getDataVencimento())
                    .dataPagamento(configuracao.getDataPagamento())
                    .build();
        }
    }

    @Builder
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


    public static TransacaoResponse fromEntity(Transacao transacao) {
        return TransacaoResponse.builder()
                .id(transacao.getId())
                .description(transacao.getDescricao())
                .value(transacao.getValor())
                .type(transacao.getTipo().toString())
                .date(transacao.getData())
                .paid(transacao.getConfiguracao().isPago())
                .config(transacao.getConfiguracao() != null ? ConfiguracaoTransacaoResponse.fromEntity(transacao.getConfiguracao()) : null)
                .createdDate(transacao.getAuditoria() != null ? transacao.getAuditoria().getCriadoEm() : null)
                .build();
    }
}