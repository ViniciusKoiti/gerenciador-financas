package com.vinicius.gerenciamento_financeiro.domain.model.moeda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MontanteMonetario")
class MontanteMonetarioTest {

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("deve criar MontanteMonetario com BigDecimal")
        void deveCriarComBigDecimal() {
            var moedaId = MoedaId.of("BRL");
            var amount = new BigDecimal("100.50");
            
            var montante = MontanteMonetario.of(amount, moedaId);
            
            assertThat(montante.getValor()).isEqualTo(amount);
            assertThat(montante.getMoedaId()).isEqualTo(moedaId);
        }

        @Test
        @DisplayName("deve criar MontanteMonetario com double")
        void deveCriarComDouble() {
            var moedaId = MoedaId.of("USD");
            var amount = 50.75;
            
            var montante = MontanteMonetario.of(amount, moedaId);
            
            assertThat(montante.getValor()).isEqualTo(BigDecimal.valueOf(amount));
            assertThat(montante.getMoedaId()).isEqualTo(moedaId);
        }

        @Test
        @DisplayName("deve criar MontanteMonetario com String")
        void deveCriarComString() {
            var moedaId = MoedaId.of("EUR");
            var amount = "250.33";
            
            var montante = MontanteMonetario.of(amount, moedaId);
            
            assertThat(montante.getValor()).isEqualTo(new BigDecimal(amount));
            assertThat(montante.getMoedaId()).isEqualTo(moedaId);
        }

        @Test
        @DisplayName("deve criar MontanteMonetario BRL")
        void deveCriarBRL() {
            var amount = new BigDecimal("100.00");
            
            var montante = MontanteMonetario.ofBRL(amount);
            
            assertThat(montante.getValor()).isEqualTo(amount);
            assertThat(montante.getMoedaId().getValor()).isEqualTo("BRL");
        }

        @Test
        @DisplayName("deve criar MontanteMonetario USD")
        void deveCriarUSD() {
            var amount = new BigDecimal("100.00");
            
            var montante = MontanteMonetario.ofUSD(amount);
            
            assertThat(montante.getValor()).isEqualTo(amount);
            assertThat(montante.getMoedaId().getValor()).isEqualTo("USD");
        }

        @Test
        @DisplayName("deve criar MontanteMonetario EUR")
        void deveCriarEUR() {
            var amount = new BigDecimal("100.00");
            
            var montante = MontanteMonetario.ofEUR(amount);
            
            assertThat(montante.getValor()).isEqualTo(amount);
            assertThat(montante.getMoedaId().getValor()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("deve criar MontanteMonetario zero")
        void deveCriarZero() {
            var moedaId = MoedaId.of("BRL");
            
            var montante = MontanteMonetario.zero(moedaId);
            
            assertThat(montante.getValor()).isEqualTo(BigDecimal.ZERO);
            assertThat(montante.getMoedaId()).isEqualTo(moedaId);
        }

        @Test
        @DisplayName("deve lançar exceção quando amount for nulo")
        void deveLancarExcecaoQuandoAmountNulo() {
            var moedaId = MoedaId.of("BRL");
            
            assertThatThrownBy(() -> MontanteMonetario.of((BigDecimal) null, moedaId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Amount não pode ser nulo");
        }

        @Test
        @DisplayName("deve lançar exceção quando moedaId for nulo")
        void deveLancarExcecaoQuandoMoedaIdNulo() {
            var amount = new BigDecimal("100.00");
            
            assertThatThrownBy(() -> MontanteMonetario.of(amount, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("CurrencyId não pode ser nulo");
        }
    }

    @Nested
    @DisplayName("Operações Matemáticas")
    class OperacoesMatematicas {

        @Test
        @DisplayName("deve somar montantes da mesma moeda")
        void deveSomarMontantesMesmaMoeda() {
            var montante1 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante2 = MontanteMonetario.ofBRL(new BigDecimal("50.00"));
            
            var resultado = montante1.add(montante2);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("150.00"));
            assertThat(resultado.getMoedaId()).isEqualTo(MoedaId.of("BRL"));
        }

        @Test
        @DisplayName("deve subtrair montantes da mesma moeda")
        void deveSubtrairMontantesMesmaMoeda() {
            var montante1 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante2 = MontanteMonetario.ofBRL(new BigDecimal("30.00"));
            
            var resultado = montante1.subtract(montante2);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("70.00"));
            assertThat(resultado.getMoedaId()).isEqualTo(MoedaId.of("BRL"));
        }

        @Test
        @DisplayName("deve multiplicar montante por BigDecimal")
        void deveMultiplicarPorBigDecimal() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var multiplicador = new BigDecimal("2.5");
            
            var resultado = montante.multiply(multiplicador);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("250.000"));
            assertThat(resultado.getMoedaId()).isEqualTo(MoedaId.of("BRL"));
        }

        @Test
        @DisplayName("deve multiplicar montante por double")
        void deveMultiplicarPorDouble() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var multiplicador = 1.5;
            
            var resultado = montante.multiply(multiplicador);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("150.000"));
        }

