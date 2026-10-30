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
    private BigDecimal price;
    private String serviceArea;
    private boolean active;
    private double averageRating;
    private int reviewCount;

    public ServiceListing() {
        this.price = BigDecimal.ZERO;
        this.active = true;
    }

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

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public int getProviderId() {
        return providerId;
    }

    public void setProviderId(int providerId) {
        this.providerId = providerId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
}