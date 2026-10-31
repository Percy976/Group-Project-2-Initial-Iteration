
package com.marketplace.service;

import com.marketplace.util.PasswordUtil;
import com.marketplace.dao.JdbcUserDAO;
import com.marketplace.dao.PasswordResetDAO;
import com.marketplace.model.User;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

public class PasswordResetService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int EXPIRATION_MINUTES = 30;

    private final JdbcUserDAO userDAO = new JdbcUserDAO();
    private final PasswordResetDAO resetDAO = new PasswordResetDAO();

    // Generate a reset token for an existing email address.
    public Optional<String> requestReset(String email)
            throws SQLException {

        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        Optional<User> user = userDAO.findByEmail(email.trim());

        if (user.isEmpty()) {
            return Optional.empty();
        }

        byte[] randomBytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(token);

        LocalDateTime expiresAt = LocalDateTime.now()
                .plusMinutes(EXPIRATION_MINUTES);

        resetDAO.saveToken(
                user.get().getUserId(),
                tokenHash,
                expiresAt
        );

        // Return the original token for delivery by email.
        return Optional.of(token);
    }

    // Check whether a reset token is valid and unexpired.
    public Optional<Integer> validateToken(String token)
            throws SQLException {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return resetDAO.findValidUserId(hashToken(token));
    }

    // Convert the token to a SHA-256 hash for database storage.
    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(
                            java.nio.charset.StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is unavailable.", e
            );
        }
    }


    public boolean resetPassword(String token, String newPassword)
            throws SQLException {

        // Reject missing or invalid reset tokens.
        if (token == null || token.isBlank()) {
            return false;
        }

        // Validate the new password.
        if (newPassword == null
                || newPassword.length() < 8
                || newPassword.length() > 128) {
            throw new IllegalArgumentException(
                    "Password must contain between 8 and 128 characters."
            );
        }

        // Hash the new password using the existing authentication system.
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);

        // Hash the reset token before looking it up in MySQL.
        String tokenHash = hashToken(token);

        // Atomically update the password and invalidate reset tokens.
        return resetDAO.resetPassword(tokenHash, newPasswordHash);
    }

}
