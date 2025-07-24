package com.vinicius.gerenciamento_financeiro.adapter.in.web.response.cliente;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Resposta com dados do cliente")
public record ClienteResponse(

        @Schema(description = "ID único do cliente", example = "1")
        Long id,

        @Schema(description = "Nome completo do cliente", example = "João Silva Santos")
        String nome,

        @Schema(description = "CPF do cliente", example = "123.456.789-00")
        String cpf,

        @Schema(description = "Email do cliente", example = "joao.silva@email.com")
        String email,

        @Schema(description = "Telefone do cliente", example = "(11) 99999-9999")
        String telefone,

        @Schema(description = "Data de nascimento", example = "1990-05-15")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,

        @Schema(description = "Endereço completo do cliente")
        EnderecoResponse endereco,

        @Schema(description = "ID do usuário proprietário", example = "1")
        Long usuarioId,

        @Schema(description = "Informações do PIX")
        PixInfoResponse pixInfo,

        @Schema(description = "Indica se o cliente está ativo", example = "true")
        Boolean ativo,

        @Schema(description = "Dados de auditoria")
        AuditoriaResponse auditoria
) {

    public ClienteResponse(Long id, String nome, String cpf, String email,
                           String telefone, LocalDate dataNascimento,
                           EnderecoResponse endereco, Long usuarioId,
                           PixInfoResponse pixInfo, Boolean ativo) {
        this(id, nome, cpf, email, telefone, dataNascimento, endereco,
                usuarioId, pixInfo, ativo, null);
    }

    // Construtor mínimo para listagens
    public ClienteResponse(Long id, String nome, String cpf, String email, Boolean ativo) {
        this(id, nome, cpf, email, null, null, null, null, null, ativo, null);
    }
}
