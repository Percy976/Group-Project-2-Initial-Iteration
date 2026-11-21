
package com.marketplace.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a service advertised in the marketplace.
 *
 * providerId identifies the user who owns the provider profile.
 * Average ratings are calculated from reviews when listings are loaded.
 *
 * Related requirements: PR0007, PR0008, PR0009, PR0012, PR0022.
 */
public class ServiceListing implements Serializable {

    private static final long serialVersionUID = 1L;

    private int listingId;
    private int providerId;
    private String serviceName;
    private String description;
    private String category;
    private BigDecimal price;
    private String serviceArea;
    private boolean active;
    private double averageRating;
    private int reviewCount;
    private String providerName;

    /**
     * Creates a new ServiceListing instance.
     */
    public ServiceListing() {
        this.price = BigDecimal.ZERO;
        this.active = true;
    }

    /**
     * Creates a new ServiceListing instance.
     * @param listingId the listing id
     * @param providerId the provider id
     * @param serviceName the service name
     * @param description the description
     * @param price the price
     * @param serviceArea the service area
     * @param active the active
     */
    public ServiceListing(int listingId, int providerId,
                          String serviceName, String description,
                          BigDecimal price, String serviceArea,
                          boolean active) {
        this.listingId = listingId;
        this.providerId = providerId;
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.serviceArea = serviceArea;
        this.active = active;
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
     * Returns the provider id.
     * @return the current provider id
     */
    public int getProviderId() {
        return providerId;
    }

    /**
     * Sets the provider id.
     * @param providerId the provider id
     */
    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    /**
     * Returns the service name.
     * @return the current service name
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the service name.
     * @param serviceName the service name
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
     * Returns the category.
     * @return the current category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the category.
     * @param category the category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the price.
     * @return the current price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price.
     * @param price the price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Returns the service area.
     * @return the current service area
     */
    public String getServiceArea() {
        return serviceArea;
    }

    /**
     * Sets the service area.
     * @param serviceArea the service area
     */
    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    /**
     * Indicates whether active is true.
     * @return whether the property is enabled
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     * @param active the active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the average rating.
     * @return the current average rating
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Sets the average rating.
     * @param averageRating the average rating
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Returns the review count.
     * @return the current review count
     */
    public int getReviewCount() {
        return reviewCount;
    }

    /**
     * Sets the review count.
     * @param reviewCount the review count
     */
    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    /**
     * Returns the provider name.
     * @return the current provider name
     */
    public String getProviderName() {
        return providerName;
    }

    /**
     * Sets the provider name.
     * @param providerName the provider name
     */
    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}
