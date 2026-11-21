
package com.marketplace.dao;

import com.marketplace.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides JDBC access to service requests and their status transitions.
 */
public class JdbcServiceRequestDAO {

    /**
     * Holds service request details for presentation.
     */
    public static class RequestRow {
        private int requestId;
        private int listingId;
        private int customerId;
        private int providerId;
        private String listingTitle;
        private String customerName;
        private String customerEmail;
        private String customerPhone;
        private String providerName;
        private String message;
        private String providerResponse;
        private String status;
        private Timestamp requestedAt;
        private boolean reviewed;

        /**
         * Returns the request id.
         * @return the current request id
         */
        public int getRequestId() { return requestId; }
        /**
         * Returns the listing id.
         * @return the current listing id
         */
        public int getListingId() { return listingId; }
        /**
         * Returns the customer id.
         * @return the current customer id
         */
        public int getCustomerId() { return customerId; }
        /**
         * Returns the provider id.
         * @return the current provider id
         */
        public int getProviderId() { return providerId; }
        /**
         * Returns the listing title.
         * @return the current listing title
         */
        public String getListingTitle() { return listingTitle; }
        /**
         * Returns the customer name.
         * @return the current customer name
         */
        public String getCustomerName() { return customerName; }
        /**
         * Returns the customer email.
         * @return the current customer email
         */
        public String getCustomerEmail() { return customerEmail; }
        /**
         * Returns the customer phone.
         * @return the current customer phone
         */
        public String getCustomerPhone() { return customerPhone; }
        /**
         * Returns the provider name.
         * @return the current provider name
         */
        public String getProviderName() { return providerName; }
        /**
         * Returns the message.
         * @return the current message
         */
        public String getMessage() { return message; }
        /**
         * Returns the provider response.
         * @return the current provider response
         */
        public String getProviderResponse() { return providerResponse; }
        /**
         * Returns the status.
         * @return the current status
         */
        public String getStatus() { return status; }
        /**
         * Returns the requested at.
         * @return the current requested at
         */
        public Timestamp getRequestedAt() { return requestedAt; }
        /**
         * Indicates whether reviewed is true.
         * @return whether the property is enabled
         */
        public boolean isReviewed() { return reviewed; }
    }

