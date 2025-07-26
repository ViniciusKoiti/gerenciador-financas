package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.cliente;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;


/**
 * DTO para receber os filtros de busca de clientes via API.
 * Representa a camada de entrada (adapter) e será convertido para ClienteFiltro (domínio).
 */
@Schema(description = "Filtros para busca de clientes")
public record ClienteFiltroRequest(

        @Schema(description = "Nome do cliente para filtro", example = "João Silva") @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres") String nome,

        @Schema(description = "CPF do cliente para filtro (apenas números ou formatado)", example = "12345678901") @Size(max = 14, message = "CPF deve ter no máximo 14 caracteres") String cpf,

        @Schema(description = "Email do cliente para filtro", example = "joao@email.com") @Size(max = 150, message = "Email deve ter no máximo 150 caracteres") String email,

        @Schema(description = "Telefone do cliente para filtro", example = "11999999999") @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres") String telefone,

        @Schema(description = "Status ativo do cliente", example = "true") Boolean ativo,

        @Schema(description = "Busca geral (pesquisa em nome ou email)", example = "joão silva") @Size(min = 2, max = 200, message = "Busca geral deve ter entre 2 e 200 caracteres") String buscaGeral,

        @Schema(description = "Data de nascimento início para filtro de período", example = "1990-01-01") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dataNascimentoInicio,

        @Schema(description = "Data de nascimento fim para filtro de período", example = "2000-12-31") @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dataNascimentoFim) {

    public boolean temFiltros() {
        return temValor(nome) || temValor(cpf) || temValor(email) || temValor(telefone) || ativo != null || temValor(buscaGeral) || dataNascimentoInicio != null || dataNascimentoFim != null;
    }

    public boolean temBuscaGeral() {
        return temValor(buscaGeral) && buscaGeral.trim().length() >= 2;
    }

    public boolean temFiltroNome() {
        return temValor(nome);
    }

    public boolean temPeriodoNascimento() {
        return dataNascimentoInicio != null || dataNascimentoFim != null;
    }

    public boolean isPeriodoNascimentoValido() {
        if (dataNascimentoInicio == null || dataNascimentoFim == null) {
            return true;
        }

        return !dataNascimentoInicio.isAfter(dataNascimentoFim);
    }

    public ClienteFiltroRequest limpar() {
        return new ClienteFiltroRequest(limparString(nome), limparString(cpf), limparString(email), limparString(telefone), ativo, limparString(buscaGeral), dataNascimentoInicio, dataNascimentoFim);
    }

    public boolean isValido() {
        if (!isPeriodoNascimentoValido()) {
            return false;
        }

        if (buscaGeral != null && !buscaGeral.trim().isEmpty() && buscaGeral.trim().length() < 2) {
            return false;
        }

        return true;
    }

    public String obterMensagemErro() {
        if (!isPeriodoNascimentoValido()) {
            return "Data de início do período não pode ser posterior à data de fim";
        }

        if (buscaGeral != null && buscaGeral.trim().length() == 1) {
            return "Busca geral deve ter pelo menos 2 caracteres";
        }

        return "Filtros inválidos";
    }

    public static Builder builder() {
        return new Builder();
    }

    private boolean temValor(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private String limparString(String valor) {
        return valor != null ? valor.trim() : null;
    }

    public static class Builder {
        private String nome;
        private String cpf;
        private String email;
        private String telefone;
        private Boolean ativo;
        private String buscaGeral;
        private LocalDate dataNascimentoInicio;
        private LocalDate dataNascimentoFim;

        public Builder nome(String nome) {
            this.nome = nome;
            return this;
        }

        public Builder cpf(String cpf) {
            this.cpf = cpf;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder telefone(String telefone) {
            this.telefone = telefone;
            return this;
        }

        public Builder ativo(Boolean ativo) {
            this.ativo = ativo;
            return this;
        }

        public Builder buscaGeral(String buscaGeral) {
            this.buscaGeral = buscaGeral;
            return this;
        }

        public Builder dataNascimentoInicio(LocalDate data) {
            this.dataNascimentoInicio = data;
            return this;
        }

        public Builder dataNascimentoFim(LocalDate data) {
            this.dataNascimentoFim = data;
            return this;
        }

        public Builder periodo(LocalDate inicio, LocalDate fim) {
            this.dataNascimentoInicio = inicio;
            this.dataNascimentoFim = fim;
            return this;
        }

        public ClienteFiltroRequest build() {
            return new ClienteFiltroRequest(nome, cpf, email, telefone, ativo, buscaGeral, dataNascimentoInicio, dataNascimentoFim);
        }
    }
}


