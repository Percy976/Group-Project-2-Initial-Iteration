package com.marketplace.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a registered marketplace user.
 * A user can act as both a customer and a service provider.
 * Password credentials are handled separately.
 *
 * Related requirements: PR0001, PR0002, PR0005, PR0006.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private boolean admin;
    private LocalDateTime createdAt;

    /**
     * Creates a new User instance.
     */
    public User() {
    }

    /**
     * Creates a new User instance.
     * @param userId the user id
     * @param username the username
     * @param email the email
     * @param firstName the first name
     * @param lastName the last name
     * @param phoneNumber the phone number
     * @param bio the bio
     * @param admin the admin
     * @param createdAt the created at
     */
    public User(int userId, String username, String email,
                String firstName, String lastName,
                String phoneNumber, String bio,
                boolean admin, LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.admin = admin;
        this.createdAt = createdAt;
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
     * Returns the username.
     * @return the current username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
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

    /**
     * Returns the first name.
     * @return the current first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name.
     * @return the current last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the name.
     * @return the current name
     */
    public String getName() {
        String first = firstName == null ? "" : firstName;
        String last = lastName == null ? "" : lastName;
        return (first + " " + last).trim();
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
     * Returns the bio.
     * @return the current bio
     */
    public String getBio() {
        return bio;
    }

    /**
     * Sets the bio.
     * @param bio the bio
     */
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * Indicates whether admin is true.
     * @return whether the property is enabled
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Sets the admin.
     * @param admin the admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Returns the created at.
     * @return the current created at
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the created at.
     * @param createdAt the created at
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}