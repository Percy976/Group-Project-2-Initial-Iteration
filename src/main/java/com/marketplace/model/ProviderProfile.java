package com.marketplace.model;

import java.io.Serializable;

/**
 * Represents the public profile of a service provider.
 *
 * Each profile belongs to one registered user.
 * That user can continue to act as a customer.
 *
 * Related requirements: PR0006, PR0010, PR0011.
 */
public class ProviderProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String providerName;
    private String description;
    private String serviceArea;
    private String phoneNumber;
    private String email;

    /**
     * Creates a new ProviderProfile instance.
     */
    public ProviderProfile() {
    }

    /**
     * Creates a new ProviderProfile instance.
     * @param userId the user id
     * @param providerName the provider name
     * @param description the description
     * @param serviceArea the service area
     * @param phoneNumber the phone number
     * @param email the email
     */
    public ProviderProfile(int userId, String providerName,
                           String description, String serviceArea,
                           String phoneNumber, String email) {
        this.userId = userId;
        this.providerName = providerName;
        this.description = description;
        this.serviceArea = serviceArea;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    /**
     * Returns the user id.
     * @return the current user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param userId the user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
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
     * Returns the phone number.
     * @return the current phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phone number.
     * @param phoneNumber the phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the email.
     * @return the current email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}