package com.marketplace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

import com.marketplace.model.User;
import com.marketplace.util.DatabaseConnection;

/**
 * Stores and retrieves user accounts using JDBC and MySQL.
 */
public class JdbcUserDAO implements UserDAO {

    private static final String USER_COLUMNS =
            "user_id, username, email, first_name, last_name, "
            + "phone, bio, is_admin, created_at";

    /**
     * Persists a new record after applying the relevant ownership and validation constraints.
     * @param user the user
     * @param passwordHash the password hash
     * @return the identifier of the created record
     * @throws SQLException if the database operation fails
     */
    @Override
    public int create(User user, String passwordHash)
            throws SQLException {

        String sql = """
                INSERT INTO users
                    (username, email, password_hash, first_name,
                     last_name, phone, bio, is_admin)
                VALUES (?, ?, ?, ?, ?, ?, ?, FALSE)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, passwordHash);
            statement.setString(4, user.getFirstName());
            statement.setString(5, user.getLastName());
            statement.setString(6, user.getPhoneNumber());
            statement.setString(7, user.getBio());

            if (statement.executeUpdate() != 1) {
                throw new SQLException("User account was not created.");
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

            throw new SQLException("Generated user ID was not returned.");
        }
    }

    /**
     * Looks up a single record by its identifier.
     * @param userId the user id
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    @Override
    public Optional<User> findById(int userId) throws SQLException {
        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? Optional.of(mapUser(result))
                        : Optional.empty();
            }
        }
    }

    /**
     * Finds a user account by username.
     * @param username the username
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    @Override
    public Optional<User> findByUsername(String username)
            throws SQLException {

        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE username = ?";

        return findByText(sql, username);
    }

    /**
     * Finds a user account by email address.
     * @param email the email
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    @Override
    public Optional<User> findByEmail(String email)
            throws SQLException {

        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE email = ?";

        return findByText(sql, email);
    }

    /**
     * Retrieves the stored password hash used to authenticate a user.
     * @param username the username
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    @Override
    public Optional<String> findPasswordHashByUsername(String username)
            throws SQLException {

        String sql = """
                SELECT password_hash
                FROM users
                WHERE username = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? Optional.ofNullable(
                                result.getString("password_hash"))
                        : Optional.empty();
            }
        }
    }

    /**
     * Saves permitted account profile changes.
     * @param user the user
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
    @Override
    public boolean updateProfile(User user) throws SQLException {
        String sql = """
                UPDATE users
                SET email = ?, first_name = ?, last_name = ?,
                    phone = ?, bio = ?
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, user.getEmail());
            statement.setString(2, user.getFirstName());
            statement.setString(3, user.getLastName());
            statement.setString(4, user.getPhoneNumber());
            statement.setString(5, user.getBio());
            statement.setInt(6, user.getUserId());

            return statement.executeUpdate() == 1;
        }
    }

    /**
     * Executes a parameterized query for a user using a text key.
     * @param sql the sql
     * @param value the value
     * @return an optional result when a matching value exists
     * @throws SQLException if the database operation fails
     */
    private Optional<User> findByText(String sql, String value)
            throws SQLException {

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, value);

            try (ResultSet result = statement.executeQuery()) {
                return result.next()
                        ? Optional.of(mapUser(result))
                        : Optional.empty();
            }
        }
    }

    /**
     * Maps the current result-set row into a User model.
     * @param result the result
     * @return the result of the operation
     * @throws SQLException if the database operation fails
     */
    private User mapUser(ResultSet result) throws SQLException {
        User user = new User();

        user.setUserId(result.getInt("user_id"));
        user.setUsername(result.getString("username"));
        user.setEmail(result.getString("email"));
        user.setFirstName(result.getString("first_name"));
        user.setLastName(result.getString("last_name"));
        user.setPhoneNumber(result.getString("phone"));
        user.setBio(result.getString("bio"));
        user.setAdmin(result.getBoolean("is_admin"));

        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        return user;
    }
}