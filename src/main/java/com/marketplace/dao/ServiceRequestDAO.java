package com.marketplace.dao;

import java.sql.SQLException;
import java.util.List;

import com.marketplace.model.ServiceRequest;

public interface ServiceRequestDAO {


    int create(ServiceRequest request) throws SQLException;

    ServiceRequest findById(int requestId) throws SQLException;

    List<ServiceRequest> findByCustomerId(int customerId)
            throws SQLException;
            

    List<ServiceRequest> findByProviderId(int providerId)
            throws SQLException;

    boolean updateStatusByProvider(
            int requestId,
            int providerId,
            ServiceRequest.Status status,
            String responseMessage) throws SQLException;

    boolean cancelByCustomer(
            int requestId,
            int customerId) throws SQLException;
}
