
package com.marketplace.dao;

import com.marketplace.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles listing management operations scoped to the listing owner.
 */
public class ManageListingDAO {

    /**
     * Represents Listing in the marketplace application.
     */
    public static class Listing {
        public int id;
        public String title;
        public String description;
        public String category;
        public BigDecimal price;
        public String priceType;
        public String status;
    }

    /**
     * Maps the current result-set row into a listing record.
     * @param rs the rs
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
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

    /**
     * Returns listings belonging to the specified user.
     * @param userId the user id
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
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

    /**
     * Retrieves a listing only when the specified user owns it.
     * @param listingId the listing id
     * @param userId the user id
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
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

    /**
     * Updates an existing record when the required ownership checks succeed.
     * @param listingId the listing id
     * @param userId the user id
     * @param title the title
     * @param description the description
     * @param category the category
     * @param price the price
     * @param priceType the price type
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
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

    /**
     * Sets the active.
     * @param listingId the listing id
     * @param userId the user id
     * @param active the active
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
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
