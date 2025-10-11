package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class ClienteFiltro {
    String nome;
    String email;
    String cpf;
    String telefone;
    Boolean ativo;
    String buscaGeral;
    LocalDate dataNascimentoInicio;
    LocalDate dataNascimentoFim;

    public static ClienteFiltro vazio() {
        return ClienteFiltro.builder().build();
    }

    public boolean temAlgumFiltro() {
        return temTexto(nome)
                || temTexto(email)
                || temTexto(cpf)
                || temTexto(telefone)
                || temTexto(buscaGeral)
                || ativo != null
                || dataNascimentoInicio != null
                || dataNascimentoFim != null;
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.isBlank();
    }
}