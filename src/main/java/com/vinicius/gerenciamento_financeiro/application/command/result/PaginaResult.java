package com.vinicius.gerenciamento_financeiro.application.command.result;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Resultado paginado genérico
 * Não depende do Page do Spring
 */
@Value
@Builder
public class PaginaResult<T> {
    List<T> conteudo;
    int paginaAtual;
    int tamanhoPagina;
    long totalElementos;
    int totalPaginas;
    boolean temProxima;
    boolean temAnterior;

    public static <T> PaginaResult<T> of(
            List<T> conteudo,
            int pagina,
            int tamanho,
            long total
    ) {
        int totalPaginas = (int) Math.ceil((double) total / tamanho);

        return PaginaResult.<T>builder()
                .conteudo(conteudo)
                .paginaAtual(pagina)
                .tamanhoPagina(tamanho)
                .totalElementos(total)
                .totalPaginas(totalPaginas)
                .temProxima(pagina < totalPaginas - 1)
                .temAnterior(pagina > 0)
                .build();
    }
}