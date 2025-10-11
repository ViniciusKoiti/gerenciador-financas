package com.vinicius.gerenciamento_financeiro.domain.model.shared;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Paginacao {
    @Builder.Default
    int pagina = 0;

    @Builder.Default
    int tamanhoPagina = 20;

    String campoOrdenacao;
    OrdenacaoDirecao direcaoOrdenacao;

    public static Paginacao padrao() {
        return Paginacao.builder().build();
    }

    public OrdenacaoDirecao direcaoOrPadrao() {
        return direcaoOrdenacao != null ? direcaoOrdenacao : OrdenacaoDirecao.ASC;
    }

    public enum OrdenacaoDirecao {
        ASC, DESC;

        public static OrdenacaoDirecao from(String value) {
            if (value == null || value.isBlank()) {
                return null;
            }
            return "DESC".equalsIgnoreCase(value) ? DESC : ASC;
        }
    }
}