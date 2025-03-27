package com.vinicius.gerenciamento_financeiro.adapter.in.web.request.pagamento.stripe;

import java.math.BigDecimal;


public record StripePaymentRequest(
        BigDecimal amount,
        String currency,
        String description,

        StripePaymentType stripePaymentType
) {

}
