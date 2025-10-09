package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao;

import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransacaoResponseMapper {

    private final ConfiguracaoTransacaoResponseMapper configuracaoMapper;

    public TransacaoResponse toResponse(Transacao transacao) {
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
                .config(configuracaoMapper.toResponse(transacao.getConfiguracao()))
                .createdDate(transacao.getAuditoria() != null ? transacao.getAuditoria().getCriadoEm() : null)
                .build();
    }

    public List<TransacaoResponse> toResponseList(List<Transacao> transacoes) {
        if (transacoes == null) {
            return List.of();
        }

        return transacoes.stream()
                .map(this::toResponse)
                .toList();
    }
}