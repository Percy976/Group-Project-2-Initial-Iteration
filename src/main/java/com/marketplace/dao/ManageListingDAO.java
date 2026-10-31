
package com.marketplace.dao;

import com.marketplace.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManageListingDAO {

    public static class Listing {
        public int id;
        public String title;
        public String description;
        public String category;
        public BigDecimal price;
        public String priceType;
        public String status;
    }

    private Listing read(ResultSet rs) throws SQLException {
        Listing listing = new Listing();
        listing.id = rs.getInt("listing_id");
        listing.title = rs.getString("title");
        listing.description = rs.getString("description");
        listing.category = rs.getString("category");
        listing.price = rs.getBigDecimal("price");
        listing.priceType = rs.getString("price_type");
        listing.status = rs.getString("status");
        return listing;
    }

    public List<Listing> findByOwner(int userId)
            throws SQLException {

        String sql = """
            SELECT listing_id, title, description,
                   category, price, price_type, status
            FROM listings
            WHERE provider_id = ?
            ORDER BY listing_id DESC
            """;

        List<Listing> listings = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    listings.add(read(rs));
                }
            }
        }

        return listings;
    }

    public Listing findOwned(int listingId, int userId)
            throws SQLException {

        String sql = """
            SELECT listing_id, title, description,
                   category, price, price_type, status
            FROM listings
            WHERE listing_id = ? AND provider_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, listingId);
            stmt.setInt(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? read(rs) : null;
            }
        }
    }

    public boolean update(int listingId, int userId,
                          String title, String description,
                          String category, BigDecimal price,
                          String priceType) throws SQLException {

        String sql = """
            UPDATE listings
            SET title = ?, description = ?, category = ?,
                price = ?, price_type = ?
            WHERE listing_id = ? AND provider_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, category);

            if (price == null) {
                stmt.setNull(4, Types.DECIMAL);
            } else {
                stmt.setBigDecimal(4, price);
            }

            stmt.setString(5, priceType);
            stmt.setInt(6, listingId);
            stmt.setInt(7, userId);

            return stmt.executeUpdate() == 1;
        }
    }

    public boolean setActive(int listingId, int userId,
                             boolean active) throws SQLException {

        String sql = """
            UPDATE listings
            SET status = ?
            WHERE listing_id = ? AND provider_id = ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, active ? "ACTIVE" : "INACTIVE");
            stmt.setInt(2, listingId);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() == 1;
        }
    }
}
