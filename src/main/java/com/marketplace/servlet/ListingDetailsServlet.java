package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marketplace.model.ServiceListing;
import com.marketplace.service.ListingService;
import com.marketplace.service.ReviewService;

@WebServlet("/listing")
public class ListingDetailsServlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    private final ListingService listingService = new ListingService();
    private final ReviewService reviewService = new ReviewService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParameter = request.getParameter("id");
        int listingId;

        try {
            listingId = Integer.parseInt(idParameter);

            if (listingId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid listing ID."
                );
                return;
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid listing ID."
            );
            return;
        }

        
        try {
            ServiceListing listing = listingService.findById(listingId);

            if (listing == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Service listing not found."
                );
                return;
            }

            request.setAttribute("listing", listing);
            request.setAttribute("reviews",
                    reviewService.findByListingId(listingId));
            request.setAttribute("averageRating",
                    reviewService.findAverageRatingByListingId(listingId));

            request.getRequestDispatcher(
                    "/WEB-INF/views/listing-details.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Failed to retrieve listing or reviews.", e
            );
        }
    }
}
