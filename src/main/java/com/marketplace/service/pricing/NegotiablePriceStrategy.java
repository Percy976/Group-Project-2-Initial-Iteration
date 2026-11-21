package com.marketplace.service.pricing;

import java.math.BigDecimal;

/**
 * Pricing strategy for negotiable services.
 * A price is optional, but must be valid when supplied.
 */
public final class NegotiablePriceStrategy implements PricingStrategy {

    private final PricingStrategy requiredPriceStrategy =
            new RequiredPriceStrategy();

    /**
     * Validates and normalizes the submitted listing price.
     * @param rawPrice the raw price
     * @return the result of the operation
     */
    @Override
    public BigDecimal validate(String rawPrice) {
        if (rawPrice == null || rawPrice.trim().isEmpty()) {
            return null;
        }

        return requiredPriceStrategy.validate(rawPrice);
    }
}