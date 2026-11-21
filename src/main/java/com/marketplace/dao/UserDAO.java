package com.marketplace.dao;

import java.sql.SQLException;
import java.util.Optional;

import com.marketplace.model.User;

/**
 * Defines database operations for marketplace user accounts.
 * Implementations handle SQL; the service layer handles validation
 * and password hashing.
 */
public interface UserDAO {

    /**
     * Creates a regular account using an already-hashed password.
     *
     * @return the generated user ID
     */
    int create(User user, String passwordHash) throws SQLException;

    /**
     * Looks up a single record by its identifier.
     * @param userId the user id
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    Optional<User> findById(int userId) throws SQLException;

    /**
     * Finds a user account by username.
     * @param username the username
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    Optional<User> findByUsername(String username) throws SQLException;

    /**
     * Finds a user account by email address.
     * @param email the email
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    Optional<User> findByEmail(String email) throws SQLException;

    /**
     * Retrieves the stored hash for authentication only.
     */
    Optional<String> findPasswordHashByUsername(String username)
            throws SQLException;

    /**
     * Updates contact/profile fields without changing credentials
     * or administrator privileges.
     *
     * @return true if the user record was updated
     */
    boolean updateProfile(User user) throws SQLException;
}