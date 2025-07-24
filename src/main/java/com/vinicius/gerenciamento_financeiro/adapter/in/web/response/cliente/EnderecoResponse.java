package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do endereço")
public record EnderecoResponse(

        @Schema(description = "CEP", example = "12345-678")
        String cep,

        @Schema(description = "Logradouro", example = "Rua das Flores")
        String logradouro,

        @Schema(description = "Número", example = "123")
        String numero,

        @Schema(description = "Complemento", example = "Apto 45")
        String complemento,

        @Schema(description = "Bairro", example = "Centro")
        String bairro,

        @Schema(description = "Cidade", example = "São Paulo")
        String cidade,

        @Schema(description = "Estado", example = "SP")
        String estado,

        @Schema(description = "País", example = "Brasil")
        String pais
) {

    public EnderecoResponse(String cep, String logradouro, String numero,
                            String bairro, String cidade, String estado) {
        this(cep, logradouro, numero, null, bairro, cidade, estado, "Brasil");
    }

    public EnderecoResponse(String cidade, String estado) {
        this(null, null, null, null, null, cidade, estado, "Brasil");
    }
}
