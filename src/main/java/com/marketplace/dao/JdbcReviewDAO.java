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
