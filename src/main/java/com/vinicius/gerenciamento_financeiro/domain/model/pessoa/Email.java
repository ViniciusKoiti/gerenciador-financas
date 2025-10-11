package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;


import lombok.Getter;


import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;


public class Email implements Serializable {

    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Getter
    private final String endereco;

    public Email(String endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Email não pode ser nulo");
        }

        if (!isValido(endereco)) {
            throw new IllegalArgumentException("Email inválido: " + endereco);
        }

        this.endereco = endereco.toLowerCase();
    }

    private boolean isValido(String email) {
        return PATTERN_EMAIL.matcher(email).matches();
    }

    @Override
    public String toString() {
        return endereco;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Email email = (Email) obj;
        return Objects.equals(this.endereco, email.endereco);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endereco);
    }
}