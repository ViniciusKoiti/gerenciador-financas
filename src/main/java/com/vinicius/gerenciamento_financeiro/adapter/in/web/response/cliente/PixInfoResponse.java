package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "Informações do PIX do cliente")
public record PixInfoResponse(

        @Schema(description = "Tipo da chave PIX", example = "CPF",
                allowableValues = {"CPF", "EMAIL", "TELEFONE", "ALEATORIA"})
        String tipoChave,

        @Schema(description = "Valor da chave PIX", example = "123.456.789-00")
        String chave,

        @Schema(description = "Nome do banco", example = "Banco do Brasil")
        String banco,

        @Schema(description = "Indica se a chave está ativa", example = "true")
        Boolean ativo
) {

    public PixInfoResponse(String tipoChave, String chave) {
        this(tipoChave, chave, null, true);
    }

    public PixInfoResponse(String tipoChave, String chave, Boolean ativo) {
        this(tipoChave, chave, null, ativo);
    }
}