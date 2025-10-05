package com.vinicius.gerenciamento_financeiro.domain.model.moeda;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class MontanteMonetario {

    public static final MontanteMonetario ZERO_BRL = new MontanteMonetario(BigDecimal.ZERO, MoedaId.of("BRL"));
    public static final MontanteMonetario ZERO_EUR = new MontanteMonetario(BigDecimal.ZERO, MoedaId.of("EUR"));
    public static final MontanteMonetario ZERO_USD = new MontanteMonetario(BigDecimal.ZERO, MoedaId.of("USD"));

    private final BigDecimal valor;
    private final MoedaId moedaId;

    private MontanteMonetario(BigDecimal amount, MoedaId currencyId) {
        this.valor = Objects.requireNonNull(amount, "Amount não pode ser nulo");
        this.moedaId = Objects.requireNonNull(currencyId, "CurrencyId não pode ser nulo");
    }

    public static MontanteMonetario of(BigDecimal amount, MoedaId currencyId) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount não pode ser nulo");
        }
        if (currencyId == null) {
            throw new IllegalArgumentException("CurrencyId não pode ser nulo");
        }
        return new MontanteMonetario(amount, currencyId);
    }

    public static MontanteMonetario of(double amount, MoedaId currencyId) {
        return of(BigDecimal.valueOf(amount), currencyId);
    }

    public static MontanteMonetario of(String amount, MoedaId currencyId) {
        return of(new BigDecimal(amount), currencyId);
    }

    public static MontanteMonetario ofBRL(BigDecimal amount) {
        return of(amount, MoedaId.of("BRL"));
    }

    public static MontanteMonetario ofEUR(BigDecimal amount) {
        return of(amount, MoedaId.of("EUR"));
    }

    public static MontanteMonetario ofUSD(BigDecimal amount) {
        return of(amount, MoedaId.of("USD"));
    }

    public static MontanteMonetario zero(MoedaId currencyId) {
        return of(BigDecimal.ZERO, currencyId);
    }

    // Operações matemáticas
    public MontanteMonetario add(MontanteMonetario other) {
        validarMesmaMoeda(other, "adição");
        return new MontanteMonetario(this.valor.add(other.valor), this.moedaId);
    }

    public MontanteMonetario subtract(MontanteMonetario other) {
        validarMesmaMoeda(other, "subtração");
        return new MontanteMonetario(this.valor.subtract(other.valor), this.moedaId);
    }

    public MontanteMonetario multiply(BigDecimal multiplier) {
        if (multiplier == null) {
            throw new IllegalArgumentException("Multiplier não pode ser nulo");
        }
        return new MontanteMonetario(this.valor.multiply(multiplier), this.moedaId);
    }

    public MontanteMonetario multiply(double multiplier) {
        return multiply(BigDecimal.valueOf(multiplier));
    }

    public MontanteMonetario divide(BigDecimal divisor) {
        if (divisor == null) {
            throw new IllegalArgumentException("Divisor não pode ser nulo");
        }
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Não é possível dividir por zero");
        }
        return new MontanteMonetario(this.valor.divide(divisor, 4, RoundingMode.HALF_UP), this.moedaId);
    }

    public MontanteMonetario divide(double divisor) {
        return divide(BigDecimal.valueOf(divisor));
    }

    public MontanteMonetario negate() {
        return new MontanteMonetario(this.valor.negate(), this.moedaId);
    }

    public MontanteMonetario abs() {
        return new MontanteMonetario(this.valor.abs(), this.moedaId);
    }

    public boolean isZero() {
        return this.valor.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.valor.compareTo(BigDecimal.ZERO) < 0;
    }

    public boolean isGreaterThan(MontanteMonetario other) {
        validarMesmaMoeda(other, "comparação");
        return this.valor.compareTo(other.valor) > 0;
    }

    public boolean isGreaterThanOrEqual(MontanteMonetario other) {
        validarMesmaMoeda(other, "comparação");
        return this.valor.compareTo(other.valor) >= 0;
    }

    public boolean isLessThan(MontanteMonetario other) {
        validarMesmaMoeda(other, "comparação");
        return this.valor.compareTo(other.valor) < 0;
    }

    public boolean isLessThanOrEqual(MontanteMonetario other) {
        validarMesmaMoeda(other, "comparação");
        return this.valor.compareTo(other.valor) <= 0;
    }

    public boolean isEqualTo(MontanteMonetario other) {
        if (other == null) return false;
        return this.moedaId.equals(other.moedaId) &&
                this.valor.compareTo(other.valor) == 0;
    }

    public MontanteMonetario convertTo(MoedaId targetCurrency, BigDecimal exchangeRate) {
        if (targetCurrency == null) {
            throw new IllegalArgumentException("Target currency não pode ser nulo");
        }
        if (exchangeRate == null) {
            throw new IllegalArgumentException("Exchange rate não pode ser nulo");
        }
        if (exchangeRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Exchange rate deve ser positivo");
        }

        if (this.moedaId.equals(targetCurrency)) {
            return this;
        }

        BigDecimal convertedAmount = this.valor.multiply(exchangeRate)
                .setScale(4, RoundingMode.HALF_UP);

        return new MontanteMonetario(convertedAmount, targetCurrency);
    }

    public MontanteMonetario roundToScale(int scale) {
        return new MontanteMonetario(this.valor.setScale(scale, RoundingMode.HALF_UP), this.moedaId);
    }

    public MontanteMonetario roundToDefaultScale() {
        return roundToScale(2);
    }

    public boolean isSameCurrency(MontanteMonetario other) {
        return other != null && this.moedaId.equals(other.moedaId);
    }

    // Getters
    public BigDecimal getValor() {
        return valor;
    }

    public MoedaId getMoedaId() {
        return moedaId;
    }

    public String get() {
        return moedaId.getValor();
    }

    private void validarMesmaMoeda(MontanteMonetario other, String operacao) {
        if (other == null) {
            throw new IllegalArgumentException("MonetaryAmount não pode ser nulo para " + operacao);
        }
        if (!this.moedaId.equals(other.moedaId)) {
            throw new IllegalArgumentException(
                    String.format("Não é possível realizar %s entre moedas diferentes: %s e %s",
                            operacao, this.moedaId.getValor(), other.moedaId.getValor())
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MontanteMonetario that = (MontanteMonetario) o;
        return Objects.equals(valor, that.valor) &&
                Objects.equals(moedaId, that.moedaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor, moedaId);
    }

    @Override
    public String toString() {
        return String.format("MonetaryAmount{amount=%s, currency=%s}",
                valor, moedaId.getValor());
    }

    public String toDisplayString() {
        return String.format("%s %s", valor, moedaId.getValor());
    }
}
