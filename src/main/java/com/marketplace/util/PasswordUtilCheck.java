package com.marketplace.util;

/**
 * Standalone checks for password hashing and verification.
 * Uses example passwords only.
 */
public class PasswordUtilCheck {

    /**
     * Runs the standalone password utility verification checks.
     * @param args the args
     */
    public static void main(String[] args) {
        String password = "ExamplePassword123!";

        String firstHash = PasswordUtil.hashPassword(password);
        String secondHash = PasswordUtil.hashPassword(password);

        check("Correct password accepted",
                PasswordUtil.verifyPassword(password, firstHash));

        check("Incorrect password rejected",
                !PasswordUtil.verifyPassword("WrongPassword!", firstHash));

        check("Random salts produce different hashes",
                !firstHash.equals(secondHash));

        check("Second hash verifies correctly",
                PasswordUtil.verifyPassword(password, secondHash));

        check("Malformed stored hash rejected",
                !PasswordUtil.verifyPassword(password, "invalid"));

        check("Null password rejected",
                !PasswordUtil.verifyPassword(null, firstHash));

        System.out.println("All password checks passed.");
    }

    /**
     * Reports the result of a password utility check.
     * @param description the description
     * @param passed the passed
     */
    private static void check(String description, boolean passed) {
        if (!passed) {
            throw new AssertionError("FAIL: " + description);
        }

        System.out.println("PASS: " + description);
    }
}