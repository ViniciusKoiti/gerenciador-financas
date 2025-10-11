package com.vinicius.gerenciamento_financeiro.application.command;

import lombok.Builder;
import lombok.Value;

/**
 * Comando genérico para paginação
 * Abstrai o Pageable do Spring para não vazar framework no domínio
 */
@Value
@Builder
public class PaginacaoCommand {
    @Builder.Default
    int pagina = 0;

    @Builder.Default
    int tamanhoPagina = 20;

    String campoOrdenacao;
    String direcaoOrdenacao; // "ASC" ou "DESC"

    public static PaginacaoCommand padrao() {
        return PaginacaoCommand.builder().build();
    }
}