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

    /**
     * Creates a new ServiceReview instance.
     */
    public ServiceReview() {
    }

    /**
     * Creates a new ServiceReview instance.
     * @param reviewId the review id
     * @param requestId the request id
     * @param listingId the listing id
     * @param customerId the customer id
     * @param title the title
     * @param description the description
     * @param rating the rating
     */
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

    /**
     * Returns the review id.
     * @return the current review id
     */
    public int getReviewId() {
        return reviewId;
    }

    /**
     * Sets the review id.
     * @param reviewId the review id
     */
    public void setReviewId(int reviewId) {
        this.reviewId = reviewId;
    }

    /**
     * Returns the request id.
     * @return the current request id
     */
    public int getRequestId() {
        return requestId;
    }

    /**
     * Sets the request id.
     * @param requestId the request id
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    /**
     * Returns the listing id.
     * @return the current listing id
     */
    public int getListingId() {
        return listingId;
    }

    /**
     * Sets the listing id.
     * @param listingId the listing id
     */
    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    /**
     * Returns the customer id.
     * @return the current customer id
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer id.
     * @param customerId the customer id
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Returns the title.
     * @return the current title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title.
     * @param title the title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description.
     * @return the current description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the rating.
     * @return the current rating
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the rating.
     * @param rating the rating
     */
    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                    "Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }
}