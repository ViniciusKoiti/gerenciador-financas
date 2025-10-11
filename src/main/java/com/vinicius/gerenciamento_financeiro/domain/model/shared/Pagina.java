package com.vinicius.gerenciamento_financeiro.domain.model.shared;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class Pagina<T> {
    List<T> conteudo;
    int paginaAtual;
    int tamanhoPagina;
    long totalElementos;
    int totalPaginas;
    boolean temProxima;
    boolean temAnterior;

    public static <T> Pagina<T> of(List<T> conteudo, int pagina, int tamanho, long total) {
        int tamanhoSeguro = tamanho <= 0 ? 1 : tamanho;
        int totalPaginas = (int) Math.ceil((double) total / tamanhoSeguro);

        return Pagina.<T>builder()
                .conteudo(conteudo)
                .paginaAtual(pagina)
                .tamanhoPagina(tamanhoSeguro)
                .totalElementos(total)
                .totalPaginas(totalPaginas)
                .temProxima(pagina < totalPaginas - 1)
                .temAnterior(pagina > 0)
                .build();
    }
}