        @Test
        @DisplayName("deve dividir montante por BigDecimal")
        void deveDividirPorBigDecimal() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var divisor = new BigDecimal("4");
            
            var resultado = montante.divide(divisor);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("25.0000"));
        }

        @Test
        @DisplayName("deve dividir montante por double")
        void deveDividirPorDouble() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var divisor = 2.0;
            
            var resultado = montante.divide(divisor);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("50.0000"));
        }

        @Test
        @DisplayName("deve negar montante")
        void deveNegarMontante() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            var resultado = montante.negate();
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("-100.00"));
        }

        @Test
        @DisplayName("deve calcular valor absoluto")
        void deveCalcularValorAbsoluto() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("-100.00"));
            
            var resultado = montante.abs();
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("100.00"));
        }

        @Test
        @DisplayName("deve lançar exceção ao somar moedas diferentes")
        void deveLancarExcecaoAoSomarMoedasDiferentes() {
            var montanteBRL = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montanteUSD = MontanteMonetario.ofUSD(new BigDecimal("50.00"));
            
            assertThatThrownBy(() -> montanteBRL.add(montanteUSD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Não é possível realizar adição entre moedas diferentes");
        }

        @Test
        @DisplayName("deve lançar exceção ao dividir por zero")
        void deveLancarExcecaoAoDividirPorZero() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThatThrownBy(() -> montante.divide(BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível dividir por zero");
        }
    }

    @Nested
    @DisplayName("Comparações")
    class Comparacoes {

        @Test
        @DisplayName("deve identificar montante zero")
        void deveIdentificarMontanteZero() {
            var montante = MontanteMonetario.ofBRL(BigDecimal.ZERO);
            
            assertThat(montante.isZero()).isTrue();
        }

        @Test
        @DisplayName("deve identificar montante positivo")
        void deveIdentificarMontantePositivo() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThat(montante.isPositive()).isTrue();
            assertThat(montante.isNegative()).isFalse();
        }

        @Test
        @DisplayName("deve identificar montante negativo")
        void deveIdentificarMontanteNegativo() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("-100.00"));
            
            assertThat(montante.isNegative()).isTrue();
            assertThat(montante.isPositive()).isFalse();
        }

        @Test
        @DisplayName("deve comparar montantes corretamente")
        void deveCompararMontantesCorretamente() {
            var montante100 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante200 = MontanteMonetario.ofBRL(new BigDecimal("200.00"));
            var montante100Dup = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThat(montante200.isGreaterThan(montante100)).isTrue();
            assertThat(montante200.isGreaterThanOrEqual(montante100)).isTrue();
            assertThat(montante100.isGreaterThanOrEqual(montante100Dup)).isTrue();
            
            assertThat(montante100.isLessThan(montante200)).isTrue();
            assertThat(montante100.isLessThanOrEqual(montante200)).isTrue();
            assertThat(montante100.isLessThanOrEqual(montante100Dup)).isTrue();
            
            assertThat(montante100.isEqualTo(montante100Dup)).isTrue();
        }

        @Test
        @DisplayName("deve lançar exceção ao comparar moedas diferentes")
        void deveLancarExcecaoAoCompararMoedasDiferentes() {
            var montanteBRL = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montanteUSD = MontanteMonetario.ofUSD(new BigDecimal("100.00"));
            
            assertThatThrownBy(() -> montanteBRL.isGreaterThan(montanteUSD))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Não é possível realizar comparação entre moedas diferentes");
        }
    }

    @Nested
    @DisplayName("Conversão de Moeda")
    class ConversaoMoeda {

        @Test
        @DisplayName("deve converter entre moedas diferentes")
        void deveConverterEntreMoedasDiferentes() {
            var montanteBRL = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var taxaCambio = new BigDecimal("0.2"); // 1 BRL = 0.2 USD
            
            var montanteUSD = montanteBRL.convertTo(MoedaId.of("USD"), taxaCambio);
            
            assertThat(montanteUSD.getValor()).isEqualTo(new BigDecimal("20.0000"));
            assertThat(montanteUSD.getMoedaId().getValor()).isEqualTo("USD");
        }

        @Test
        @DisplayName("deve retornar o mesmo montante ao converter para a mesma moeda")
        void deveRetornarMesmoMontanteAoConverterParaMesmaMoeda() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var taxaCambio = new BigDecimal("1.0");
            
            var resultado = montante.convertTo(MoedaId.of("BRL"), taxaCambio);
            
            assertThat(resultado).isSameAs(montante);
        }

        @Test
        @DisplayName("deve lançar exceção com taxa de câmbio inválida")
        void deveLancarExcecaoComTaxaCambioInvalida() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThatThrownBy(() -> montante.convertTo(MoedaId.of("USD"), BigDecimal.ZERO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Exchange rate deve ser positivo");
        }
    }

    @Nested
    @DisplayName("Arredondamento")
    class Arredondamento {

        @Test
        @DisplayName("deve arredondar para escala específica")
        void deveArredondarParaEscalaEspecifica() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.12345"));
            
            var resultado = montante.roundToScale(2);
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("100.12"));
        }

        @Test
        @DisplayName("deve arredondar para escala padrão")
        void deveArredondarParaEscalaPadrao() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.12678"));
            
            var resultado = montante.roundToDefaultScale();
            
            assertThat(resultado.getValor()).isEqualTo(new BigDecimal("100.13"));
        }
    }

    @Nested
    @DisplayName("Utilitários")
    class Utilitarios {

        @Test
        @DisplayName("deve verificar se montantes têm a mesma moeda")
        void deveVerificarSeMesmaMoeda() {
            var montante1 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante2 = MontanteMonetario.ofBRL(new BigDecimal("200.00"));
            var montante3 = MontanteMonetario.ofUSD(new BigDecimal("100.00"));
            
            assertThat(montante1.isSameCurrency(montante2)).isTrue();
            assertThat(montante1.isSameCurrency(montante3)).isFalse();
            assertThat(montante1.isSameCurrency(null)).isFalse();
        }

        @Test
        @DisplayName("deve formatar para display corretamente")
        void deveFormatarParaDisplay() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.50"));
            
            var display = montante.toDisplayString();
            
            assertThat(display).isEqualTo("100.50 BRL");
        }
    }

    @Nested
    @DisplayName("Constantes")
    class Constantes {

        @Test
        @DisplayName("deve ter constantes zero corretas")
        void deveTerConstantesZeroCorretas() {
            assertThat(MontanteMonetario.ZERO_BRL.getValor()).isEqualTo(BigDecimal.ZERO);
            assertThat(MontanteMonetario.ZERO_BRL.getMoedaId().getValor()).isEqualTo("BRL");
            
            assertThat(MontanteMonetario.ZERO_EUR.getValor()).isEqualTo(BigDecimal.ZERO);
            assertThat(MontanteMonetario.ZERO_EUR.getMoedaId().getValor()).isEqualTo("EUR");
            
            assertThat(MontanteMonetario.ZERO_USD.getValor()).isEqualTo(BigDecimal.ZERO);
            assertThat(MontanteMonetario.ZERO_USD.getMoedaId().getValor()).isEqualTo("USD");
        }
    }

    @Nested
    @DisplayName("Equals e HashCode")
    class EqualsEHashCode {

        @Test
        @DisplayName("deve implementar equals corretamente")
        void deveImplementarEqualsCorretamente() {
            var montante1 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante2 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante3 = MontanteMonetario.ofBRL(new BigDecimal("200.00"));
            var montante4 = MontanteMonetario.ofUSD(new BigDecimal("100.00"));
            
            assertThat(montante1).isEqualTo(montante2);
            assertThat(montante1).isNotEqualTo(montante3);
            assertThat(montante1).isNotEqualTo(montante4);
            assertThat(montante1).isNotEqualTo(null);
            assertThat(montante1).isNotEqualTo("string");
        }

        @Test
        @DisplayName("deve implementar hashCode corretamente")
        void deveImplementarHashCodeCorretamente() {
            var montante1 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            var montante2 = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThat(montante1.hashCode()).isEqualTo(montante2.hashCode());
        }
    }

    @Nested
    @DisplayName("Validações de Parâmetros")
    class ValidacoesParametros {

        @ParameterizedTest
        @ValueSource(strings = {"BRL", "USD", "EUR", "GBP", "JPY"})
        @DisplayName("deve aceitar diferentes códigos de moeda válidos")
        void deveAceitarDiferentesCodigosMoedaValidos(String codigo) {
            var moedaId = MoedaId.of(codigo);
            var montante = MontanteMonetario.of(BigDecimal.TEN, moedaId);
            
            assertThat(montante.getMoedaId().getValor()).isEqualTo(codigo);
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "  ", "INVALID", "XX"})
        @DisplayName("deve lançar exceção com códigos de moeda inválidos")
        void deveLancarExcecaoComCodigosMoedaInvalidos(String codigo) {
            assertThatThrownBy(() -> MoedaId.of(codigo))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("deve lançar exceção com multiplicador nulo")
        void deveLancarExcecaoComMultiplicadorNulo() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThatThrownBy(() -> montante.multiply((BigDecimal) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Multiplier não pode ser nulo");
        }

        @Test
        @DisplayName("deve lançar exceção com divisor nulo")
        void deveLancarExcecaoComDivisorNulo() {
            var montante = MontanteMonetario.ofBRL(new BigDecimal("100.00"));
            
            assertThatThrownBy(() -> montante.divide((BigDecimal) null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Divisor não pode ser nulo");
        }
    }
}