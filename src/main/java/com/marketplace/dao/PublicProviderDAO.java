
package com.marketplace.dao;

import com.marketplace.model.ServiceListing;
import com.marketplace.model.User;
import com.marketplace.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PublicProviderDAO {

    public Optional<User> findProvider(int providerId)
            throws SQLException {

        String sql = """
                SELECT user_id, username, first_name,
                       last_name, bio
                FROM users
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, providerId);

            try (ResultSet rs = statement.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                User provider = new User();
                provider.setUserId(rs.getInt("user_id"));
                provider.setUsername(rs.getString("username"));
                provider.setFirstName(rs.getString("first_name"));
                provider.setLastName(rs.getString("last_name"));
                provider.setBio(rs.getString("bio"));

                return Optional.of(provider);
            }
        }
    }

    public List<ServiceListing> findActiveListings(int providerId)
            throws SQLException {

        String sql = """
                SELECT listing_id, provider_id, title,
                       description, price
                FROM listings
                WHERE provider_id = ?
                  AND status = 'ACTIVE'
                ORDER BY listing_id DESC
                """;

        List<ServiceListing> listings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, providerId);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ServiceListing listing = new ServiceListing();

                    listing.setListingId(rs.getInt("listing_id"));
                    listing.setProviderId(rs.getInt("provider_id"));
                    listing.setServiceName(rs.getString("title"));
                    listing.setDescription(rs.getString("description"));
                    listing.setPrice(rs.getBigDecimal("price"));
                    listing.setActive(true);

                    listings.add(listing);
                }
            }
        }

        return listings;
    }
}
