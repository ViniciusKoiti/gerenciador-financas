package com.vinicius.gerenciamento_financeiro.adapter.in.web.mapper;

import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPost;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.request.transacao.TransacaoPut;
import com.vinicius.gerenciamento_financeiro.adapter.in.web.response.transacao.TransacaoResponse;
import com.vinicius.gerenciamento_financeiro.domain.model.auditoria.Auditoria;
import com.vinicius.gerenciamento_financeiro.domain.model.categoria.Categoria;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.ConfiguracaoTransacao;
import com.vinicius.gerenciamento_financeiro.domain.model.transacao.Transacao;
import com.vinicius.gerenciamento_financeiro.domain.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.math.BigDecimal;
import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface TransacaoMapper {

    @Named("putToEntity")
    default Transacao toEntity(TransacaoPut put) {
        if (put == null) {
            return null;
        }

        return Transacao.builder()
                .id(put.id())
                .descricao(put.descricao())
                .valor(put.valor())
                .tipo(put.tipo())
                .data(put.data())
                .configuracao(criarConfiguracaoPadrao())
                .build();
    }

    @Named("postToEntity")
    default Transacao toEntity(TransacaoPost put, Categoria categoria, Usuario usuario, Auditoria auditoria) {
        if (put == null) {
            throw new IllegalArgumentException("Transação existente e dados de atualização não podem ser nulos");
        }

        return Transacao.builder()
                .descricao(put.descricao())
                .valor(put.valor())
                .tipo(put.tipoMovimentacao())
                .data(put.data())
                .categoria(categoria)
                .auditoria(auditoria)
                .usuario(usuario)
                .configuracao(criarConfiguracaoPadrao())
                .build();
    }


    private ConfiguracaoTransacao criarConfiguracaoPadrao() {
        return ConfiguracaoTransacao.builder()
                .recorrente(false)
                .parcelado(false)
                .dataVencimento(LocalDate.now())
                .build();
    }

    default TransacaoResponse toResponse(Transacao transacao) {

        return TransacaoResponse.builder()
                .id(transacao.getId())
                .description(transacao.getDescricao())
                .amount(transacao.getValor())
                .type(transacao.getTipo().name())
                .date(transacao.getData())
                .paid(transacao.getConfiguracao() != null && transacao.getConfiguracao().getDataPagamento() != null)
                .config(
                        transacao.getConfiguracao() != null ?
                                TransacaoResponse.ConfiguracaoTransacaoResponse.builder()
                                        .recorrente(transacao.getConfiguracao().isRecorrente())
                                        .periodicidade(transacao.getConfiguracao().getPeriodicidade())
                                        .parcelado(transacao.getConfiguracao().isParcelado())
                                        .dataVencimento(transacao.getConfiguracao().getDataVencimento())
                                        .dataPagamento(transacao.getConfiguracao().getDataPagamento())
                                        .build()
                                : null
                )
                .build();
    }

    default BigDecimal validarValor(BigDecimal novoValor, BigDecimal valorExistente) {
        if (novoValor == null) {
            return valorExistente;
        }
        if (novoValor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        return novoValor;
    }
}
