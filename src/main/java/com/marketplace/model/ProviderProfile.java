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

    public ProviderProfile() {
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}