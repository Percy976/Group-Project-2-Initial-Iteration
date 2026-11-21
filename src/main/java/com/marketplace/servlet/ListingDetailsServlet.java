package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.marketplace.model.ServiceReview;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.marketplace.dao.JdbcListingDAO;
import com.marketplace.dao.JdbcReviewDAO;
import com.marketplace.model.ServiceListing;
import com.marketplace.model.User;
import javax.servlet.http.HttpSession;

/**
 * Displays listing details and reviews with eligibility information.
 */
@WebServlet("/listing")
public class ListingDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final JdbcListingDAO listingDAO = new JdbcListingDAO();
    private final JdbcReviewDAO reviewDAO = new JdbcReviewDAO();

    /**
     * Handles HTTP GET requests for this servlet.
     * @param request the HTTP request
     * @param response the response
     * @throws ServletException if the servlet cannot complete the request
     * @throws IOException if an I/O operation fails
     */
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
            ServiceListing listing = listingDAO.findById(listingId);

            if (listing == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Service listing not found."
                );
                return;
            }

            HttpSession session = request.getSession(false);
            User currentUser = session != null && session.getAttribute("user") instanceof User
                    ? (User) session.getAttribute("user") : null;
            JdbcReviewDAO.ReviewEligibility reviewEligibility = null;
            if (currentUser != null && currentUser.getUserId() != listing.getProviderId()) {
                reviewEligibility = reviewDAO.getReviewEligibility(listingId, currentUser.getUserId());
            }
            request.setAttribute("reviewEligibility", reviewEligibility);

            List<ServiceReview> reviews = reviewDAO.findByListingId(listingId);
            if (currentUser != null) {
                int currentUserId = currentUser.getUserId();
                // Put this customer's reviews first without changing the
                // existing newest-first order within either group.
                reviews.sort((left, right) -> Boolean.compare(
                        right.getCustomerId() == currentUserId,
                        left.getCustomerId() == currentUserId));
            }

            request.setAttribute("listing", listing);
            request.setAttribute("reviews", reviews);
            request.setAttribute("averageRating",
                    reviewDAO.findAverageRatingByListingId(listingId));

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
