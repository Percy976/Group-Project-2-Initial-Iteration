package com.marketplace.service.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Pricing strategy for fixed and hourly services.
 * A valid price is required.
 */
public final class RequiredPriceStrategy implements PricingStrategy {

    /**
     * Validates and normalizes the submitted listing price.
     * @param rawPrice the raw price
     * @return the result of the operation
     */
    @Override
    public BigDecimal validate(String rawPrice) {
        String cleaned = rawPrice == null ? "" : rawPrice.trim();

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(
                    "Enter a price for fixed or hourly services.");
        }

        if (!cleaned.matches("[0-9]{1,8}(\\.[0-9]{1,2})?")) {
            throw new IllegalArgumentException(
                    "Enter a valid price with up to two decimal places "
                            + "and a maximum of 99999999.99.");
        }

        return new BigDecimal(cleaned)
                .setScale(2, RoundingMode.UNNECESSARY);
    }
}