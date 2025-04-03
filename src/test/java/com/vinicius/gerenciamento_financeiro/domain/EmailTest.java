package com.vinicius.gerenciamento_financeiro.domain;

import com.vinicius.gerenciamento_financeiro.domain.model.pessoa.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class EmailTest {

    @Test
    @DisplayName("Deve criar um email válido")
    void deveCriarEmailValido() {
        // Act
        Email email = new Email("teste@exemplo.com");

        // Assert
        assertEquals("teste@exemplo.com", email.getEndereco());
    }

    @Test
    @DisplayName("Deve normalizar email para minúsculas")
    void deveNormalizarEmailParaMinusculas() {
        // Act
        Email email = new Email("TESTE@EXEMPLO.COM");

        // Assert
        assertEquals("teste@exemplo.com", email.getEndereco());
    }

    @ParameterizedTest
    @ValueSource(strings = {"teste@", "@exemplo.com", "teste@exemplo", "teste.exemplo.com", "teste@.com"})
    @DisplayName("Deve lançar exceção para email inválido")
    void deveLancarExcecaoParaEmailInvalido(String emailInvalido) {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> new Email(emailInvalido));
    }

    @Test
    @DisplayName("Deve lançar exceção para email nulo")
    void deveLancarExcecaoParaEmailNulo() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    @DisplayName("Emails iguais devem ser considerados iguais")
    void emailsIguaisDevemSerConsideradosIguais() {
        // Arrange
        Email email1 = new Email("teste@exemplo.com");
        Email email2 = new Email("TESTE@EXEMPLO.COM");

        // Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}