package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;

import java.util.Objects;

public final class PessoaId {
    private final Long value;

    private PessoaId(Long value) {
        this.value = Objects.requireNonNull(value, "PessoaId não pode ser nulo");
    }

    public static PessoaId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("PessoaId deve ser um número positivo");
        }
        return new PessoaId(value);
    }

    public Long getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PessoaId pessoaId = (PessoaId) o;
        return Objects.equals(value, pessoaId.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return "PessoaId{" + value + '}'; }
}
