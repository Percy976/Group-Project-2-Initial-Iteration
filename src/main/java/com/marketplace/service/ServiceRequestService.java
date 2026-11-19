package com.marketplace.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.marketplace.dao.JdbcServiceRequestDAO;
import com.marketplace.dao.ServiceRequestDAO;
import com.marketplace.model.ServiceRequest;

public class ServiceRequestService {

    private final ServiceRequestDAO requestDAO;


    public ServiceRequestService() {
        this(new JdbcServiceRequestDAO());
    }

    public ServiceRequestService(ServiceRequestDAO requestDAO) {
        this.requestDAO = Objects.requireNonNull(requestDAO);
    }

    public int create(ServiceRequest request) throws SQLException {

        Objects.requireNonNull(request, "Request cannot be null.");

        if (request.getListingId() <= 0
                || request.getCustomerId() <= 0) {
            throw new IllegalArgumentException(
                    "Valid listing and customer IDs are required.");
        }

        if (request.getJobDetails() == null
                || request.getJobDetails().isBlank()) {
            throw new IllegalArgumentException(
                    "Request details are required.");
        }

        
        if (request.getJobDetails().length() > 5000) {
            throw new IllegalArgumentException(
                    "Request details cannot exceed 5000 characters.");
        }

        return requestDAO.create(request);
    }

    public ServiceRequest findById(int requestId) throws SQLException {

        if (requestId <= 0) {
            throw new IllegalArgumentException(
                    "A valid request ID is required.");
        }

        return requestDAO.findById(requestId);
    }

    public List<ServiceRequest> findByCustomerId(int customerId)
            throws SQLException {

        if (customerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid customer ID is required.");
        }

        return requestDAO.findByCustomerId(customerId);
    }

    public List<ServiceRequest> findByProviderId(int providerId)
            throws SQLException {

        if (providerId <= 0) {
            throw new IllegalArgumentException(
                    "A valid provider ID is required.");
        }

        return requestDAO.findByProviderId(providerId);
    }

    public boolean updateStatusByProvider(
            int requestId,
            int providerId,
            ServiceRequest.Status status,
            String responseMessage) throws SQLException {

        if (requestId <= 0 || providerId <= 0) {
            throw new IllegalArgumentException(
                    "Valid request and provider IDs are required.");
        }

        if (status != ServiceRequest.Status.ACCEPTED
                && status != ServiceRequest.Status.DECLINED
                && status != ServiceRequest.Status.COMPLETED) {
            throw new IllegalArgumentException(
                    "Invalid provider status update.");
        }

        if (responseMessage != null && responseMessage.length() > 2000) {
            throw new IllegalArgumentException(
                    "Response message cannot exceed 2000 characters.");
        }

        return requestDAO.updateStatusByProvider(
                requestId, providerId, status, responseMessage);
    }

    public boolean cancelByCustomer(
            int requestId, int customerId) throws SQLException {

        if (requestId <= 0 || customerId <= 0) {
            throw new IllegalArgumentException(
                    "Valid request and customer IDs are required.");
        }

        return requestDAO.cancelByCustomer(requestId, customerId);
    }
}
