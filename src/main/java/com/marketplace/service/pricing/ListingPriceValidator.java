package com.marketplace.service.pricing;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Selects a pricing strategy based on the listing's price type.
 * All strategies are stateless and the registry is immutable.
 */
public final class ListingPriceValidator {

    private final Map<String, PricingStrategy> strategies;

    public ListingPriceValidator() {
        PricingStrategy required = new RequiredPriceStrategy();

        strategies = Map.of(
                "FIXED", required,
                "HOURLY", required,
                "NEGOTIABLE", new NegotiablePriceStrategy());
    }

    public BigDecimal validate(String priceType, String rawPrice) {
        String cleanedType = priceType == null ? "" : priceType.trim();

        PricingStrategy strategy = strategies.get(cleanedType);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Select a valid price type.");
        }

        return strategy.validate(rawPrice);
    }
}