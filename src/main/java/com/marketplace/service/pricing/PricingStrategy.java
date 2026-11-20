package com.marketplace.service.pricing;

import java.math.BigDecimal;

/**
 * Strategy for validating and normalizing a listing price.
 * Implementations must be stateless so they can be shared safely.
 */
public interface PricingStrategy {

    /**
     * Returns the validated price, or null when pricing is optional.
     *
     * @throws IllegalArgumentException when the price is invalid
     */
    BigDecimal validate(String rawPrice);
}