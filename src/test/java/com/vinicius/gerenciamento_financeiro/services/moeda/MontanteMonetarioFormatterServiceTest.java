package com.vinicius.gerenciamento_financeiro.services.moeda;

import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.application.service.moeda.MontanteMonetarioFormatterService;
import com.vinicius.gerenciamento_financeiro.port.out.moeda.MoedaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do MontanteMonetarioFormatterService")
class MontanteMonetarioFormatterServiceTest {

    @Mock
    private MoedaRepository moedaRepository;

    private MontanteMonetarioFormatterService service;

    @BeforeEach
    void setUp() {
        service = new MontanteMonetarioFormatterService(moedaRepository);
    }

    @Test
    @DisplayName("Deve formatar valor em BRL com locale brasileiro")
    void deveFormatarBRL() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(new BigDecimal("1234.56"));

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, allOf(
                containsString("1.234,56"),
                containsString("R$")
        ));
    }

    @Test
    @DisplayName("Deve formatar valor em USD com locale americano")
    void deveFormatarUSD() {
        // Given
        MontanteMonetario valor = MontanteMonetario.of(
                new BigDecimal("1234.56"),
                MoedaId.of("USD")
        );

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, allOf(
                containsString("1,234.56"),
                containsString("$")
        ));
    }

    @Test
    @DisplayName("Deve formatar valor em EUR com locale alemão")
    void deveFormatarEUR() {
        // Given
        MontanteMonetario valor = MontanteMonetario.of(
                new BigDecimal("1234.56"),
                MoedaId.of("EUR")
        );

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, allOf(
                containsString("1.234,56"),
                containsString("€")
        ));
    }

    @ParameterizedTest
    @MethodSource("provideMoedasEFormatos")
    @DisplayName("Deve formatar diferentes moedas corretamente")
    void deveFormatarDiferentesMoedas(String codigoMoeda, BigDecimal valor, String expectedPattern) {
        // Given
        MontanteMonetario montante = MontanteMonetario.of(valor, MoedaId.of(codigoMoeda));

        // When
        String formatado = service.formatarParaExibicao(montante);

        // Then
        assertThat(formatado, matchesPattern(expectedPattern));
    }

    private static Stream<Arguments> provideMoedasEFormatos() {
        return Stream.of(
                Arguments.of("BRL", new BigDecimal("1000.00"), ".*R\\$.*1\\.000,00.*"),
                Arguments.of("USD", new BigDecimal("1000.00"), ".*\\$.*1,000\\.00.*"),
                Arguments.of("EUR", new BigDecimal("1000.00"), ".*1\\.000,00.*€.*"),
                Arguments.of("GBP", new BigDecimal("1000.00"), ".*£.*1,000\\.00.*"),
                Arguments.of("CHF", new BigDecimal("1000.00"), "CHF 1’000.00")
        );
    }

    @Test
    @DisplayName("Deve formatar com locale específico quando fornecido")
    void deveFormatarComLocaleEspecifico() {
        // Given
        MontanteMonetario valor = MontanteMonetario.of(    new BigDecimal("1234.56"), MoedaId.of("USD"));
        Locale localeUS = Locale.US;

        // When
        String formatado = service.formatarComLocale(valor, localeUS);

        // Then
        // Formata BRL com padrão americano
        assertThat(formatado, containsString("1,234.56"));
    }

    @Test
    @DisplayName("Deve usar locale padrão para moeda desconhecida")
    void deveUsarLocalePadraoParaMoedaDesconhecida() {
        // Given
        MontanteMonetario valor = MontanteMonetario.of(    new BigDecimal("1234.56"), MoedaId.of("JPY"));
        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertNotNull(formatado);
        assertThat(formatado, not(isEmptyString()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando MontanteMonetario é nulo")
    void deveLancarExcecaoQuandoMontanteNulo() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.formatarComLocale(null, Locale.getDefault())
        );

        assertThat(exception.getMessage(),
                containsString("MontanteMonetario não pode ser nulo"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando Locale é nulo")
    void deveLancarExcecaoQuandoLocaleNulo() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(BigDecimal.TEN);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.formatarComLocale(valor, null)
        );

        assertThat(exception.getMessage(),
                containsString("Locale não pode ser nulo"));
    }

    @Test
    @DisplayName("Deve formatar valores com casas decimais")
    void deveFormatarValoresComCasasDecimais() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(new BigDecimal("0.99"));

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, containsString("0,99"));
    }

    @Test
    @DisplayName("Deve formatar valores negativos")
    void deveFormatarValoresNegativos() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(new BigDecimal("-1234.56"));

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, allOf(
                containsString("-"),
                containsString("1.234,56")
        ));
    }

    @Test
    @DisplayName("Deve formatar valor zero")
    void deveFormatarValorZero() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(BigDecimal.ZERO);

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertThat(formatado, allOf(
                containsString("0"),
                containsString("R$")
        ));
    }

    @Test
    @DisplayName("Deve formatar valores muito grandes")
    void deveFormatarValoresMuitoGrandes() {
        // Given
        MontanteMonetario valor = MontanteMonetario.ofBRL(
                new BigDecimal("999999999.99")
        );

        // When
        String formatado = service.formatarParaExibicao(valor);

        // Then
        assertNotNull(formatado);
        assertThat(formatado, containsString("999.999.999,99"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao formatar moeda inválida")
    void deveLancarExcecaoAoFormatarMoedaInvalida() {


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () ->   MontanteMonetario.of(
                        new BigDecimal("1234.56"),
                        MoedaId.of("INVALID")
                )
        );

        assertThat(exception.getMessage(), containsString("MoedaId deve ter exatamente 3 caracteres"));
    }
}