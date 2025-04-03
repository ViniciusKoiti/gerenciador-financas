package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.regex.Pattern;

@Embeddable
@NoArgsConstructor
@EqualsAndHashCode
public class Email implements Serializable {

    private static final Pattern PATTERN_EMAIL =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    @Getter
    private String endereco;

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
}