package com.marketplace.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hashes and verifies passwords using PBKDF2 with SHA-256.
 *
 * Each password receives a random salt.
 * Stored format: algorithm$iterations$salt$hash
 *
 * Related requirements: PR0001, PR0002.
 */
public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
        // Utility class: instances are not needed.
    }

    /**
     * Creates a salted hash for storage in the database.
     */
    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password must not be empty.");
        }

        byte[] salt = new byte[SALT_BYTES];
        RANDOM.nextBytes(salt);

        byte[] hash = deriveKey(password, salt, ITERATIONS);

        return ALGORITHM + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Checks a supplied password against a stored hash.
     */
    public static boolean verifyPassword(String password,
                                         String storedHash) {
        if (password == null || storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split("\\$", -1);

            if (parts.length != 4 || !ALGORITHM.equals(parts[0])) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations != ITERATIONS) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[3]);

            if (salt.length != SALT_BYTES
                    || expectedHash.length != KEY_BITS / 8) {
                return false;
            }

            byte[] actualHash =
                    deriveKey(password, salt, iterations);

            return MessageDigest.isEqual(
                    expectedHash, actualHash);

        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] deriveKey(String password,
                                    byte[] salt,
                                    int iterations) {
        char[] characters = password.toCharArray();

        PBEKeySpec specification = new PBEKeySpec(
                characters, salt, iterations, KEY_BITS);

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            return factory.generateSecret(specification).getEncoded();

        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException(
                    "Password hashing is unavailable.", ex);

        } finally {
            specification.clearPassword();
            Arrays.fill(characters, '\0');
        }
    }
}