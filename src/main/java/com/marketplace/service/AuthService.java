package com.marketplace.service;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.marketplace.dao.JdbcUserDAO;
import com.marketplace.dao.UserDAO;
import com.marketplace.model.User;
import com.marketplace.util.PasswordUtil;

/**
 * Handles account registration and authentication.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this(new JdbcUserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO);
    }

    /**
     * Registers a regular user and returns the generated account ID.
     */
    public int register(User user, String password)
            throws SQLException {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Account details are required.");
        }

        String username = required(
                user.getUsername(), "Username", 50);

        if (!username.matches("[A-Za-z0-9_]{3,50}")) {
            throw new IllegalArgumentException(
                    "Username must contain 3–50 letters, numbers, "
                    + "or underscores.");
        }

        String email = required(user.getEmail(), "Email", 255)
                .toLowerCase(Locale.ROOT);

        if (!email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new IllegalArgumentException(
                    "Enter a valid email address.");
        }

        if (password == null
                || password.length() < 8
                || password.length() > 128) {
            throw new IllegalArgumentException(
                    "Password must contain 8–128 characters.");
        }

        String firstName = required(
                user.getFirstName(), "First name", 100);
        String lastName = required(
                user.getLastName(), "Last name", 100);

        String phone = user.getPhoneNumber();
        if (phone != null) {
            phone = phone.trim();
            if (phone.length() > 30) {
                throw new IllegalArgumentException(
                        "Phone number must be 30 characters or fewer.");
            }
        }

        String bio = user.getBio();
        if (bio != null && bio.length() > 5000) {
            throw new IllegalArgumentException(
                    "Bio must be 5,000 characters or fewer.");
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phone);
        user.setAdmin(false);

        if (userDAO.findByUsername(username).isPresent()
                || userDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "That username or email is already registered.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);

        try {
            int userId = userDAO.create(user, passwordHash);
            user.setUserId(userId);
            return userId;
        } catch (SQLException e) {
            // Handles duplicate registrations arriving simultaneously.
            if (e.getErrorCode() == 1062) {
                throw new IllegalArgumentException(
                        "That username or email is already registered.");
            }
            throw e;
        }
    }

    /**
     * Returns the user when credentials are valid.
     * Database failures are propagated rather than treated as bad passwords.
     */
    public Optional<User> login(String username, String password)
            throws SQLException {

        if (username == null || password == null
                || username.isBlank()
                || username.length() > 50
                || password.isEmpty()
                || password.length() > 128) {
            return Optional.empty();
        }

        String cleanedUsername = username.trim();

        Optional<String> storedHash =
                userDAO.findPasswordHashByUsername(cleanedUsername);

        if (storedHash.isEmpty()
                || !PasswordUtil.verifyPassword(
                        password, storedHash.get())) {
            return Optional.empty();
        }

        return userDAO.findByUsername(cleanedUsername);
    }

    private String required(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }

        String cleaned = value.trim();

        if (cleaned.length() > maxLength) {
            throw new IllegalArgumentException(
                    label + " must be " + maxLength
                    + " characters or fewer.");
        }

        return cleaned;
    }
}