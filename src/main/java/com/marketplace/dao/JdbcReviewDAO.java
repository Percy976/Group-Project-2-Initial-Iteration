package com.marketplace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.marketplace.model.ServiceReview;
import com.marketplace.util.DatabaseConnection;

/**
 * Provides JDBC operations for reviews, eligibility checks, and rating summaries.
 */
public class JdbcReviewDAO {

    /**
     * Persists a new record after applying the relevant ownership and validation constraints.
     * @param review the review
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
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
     * Summarizes whether a customer may create another review for this listing.
     * Each completed service request may have at most one review.
     */
    public enum ReviewEligibility {
        NO_REQUEST, NOT_COMPLETED, ALREADY_REVIEWED, ELIGIBLE
    }

    /**
     * Returns the review eligibility.
     * @param listingId the listing id
     * @param customerId the customer id
     * @return the current review eligibility
     * @throws SQLException if the database operation fails
     */
    public ReviewEligibility getReviewEligibility(int listingId, int customerId)
            throws SQLException {
        String sql = """
                SELECT sr.status, r.review_id
                FROM service_requests sr
                LEFT JOIN reviews r ON r.request_id = sr.request_id
                WHERE sr.listing_id = ? AND sr.customer_id = ?
                """;

        boolean hasRequest = false;
        boolean hasReviewedCompletedRequest = false;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, listingId);
            statement.setInt(2, customerId);
            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    hasRequest = true;
                    if ("COMPLETED".equals(results.getString("status"))) {
                        if (results.getObject("review_id") == null) {
                            return ReviewEligibility.ELIGIBLE;
                        }
                        hasReviewedCompletedRequest = true;
                    }
                }
            }
        }
        if (!hasRequest) return ReviewEligibility.NO_REQUEST;
        if (hasReviewedCompletedRequest) return ReviewEligibility.ALREADY_REVIEWED;
        return ReviewEligibility.NOT_COMPLETED;
    }

    /**
     * Selects an eligible, unreviewed completed request on the server.
     * No request ID supplied by the browser is trusted or required.
     */
    public boolean createForEligibleRequest(int listingId, int customerId,
                                            String title, String description, int rating)
            throws SQLException {
        String sql = """
                INSERT INTO reviews
                    (request_id, listing_id, customer_id, title, description, rating)
                SELECT sr.request_id, sr.listing_id, sr.customer_id, ?, ?, ?
                FROM service_requests sr
                LEFT JOIN reviews r ON r.request_id = sr.request_id
                WHERE sr.listing_id = ?
                  AND sr.customer_id = ?
                  AND sr.status = 'COMPLETED'
                  AND r.review_id IS NULL
                ORDER BY sr.request_id ASC
                LIMIT 1
                """;
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, title);
            statement.setString(2, description);
            statement.setInt(3, rating);
            statement.setInt(4, listingId);
            statement.setInt(5, customerId);
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

    /**
     * Represents MyReviewRow in the marketplace application.
     */
    public record MyReviewRow(ServiceReview review, String listingName) {}

    /**
     * Retrieves reviews associated with the specified listing.
     * @param listingId the listing id
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
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


    /**
     * Calculates the average rating for the specified listing.
     * @param listingId the listing id
     * @return the result of the operation
     * @throws SQLException if the database operation fails
     */
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
