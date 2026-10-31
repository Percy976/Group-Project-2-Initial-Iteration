
package com.marketplace.dao;

import com.marketplace.model.ServiceListing;
import com.marketplace.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class JdbcListingDAO {

    public List<ServiceListing> findActive(
            int limit, int offset, String searchQuery)
            throws SQLException {

        StringBuilder sql = new StringBuilder("""
                SELECT l.listing_id, l.provider_id, l.title,
                       l.description, l.category, l.price,
                       CONCAT_WS(' ', u.first_name, u.last_name)
                           AS provider_name
                FROM listings l
                JOIN users u ON l.provider_id = u.user_id
                WHERE l.status = 'ACTIVE'
                """);

        String[] keywords = searchQuery == null
                || searchQuery.isBlank()
                ? new String[0]
                : searchQuery.trim().split("\\s+");

        for (String keyword : keywords) {
            sql.append("""
                    
                    AND (
                        LOWER(l.title) LIKE ? ESCAPE '!'
                        OR LOWER(CONCAT_WS(' ', u.first_name, u.last_name))
                           LIKE ? ESCAPE '!'
                    )
                    """);
        }

        sql.append("""
                
                ORDER BY l.listing_id
                LIMIT ? OFFSET ?
                """);

        List<ServiceListing> listings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql.toString())) {

            int parameter = 1;

            for (String keyword : keywords) {
                String pattern = "%" + keyword.toLowerCase(Locale.ROOT)
                        .replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_") + "%";

                statement.setString(parameter++, pattern);
                statement.setString(parameter++, pattern);
            }

            statement.setInt(parameter++, limit);
            statement.setInt(parameter, offset);

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {
                    ServiceListing listing = new ServiceListing();

                    listing.setListingId(results.getInt("listing_id"));
                    listing.setProviderId(results.getInt("provider_id"));
                    listing.setProviderName(
                            results.getString("provider_name"));
                    listing.setServiceName(
                            results.getString("title"));
                    listing.setDescription(
                            results.getString("description"));
                    listing.setCategory(results.getString("category"));
                    listing.setPrice(results.getBigDecimal("price"));
                    listing.setActive(true);

                    listings.add(listing);
                }
            }
        }

        return listings;
    }

    public ServiceListing findById(int listingId) throws SQLException {

        String sql = """
                SELECT l.listing_id, l.provider_id, l.title,
                       l.description, l.category, l.price,
                       CONCAT_WS(' ', u.first_name, u.last_name)
                           AS provider_name
                FROM listings l
                JOIN users u ON l.provider_id = u.user_id
                WHERE l.listing_id = ?
                  AND l.status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, listingId);

            try (ResultSet results = statement.executeQuery()) {

                if (results.next()) {
                    ServiceListing listing = new ServiceListing();

                    listing.setListingId(results.getInt("listing_id"));
                    listing.setProviderId(results.getInt("provider_id"));
                    listing.setProviderName(
                            results.getString("provider_name"));
                    listing.setServiceName(
                            results.getString("title"));
                    listing.setDescription(
                            results.getString("description"));
                    listing.setCategory(results.getString("category"));
                    listing.setPrice(results.getBigDecimal("price"));
                    listing.setActive(true);

                    return listing;
                }
            }
        }

        return null;
    }

    public int create(int providerId, String title,
                      String description, String category,
                      BigDecimal price, String priceType)
            throws SQLException {

        String sql = """
                INSERT INTO listings
                    (provider_id, title, description, category,
                     price, price_type, status)
                VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, providerId);
            statement.setString(2, title);
            statement.setString(3, description);
            statement.setString(4, category);

            if (price == null) {
                statement.setNull(5, Types.DECIMAL);
            } else {
                statement.setBigDecimal(5, price);
            }

            statement.setString(6, priceType);

            if (statement.executeUpdate() != 1) {
                throw new SQLException("Could not create the listing.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException(
                    "Listing created but no generated ID was returned.");
        }
    }
}
