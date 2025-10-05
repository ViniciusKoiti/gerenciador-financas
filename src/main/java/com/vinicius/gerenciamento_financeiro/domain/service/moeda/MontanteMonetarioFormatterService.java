package com.vinicius.gerenciamento_financeiro.domain.service.moeda;

import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MoedaId;
import com.vinicius.gerenciamento_financeiro.domain.model.moeda.MontanteMonetario;
import com.vinicius.gerenciamento_financeiro.port.out.moeda.MoedaRepository;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.Map;

@Service
public class MontanteMonetarioFormatterService {

    private static final Map<String, Locale> CURRENCY_LOCALES = Map.of(
            "BRL", Locale.of("pt", "BR"),
            "EUR", Locale.GERMANY,
            "USD", Locale.US,
            "GBP", Locale.UK,
            "CHF", Locale.of("de", "CH")
    );

    private final MoedaRepository moedaRepository;

    public MontanteMonetarioFormatterService(MoedaRepository moedaRepository) {
        this.moedaRepository = moedaRepository;
    }

    public String formatarParaExibicao(MontanteMonetario montante) { return formatarComLocale(montante, getLocaleFromCurrency(montante.getMoedaId())); }

    public String formatarComLocale(MontanteMonetario montante, Locale locale) {
        if (montante == null) { throw new IllegalArgumentException("MontanteMonetario não pode ser nulo"); }
        if (locale == null) { throw new IllegalArgumentException("Locale não pode ser nulo"); }
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(locale);
            Currency currency = Currency.getInstance(montante.getMoedaId().getValor());
            formatter.setCurrency(currency); return formatter.format(montante.getValor()); }
        catch (Exception e) {
            return String.format("%s %s", montante.getValor(), montante.getMoedaId().getValor());
        }
    }

    private Locale getLocaleFromCurrency(MoedaId moedaId) { return CURRENCY_LOCALES.getOrDefault(moedaId.getValor(), Locale.getDefault()); }


}
