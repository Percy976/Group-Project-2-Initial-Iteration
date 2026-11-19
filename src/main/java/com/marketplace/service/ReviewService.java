package com.marketplace.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.marketplace.dao.JdbcReviewDAO;
import com.marketplace.model.ServiceReview;

public class ReviewService {

    private final JdbcReviewDAO reviewDAO;

    public ReviewService() {
        this(new JdbcReviewDAO());
    }


    public ReviewService(JdbcReviewDAO reviewDAO) {
        this.reviewDAO = Objects.requireNonNull(reviewDAO);
    }

    public boolean create(ServiceReview review) throws SQLException {

        Objects.requireNonNull(review, "Review cannot be null.");

        if (review.getRequestId() <= 0
                || review.getListingId() <= 0
                || review.getCustomerId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid request, listing, and customer IDs are required.");
        }


        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }

        if (review.getTitle() == null || review.getTitle().isBlank()
                || review.getDescription() == null
                || review.getDescription().isBlank()) {
            throw new IllegalArgumentException(
                    "Review title and description are required.");
        }

        return reviewDAO.create(review);
    }

    public List<ServiceReview> findByListingId(int listingId)
            throws SQLException {

        if (listingId <= 0) {
            throw new IllegalArgumentException(
                    "A valid listing ID is required!");
        }

        return reviewDAO.findByListingId(listingId);
    }

    public Double findAverageRatingByListingId(int listingId)
            throws SQLException {

        if (listingId <= 0) {
            throw new IllegalArgumentException(
                    "A valid listing ID is required!");
        }

        return reviewDAO.findAverageRatingByListingId(listingId);
    }
    
}
