package com.vinicius.gerenciamento_financeiro.domain;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Cpf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CpfTest {

    @Test
    @DisplayName("Deve criar um CPF válido")
    void deveCriarCpfValido() {
        // Act
        Cpf cpf = new Cpf("123.456.789-09");

        // Assert
        assertEquals("123.456.789-09", cpf.getNumero());
    }

    @Test
    @DisplayName("Deve criar um CPF válido mesmo com formato diferente")
    void deveCriarCpfValidoComFormatoDiferente() {
        // Act
        Cpf cpf = new Cpf("12345678909");

        // Assert
        assertEquals("123.456.789-09", cpf.getNumero());
    }

    @ParameterizedTest
    @ValueSource(strings = {"111.111.111-11", "000.000.000-00", "123.456.789-00"})
    @DisplayName("Deve lançar exceção para CPF inválido")
    void deveLancarExcecaoParaCpfInvalido(String cpfInvalido) {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> new Cpf(cpfInvalido));
    }

    @Test
    @DisplayName("Deve lançar exceção para CPF nulo")
    void deveLancarExcecaoParaCpfNulo() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
    }

    @Test
    @DisplayName("CPFs iguais devem ser considerados iguais")
    void cpfsIguaisDevemSerConsideradosIguais() {
        Cpf cpf1 = new Cpf("123.456.789-09");
        Cpf cpf2 = new Cpf("12345678909");

        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }
}