    /**
     * Persists a new record after applying the relevant ownership and validation constraints.
     * @param listingId the listing id
     * @param customerId the customer id
     * @param message the message
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
    public boolean create(int listingId, int customerId,
                          String message) throws SQLException {

        String sql = """
            INSERT INTO service_requests
                (listing_id, customer_id, message, status)
            SELECT listing_id, ?, ?, 'PENDING'
            FROM listings
            WHERE listing_id = ?
              AND status = 'ACTIVE'
              AND provider_id <> ?
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, message);
            stmt.setInt(3, listingId);
            stmt.setInt(4, customerId);

            return stmt.executeUpdate() == 1;
        }
    }

    /**
     * Retrieves requests submitted by a particular customer.
     * @param customerId the customer id
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
    public List<RequestRow> findByCustomer(int customerId)
            throws SQLException {

        return findRequests("""
            WHERE sr.customer_id = ?
            ORDER BY sr.requested_at DESC, sr.request_id DESC
            """, customerId, false);
    }

    /**
     * Retrieves requests for listings owned by a particular provider.
     * @param providerId the provider id
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
    public List<RequestRow> findByProvider(int providerId)
            throws SQLException {

        return findRequests("""
            WHERE l.provider_id = ?
            ORDER BY sr.requested_at DESC, sr.request_id DESC
            """, providerId, true);
    }

    /**
     * Executes the request lookup query with the selected visibility rules.
     * @param condition the condition
     * @param userId the user id
     * @param includeCustomerContact the include customer contact
     * @return the matching records
     * @throws SQLException if the database operation fails
     */
    private List<RequestRow> findRequests(
            String condition, int userId,
            boolean includeCustomerContact) throws SQLException {

        // Customer contact details are retrieved only for requests
        // addressed to a listing owned by the current user.
        String customerContactColumns = includeCustomerContact
                ? ", c.email AS customer_email, c.phone AS customer_phone\n"
                : ", NULL AS customer_email, NULL AS customer_phone\n";

        String sql = """
            SELECT sr.request_id, sr.listing_id,
                   sr.customer_id, l.provider_id,
                   l.title AS listing_title,
                   CONCAT_WS(' ', c.first_name, c.last_name)
                       AS customer_name,
                   CONCAT_WS(' ', p.first_name, p.last_name)
                       AS provider_name,
                   sr.message, sr.provider_response,
                   sr.status, sr.requested_at,
                   CASE WHEN r.review_id IS NULL
                        THEN 0 ELSE 1 END AS reviewed
            """ + customerContactColumns + """
            FROM service_requests sr
            JOIN listings l ON sr.listing_id = l.listing_id
            JOIN users c ON sr.customer_id = c.user_id
            JOIN users p ON l.provider_id = p.user_id
            LEFT JOIN reviews r ON sr.request_id = r.request_id
            """ + condition;

        List<RequestRow> requests = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    RequestRow row = new RequestRow();

                    row.requestId = rs.getInt("request_id");
                    row.listingId = rs.getInt("listing_id");
                    row.customerId = rs.getInt("customer_id");
                    row.providerId = rs.getInt("provider_id");
                    row.listingTitle = rs.getString("listing_title");
                    row.customerName = rs.getString("customer_name");
                    row.customerEmail = rs.getString("customer_email");
                    row.customerPhone = rs.getString("customer_phone");
                    row.providerName = rs.getString("provider_name");
                    row.message = rs.getString("message");
                    row.providerResponse =
                            rs.getString("provider_response");
                    row.status = rs.getString("status");
                    row.requestedAt = rs.getTimestamp("requested_at");
                    row.reviewed = rs.getBoolean("reviewed");

                    requests.add(row);
                }
            }
        }

        return requests;
    }

    /**
     * Changes a request status subject to the appropriate user and status checks.
     * @param requestId the request id
     * @param userId the user id
     * @param action the action
     * @param response the response
     * @return true if the operation succeeds; false otherwise
     * @throws SQLException if the database operation fails
     */
    public boolean updateStatus(int requestId, int userId,
                                String action, String response)
            throws SQLException {

        String newStatus;
        String requiredStatus;
        boolean providerAction;

        switch (action) {
            case "accept":
                newStatus = "ACCEPTED";
                requiredStatus = "PENDING";
                providerAction = true;
                break;

            case "decline":
                newStatus = "DECLINED";
                requiredStatus = "PENDING";
                providerAction = true;
                break;

            case "complete":
                newStatus = "COMPLETED";
                requiredStatus = "ACCEPTED";
                providerAction = true;
                break;

            case "cancel":
                newStatus = "CANCELLED";
                requiredStatus = "PENDING";
                providerAction = false;
                break;

            default:
                return false;
        }

        String sql;

        if (providerAction) {
            sql = """
                UPDATE service_requests sr
                JOIN listings l ON sr.listing_id = l.listing_id
                SET sr.status = ?,
                    sr.provider_response =
                        CASE WHEN ? = 'complete'
                             THEN sr.provider_response
                             ELSE ? END
                WHERE sr.request_id = ?
                  AND l.provider_id = ?
                  AND sr.status = ?
                """;
        } else {
            sql = """
                UPDATE service_requests sr
                SET sr.status = ?
                WHERE sr.request_id = ?
                  AND sr.customer_id = ?
                  AND sr.status = ?
                """;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);

            if (providerAction) {
                stmt.setString(2, action);
                stmt.setString(3, response);
                stmt.setInt(4, requestId);
                stmt.setInt(5, userId);
                stmt.setString(6, requiredStatus);
            } else {
                stmt.setInt(2, requestId);
                stmt.setInt(3, userId);
                stmt.setString(4, requiredStatus);
            }

            return stmt.executeUpdate() == 1;
        }
    }
}
