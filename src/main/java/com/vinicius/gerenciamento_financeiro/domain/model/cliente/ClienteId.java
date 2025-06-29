package com.vinicius.gerenciamento_financeiro.domain.model.cliente;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.PessoaId;

import java.util.Objects;

public class ClienteId {

    private final Long value;

    private ClienteId(Long value) {
        this.value = Objects.requireNonNull(value, "ClienteId não pode ser nulo");
    }

    public static ClienteId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("ClienteId deve ser um número positivo");
        }
        return new ClienteId(value);
    }

    public Long getValue() { return value; }
}
