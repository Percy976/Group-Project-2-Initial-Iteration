
package com.marketplace.dao;

import com.marketplace.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class PasswordResetDAO {

    public void saveToken(int userId, String tokenHash,
                          LocalDateTime expiresAt)
            throws SQLException {

        String sql = """
                INSERT INTO password_reset_tokens
                    (user_id, token_hash, expires_at)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, tokenHash);
            statement.setTimestamp(3, Timestamp.valueOf(expiresAt));

            statement.executeUpdate();
        }
    }

    public Optional<Integer> findValidUserId(String tokenHash)
            throws SQLException {

        String sql = """
                SELECT user_id
                FROM password_reset_tokens
                WHERE token_hash = ?
                  AND expires_at > CURRENT_TIMESTAMP
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, tokenHash);

            try (ResultSet results = statement.executeQuery()) {

                if (results.next()) {
                    return Optional.of(results.getInt("user_id"));
                }
            }
        }

        return Optional.empty();
    }

    public void deleteTokensForUser(int userId) throws SQLException {

        String sql = """
                DELETE FROM password_reset_tokens
                WHERE user_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }


    public boolean resetPassword(String tokenHash, String newPasswordHash)
            throws SQLException {

        String findTokenSql = """
            SELECT user_id
            FROM password_reset_tokens
            WHERE token_hash = ?
              AND expires_at > CURRENT_TIMESTAMP
            FOR UPDATE
            """;

        String updatePasswordSql = """
            UPDATE users
            SET password_hash = ?
            WHERE user_id = ?
            """;

        String deleteTokensSql = """
            DELETE FROM password_reset_tokens
            WHERE user_id = ?
            """;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                int userId;

                // Find and lock the valid token.
                try (PreparedStatement statement =
                             connection.prepareStatement(findTokenSql)) {

                    statement.setString(1, tokenHash);

                    try (ResultSet results = statement.executeQuery()) {

                        if (!results.next()) {
                            connection.rollback();
                            return false;
                        }

                        userId = results.getInt("user_id");
                    }
                }

                // Update the user's password.
                try (PreparedStatement statement =
                             connection.prepareStatement(updatePasswordSql)) {

                    statement.setString(1, newPasswordHash);
                    statement.setInt(2, userId);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Password update failed."
                        );
                    }
                }

                // Invalidate all outstanding reset tokens for this user.
                try (PreparedStatement statement =
                             connection.prepareStatement(deleteTokensSql)) {

                    statement.setInt(1, userId);
                    statement.executeUpdate();
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            }
        }
    }

}
