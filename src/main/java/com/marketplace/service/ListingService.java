package com.marketplace.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import com.marketplace.dao.JdbcListingDAO;
import com.marketplace.model.ServiceListing;

public class ListingService {

    private final JdbcListingDAO listingDAO;

    public ListingService() {
        this(new JdbcListingDAO());
    }

    public ListingService(JdbcListingDAO listingDAO) {
        this.listingDAO = Objects.requireNonNull(listingDAO);
    }


    public List<ServiceListing> findActive(
            int limit, int offset, String searchQuery)
            throws SQLException {

        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(
                    "Limit must be between 1 and 100.");
        }

        if (offset < 0) {
            throw new IllegalArgumentException(
                    "Offset cannot be negative!");
        }

        return listingDAO.findActive(limit, offset, searchQuery);
    }



    public ServiceListing findById(int listingId)
            throws SQLException {

        if (listingId <= 0) {
            throw new IllegalArgumentException(
                    "A valid listing ID is required!");
        }

        return listingDAO.findById(listingId);
    }
}
