
package com.marketplace.dao;

import com.marketplace.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcServiceRequestDAO {

    public static class RequestRow {
        private int requestId;
        private int listingId;
        private int customerId;
        private int providerId;
        private String listingTitle;
        private String customerName;
        private String providerName;
        private String message;
        private String providerResponse;
        private String status;
        private Timestamp requestedAt;
        private boolean reviewed;

        public int getRequestId() { return requestId; }
        public int getListingId() { return listingId; }
        public int getCustomerId() { return customerId; }
        public int getProviderId() { return providerId; }
        public String getListingTitle() { return listingTitle; }
        public String getCustomerName() { return customerName; }
        public String getProviderName() { return providerName; }
        public String getMessage() { return message; }
        public String getProviderResponse() { return providerResponse; }
        public String getStatus() { return status; }
        public Timestamp getRequestedAt() { return requestedAt; }
        public boolean isReviewed() { return reviewed; }
    }

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

    public List<RequestRow> findByCustomer(int customerId)
            throws SQLException {

        return findRequests("""
            WHERE sr.customer_id = ?
            ORDER BY sr.requested_at DESC, sr.request_id DESC
            """, customerId);
    }

    public List<RequestRow> findByProvider(int providerId)
            throws SQLException {

        return findRequests("""
            WHERE l.provider_id = ?
            ORDER BY sr.requested_at DESC, sr.request_id DESC
            """, providerId);
    }

    private List<RequestRow> findRequests(
            String condition, int userId) throws SQLException {

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
