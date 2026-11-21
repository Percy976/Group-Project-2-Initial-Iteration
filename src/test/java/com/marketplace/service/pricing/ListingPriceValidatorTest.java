package com.marketplace.service.pricing;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests pricing strategies and their selection.
 */
class ListingPriceValidatorTest {

    private final ListingPriceValidator validator =
            new ListingPriceValidator();

    @Test
    void fixedPriceIsNormalizedToTwoDecimalPlaces() {
        assertEquals(
                new BigDecimal("25.00"),
                validator.validate("FIXED", " 25 "));
    }

    @Test
    void hourlyPriceAcceptsCents() {
        assertEquals(
                new BigDecimal("18.75"),
                validator.validate("HOURLY", "18.75"));
    }

    @Test
    void negotiablePriceCanBeBlankOrNull() {
        assertNull(validator.validate("NEGOTIABLE", ""));
        assertNull(validator.validate("NEGOTIABLE", "   "));
        assertNull(validator.validate("NEGOTIABLE", null));
    }

    @Test
    void negotiablePriceValidatesSuppliedAmount() {
        assertEquals(
                new BigDecimal("12.50"),
                validator.validate("NEGOTIABLE", "12.5"));

        assertThrows(IllegalArgumentException.class,
                () -> validator.validate("NEGOTIABLE", "-1"));
    }

    @Test
    void fixedAndHourlyPricesAreRequired() {
        for (String type : new String[]{"FIXED", "HOURLY"}) {
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validate(type, ""));
            assertThrows(IllegalArgumentException.class,
                    () -> validator.validate(type, null));
        }
    }

    @Test
    void zeroAndMaximumPriceAreAccepted() {
        assertEquals(
                new BigDecimal("0.00"),
                validator.validate("FIXED", "0"));

        assertEquals(
                new BigDecimal("99999999.99"),
                validator.validate("FIXED", "99999999.99"));
    }

    @Test
    void invalidAmountsAreRejectedForEveryPriceType() {
        String[] invalidAmounts = {
                "-1", "1.234", "100000000",
                "abc", "1e2", "12,50", ".50", "1."
        };

        for (String type :
                new String[]{"FIXED", "HOURLY", "NEGOTIABLE"}) {
            for (String amount : invalidAmounts) {
                assertThrows(
                        IllegalArgumentException.class,
                        () -> validator.validate(type, amount),
                        type + " should reject " + amount);
            }
        }
    }

    @Test
    void unknownPriceTypesAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate("OTHER", "25"));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate("", "25"));
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(null, "25"));
    }
}