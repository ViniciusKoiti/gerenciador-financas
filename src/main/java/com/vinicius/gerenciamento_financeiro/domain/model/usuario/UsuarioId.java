package com.vinicius.gerenciamento_financeiro.domain.model.usuario;

import java.util.Objects;

public class UsuarioId {
    private final Long value;

    private UsuarioId(Long value) {
        this.value = Objects.requireNonNull(value, "UsuarioId não pode ser nulo");
    }

    public static UsuarioId of(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("UsuarioId deve ser um número positivo");
        }
        return new UsuarioId(value);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioId usuarioId = (UsuarioId) o;
        return Objects.equals(value, usuarioId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UsuarioId{" + value + '}';
    }
}