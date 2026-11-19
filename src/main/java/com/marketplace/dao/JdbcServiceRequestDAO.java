package com.marketplace.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.marketplace.model.ServiceRequest;
import com.marketplace.util.DatabaseConnection;

public class JdbcServiceRequestDAO implements ServiceRequestDAO {

    private ServiceRequest mapRequest(ResultSet results)
            throws SQLException {

                
        ServiceRequest request = new ServiceRequest();

        request.setRequestId(results.getInt("request_id"));
        request.setListingId(results.getInt("listing_id"));
        request.setCustomerId(results.getInt("customer_id"));
        request.setProviderId(results.getInt("provider_id"));
        request.setJobDetails(results.getString("message"));
        request.setSubmissionMessage(results.getString("message"));

        Timestamp timestamp = results.getTimestamp("requested_at");

        if (timestamp != null) {
            request.setSubmissionDate(timestamp.toLocalDateTime());
        }

        request.setStatus(
                ServiceRequest.Status.valueOf(results.getString("status")));

        request.setResponseMessage(
                results.getString("provider_response"));

        return request;
    }

    private String baseSelect() {
        return """
            SELECT sr.request_id, sr.listing_id, sr.customer_id,
                   l.provider_id, sr.message, sr.status,
                   sr.provider_response, sr.requested_at
            FROM service_requests sr
            JOIN listings l ON sr.listing_id = l.listing_id
            """;
    }

    @Override
    public int create(ServiceRequest request) throws SQLException {

        String sql = """
            INSERT INTO service_requests
                (listing_id, customer_id, message)
            SELECT listing_id, ?, ?
            FROM listings
            WHERE listing_id = ?
              AND provider_id <> ?
              AND status = 'ACTIVE'
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, request.getCustomerId());
            statement.setString(2, request.getJobDetails());
            statement.setInt(3, request.getListingId());
            statement.setInt(4, request.getCustomerId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return 0;
            }

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        return 0;
    }

    @Override
    public ServiceRequest findById(int requestId)
            throws SQLException {

        String sql = baseSelect() + " WHERE sr.request_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, requestId);

            try (ResultSet results = statement.executeQuery()) {
                if (results.next()) {
                    return mapRequest(results);
                }
            }
        }

        return null;
    }

    @Override
    public List<ServiceRequest> findByCustomerId(int customerId)
            throws SQLException {

        String sql = baseSelect()
                + " WHERE sr.customer_id = ? ORDER BY sr.requested_at DESC";

        return findRequests(sql, customerId);
    }

    @Override
    public List<ServiceRequest> findByProviderId(int providerId)
            throws SQLException {

        String sql = baseSelect()
                + " WHERE l.provider_id = ? ORDER BY sr.requested_at DESC";

        return findRequests(sql, providerId);
    }

    private List<ServiceRequest> findRequests(
            String sql, int userId) throws SQLException {

        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    requests.add(mapRequest(results));
                }
            }
        }

        return requests;
    }

    @Override
    public boolean updateStatusByProvider(
            int requestId,
            int providerId,
            ServiceRequest.Status status,
            String responseMessage) throws SQLException {

        String sql = """
            UPDATE service_requests sr
            JOIN listings l ON sr.listing_id = l.listing_id
            SET sr.status = ?,
                sr.provider_response = ?
            WHERE sr.request_id = ?
              AND l.provider_id = ?
              AND sr.status IN ('PENDING', 'ACCEPTED')
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, status.name());
            statement.setString(2, responseMessage);
            statement.setInt(3, requestId);
            statement.setInt(4, providerId);

            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public boolean cancelByCustomer(
            int requestId, int customerId) throws SQLException {

        String sql = """
            UPDATE service_requests
            SET status = 'CANCELLED'
            WHERE request_id = ?
              AND customer_id = ?
              AND status = 'PENDING'
            """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, requestId);
            statement.setInt(2, customerId);

            return statement.executeUpdate() > 0;
        }
    }
}
