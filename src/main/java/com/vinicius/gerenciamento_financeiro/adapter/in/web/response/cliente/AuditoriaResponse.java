package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Dados de auditoria")
public record AuditoriaResponse(

        @Schema(description = "Data de criação do registro")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataCriacao,

        @Schema(description = "Data da última atualização")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataAtualizacao,

        @Schema(description = "Usuário que criou o registro", example = "admin")
        String criadoPor,

        @Schema(description = "Usuário que fez a última atualização", example = "admin")
        String atualizadoPor,

        @Schema(description = "Versão do registro", example = "1")
        Integer versao
) {

    public AuditoriaResponse(LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this(dataCriacao, dataAtualizacao, null, null, 1);
    }
}