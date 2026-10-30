package com.marketplace.model;

import java.io.Serializable;

/**
 * Represents a customer's review of a completed service.
 *
 * The business layer checks that the request is completed
 * and belongs to the customer submitting the review.
 *
 * Related requirements: PR0019, PR0020, PR0021, PR0022.
 */
public class ServiceReview implements Serializable {

    private static final long serialVersionUID = 1L;

    private int reviewId;
    private int requestId;
    private int listingId;
    private int customerId;
    private String title;
    private String description;
    private int rating;

    public ServiceReview() {
    }

    public ServiceReview(int reviewId, int requestId,
                         int listingId, int customerId,
                         String title, String description,
                         int rating) {
        this.reviewId = reviewId;
        this.requestId = requestId;
        this.listingId = listingId;
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        setRating(rating);
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }
}