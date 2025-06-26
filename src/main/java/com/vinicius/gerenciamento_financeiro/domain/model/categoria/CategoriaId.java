package com.vinicius.gerenciamento_financeiro.domain.model.categoria;

import java.util.Objects;

public class CategoriaId {
    private final Long value;

    private CategoriaId(Long value) {
        this.value = Objects.requireNonNull(value, "CategoriaId não pode ser nulo");
    }

    public static CategoriaId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("CategoriaId deve ser um número positivo");
        }
        return new CategoriaId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaId that = (CategoriaId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CategoriaId{" + value + '}';
    }
}