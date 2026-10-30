
package com.marketplace.dao;

import com.marketplace.model.ServiceListing;
import com.marketplace.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcListingDAO {

    public List<ServiceListing> findActive(int limit, int offset)
            throws SQLException {

        String sql = """
                SELECT l.listing_id, l.provider_id, l.title,
                       l.description, l.price,
                       CONCAT(u.first_name, ' ', u.last_name) AS provider_name
                FROM listings l
                JOIN users u ON l.provider_id = u.user_id
                WHERE l.status = 'ACTIVE'
                ORDER BY l.listing_id
                LIMIT ? OFFSET ?
                """;

        List<ServiceListing> listings = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, limit);
            statement.setInt(2, offset);

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {

                    ServiceListing listing = new ServiceListing();

                    listing.setListingId(results.getInt("listing_id"));
                    listing.setProviderId(results.getInt("provider_id"));
                    listing.setProviderName(results.getString("provider_name"));
                    listing.setServiceName(results.getString("title"));
                    listing.setDescription(results.getString("description"));
                    listing.setPrice(results.getBigDecimal("price"));
                    listing.setActive(true);

                    listings.add(listing);
                }
            }
        }

        return listings;
    }
}
