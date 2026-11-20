package com.marketplace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marketplace.model.ServiceReview;
import com.marketplace.util.DatabaseConnection;

public class JdbcReviewDAO {

    public boolean create(ServiceReview review) throws SQLException {

        String sql = """
                INSERT INTO reviews
                (request_id, listing_id, customer_id, title, description, rating)
                SELECT sr.request_id, sr.listing_id, sr.customer_id, ?, ?, ?
                FROM service_requests sr
                WHERE sr.request_id = ?
                AND sr.customer_id = ?
                AND sr.status = 'COMPLETED'
                AND sr.listing_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, review.getTitle());
            statement.setString(2, review.getDescription());
            statement.setInt(3, review.getRating());
            statement.setInt(4, review.getRequestId());
            statement.setInt(5, review.getCustomerId());
            statement.setInt(6, review.getListingId());

            return statement.executeUpdate() > 0;
        }
    }


    /**
     * Updates a review only if it belongs to the current customer and listing.
     */
    public boolean update(int reviewId, int listingId, int customerId,
                          String title, String description, int rating)
            throws SQLException {

        String sql = """
                UPDATE reviews
                SET title = ?, description = ?, rating = ?
                WHERE review_id = ? AND listing_id = ? AND customer_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, rating);
            statement.setInt(4, reviewId);
            statement.setInt(5, listingId);
            statement.setInt(6, customerId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a review only if it belongs to the current customer and listing.
     */
    public boolean delete(int reviewId, int listingId, int customerId)
            throws SQLException {

        String sql = """
                DELETE FROM reviews
                WHERE review_id = ? AND listing_id = ? AND customer_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reviewId);
            statement.setInt(2, listingId);
            statement.setInt(3, customerId);
            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Returns reviews written by the supplied customer, newest first.
     * Listing names are loaded alongside the reviews for the account page.
     */
    public List<MyReviewRow> findByCustomerId(int customerId) throws SQLException {
        String sql = """
                SELECT r.review_id, r.request_id, r.listing_id, r.customer_id,
                       r.title, r.description, r.rating, l.title AS service_name
                FROM reviews r
                JOIN listings l ON l.listing_id = r.listing_id
                WHERE r.customer_id = ?
                ORDER BY r.created_at DESC, r.review_id DESC
                """;
        List<MyReviewRow> reviews = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    ServiceReview review = new ServiceReview(
                            results.getInt("review_id"),
                            results.getInt("request_id"),
                            results.getInt("listing_id"),
                            results.getInt("customer_id"),
                            results.getString("title"),
                            results.getString("description"),
                            results.getInt("rating")
                    );
                    reviews.add(new MyReviewRow(review, results.getString("service_name")));
                }
            }
        }
        return reviews;
    }

    public record MyReviewRow(ServiceReview review, String listingName) {}

    public List<ServiceReview> findByListingId(int listingId) throws SQLException {

        List<ServiceReview> reviews = new ArrayList<>();

        String sql = """
                SELECT review_id, request_id, listing_id, customer_id,
                       title, description, rating
                FROM reviews
                WHERE listing_id = ?
                ORDER BY created_at DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listingId);

            try (ResultSet results = statement.executeQuery()) {

                while (results.next()) {
                    reviews.add(new ServiceReview(
                            results.getInt("review_id"),
                            results.getInt("request_id"),
                            results.getInt("listing_id"),
                            results.getInt("customer_id"),
                            results.getString("title"),
                            results.getString("description"),
                            results.getInt("rating")
                    ));
                }
            }
        }

        return reviews;
    }


    public Double findAverageRatingByListingId(int listingId) throws SQLException {

        String sql = """
                SELECT AVG(rating) AS average_rating
                FROM reviews
                WHERE listing_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, listingId);

            try (ResultSet results = statement.executeQuery()) {

                if (results.next()) {
                    double average = results.getDouble("average_rating");

                    return results.wasNull() ? null : average;
                }
            }
        }

        return null;
    }
}
