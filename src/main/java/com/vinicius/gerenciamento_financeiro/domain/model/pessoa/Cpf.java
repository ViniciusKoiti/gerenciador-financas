package com.vinicius.gerenciamento_financeiro.domain.model.pessoa;

import lombok.Getter;


import java.io.Serializable;
import java.util.Objects;

public class Cpf implements Serializable {

    @Getter
    private final String numero;

    public Cpf(String numero) {
        if (numero == null) {
            throw new IllegalArgumentException("CPF não pode ser nulo");
        }

        String cpfLimpo = limpar(numero);

        if (!isValido(cpfLimpo)) {
            throw new IllegalArgumentException("CPF inválido: " + numero);
        }

        this.numero = formatarCpf(cpfLimpo);
    }

    private String limpar(String cpf) {
        return cpf.replaceAll("[^0-9]", "");
    }

    private boolean isValido(String cpf) {
        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Valida os dígitos verificadores
        int[] numeros = new int[11];
        for (int i = 0; i < 11; i++) {
            numeros[i] = Character.getNumericValue(cpf.charAt(i));
        }

        // Validação do primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += numeros[i] * (10 - i);
        }

        int resto = soma % 11;
        int dv1 = (resto < 2) ? 0 : 11 - resto;

        if (numeros[9] != dv1) {
            return false;
        }

        // Validação do segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += numeros[i] * (11 - i);
        }

        resto = soma % 11;
        int dv2 = (resto < 2) ? 0 : 11 - resto;

        return numeros[10] == dv2;
    }

    private String formatarCpf(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public String toString() {
        return numero;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cpf cpf = (Cpf) obj;
        return Objects.equals(numero, cpf.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

}