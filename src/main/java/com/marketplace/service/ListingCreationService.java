package com.marketplace.service;

import com.marketplace.dao.JdbcListingDAO;
import com.marketplace.service.pricing.ListingPriceValidator;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Business logic for creating service listings.
 * Keeps validation separate from HTTP request handling.
 */
public final class ListingCreationService {

    private final JdbcListingDAO listingDAO;
    private final ListingPriceValidator priceValidator;

    /**
     * Creates a new ListingCreationService instance.
     */
    public ListingCreationService() {
        this(new JdbcListingDAO());
    }

    /**
     * Allows a test DAO to be supplied without connecting to MySQL.
     */
    public ListingCreationService(JdbcListingDAO listingDAO) {
        this.listingDAO = Objects.requireNonNull(listingDAO);
        this.priceValidator = new ListingPriceValidator();
    }

    /**
     * Persists a new record after applying the relevant ownership and validation constraints.
     * @param providerId the provider id
     * @param title the title
     * @param description the description
     * @param category the category
     * @param rawPrice the raw price
     * @param priceType the price type
     * @return the identifier of the created record
     * @throws SQLException if the database operation fails
     */
    public int create(int providerId, String title,
                      String description, String category,
                      String rawPrice, String priceType)
            throws SQLException {

        if (providerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid logged-in account is required.");
        }

        String cleanedTitle = requiredText(title, "Title", 150);
        String cleanedDescription =
                requiredText(description, "Description", 10000);
        String cleanedCategory =
                requiredText(category, "Category", 100);
        String cleanedPriceType = text(priceType);

        BigDecimal price =
                priceValidator.validate(cleanedPriceType, rawPrice);

        return listingDAO.create(
                providerId,
                cleanedTitle,
                cleanedDescription,
                cleanedCategory,
                price,
                cleanedPriceType);
    }

    /**
     * Normalizes optional text input.
     * @param value the value
     * @return the result of the operation
     */
    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Validates required text and its maximum length.
     * @param value the value
     * @param label the label
     * @param maximumLength the maximum length
     * @return the result of the operation
     */
    private static String requiredText(
            String value, String label, int maximumLength) {

        String cleaned = text(value);

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(
                    label + " is required.");
        }

        if (cleaned.length() > maximumLength) {
            throw new IllegalArgumentException(
                    label + " must be "
                            + maximumLength + " characters or fewer.");
        }

        return cleaned;
    }
}