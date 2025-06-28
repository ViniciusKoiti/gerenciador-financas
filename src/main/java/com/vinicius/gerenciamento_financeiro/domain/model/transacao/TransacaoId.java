package com.vinicius.gerenciamento_financeiro.domain.model.transacao;

import java.util.Objects;

public final class TransacaoId {

    private final Long value;

    private TransacaoId(Long value) {
        this.value = Objects.requireNonNull(value, "TransacaoId não pode ser nulo");
    }

    public static TransacaoId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("TransacaoId deve ser um número positivo");
        }
        return new TransacaoId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransacaoId that = (TransacaoId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "TransacaoId{" + value + '}';
    }
}