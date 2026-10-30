package com.marketplace.model;

import java.io.Serializable;

/**
 * Represents a registered marketplace user.
 *
 * Supports account identity and contact information.
 * A user can act as both a customer and a service provider.
 * Password credentials are handled separately by the authentication layer.
 *
 * Related requirements: PR0001, PR0002, PR0005, PR0006.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public User() {
    }

    public User(int userId, String name, String email,
                String phoneNumber, String address) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}