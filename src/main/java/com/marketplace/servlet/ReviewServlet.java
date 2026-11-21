package com.marketplace.servlet;

import com.marketplace.dao.JdbcReviewDAO;
import com.marketplace.model.ServiceReview;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

/**
 * Handles creating, updating, and deleting customer reviews.
 */
@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final JdbcReviewDAO reviewDAO = new JdbcReviewDAO();

    /**
     * Handles HTTP POST requests for this servlet.
     * @param request the HTTP request
     * @param response the response
     * @throws ServletException if the servlet cannot complete the request
     * @throws IOException if an I/O operation fails
     */
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // All review changes must come from a valid session form.
        String submittedCsrf = request.getParameter("csrfToken");
        Object expectedCsrf = session.getAttribute("csrfToken");
        if (!(expectedCsrf instanceof String)
                || submittedCsrf == null
                || !submittedCsrf.equals(expectedCsrf)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or expired form.");
            return;
        }

        int listingId;
        try {
            listingId = positiveInt(request.getParameter("listingId"));
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/listings");
            return;
        }

        User user = (User) session.getAttribute("user");
        String action = request.getParameter("action");
        if (action == null || action.isBlank()) {
            action = "create";  // Preserve the existing review submission form.
        }

        try {
            switch (action) {
                case "create" -> createReview(request, response, listingId, user);
                case "update" -> updateReview(request, response, listingId, user);
                case "delete" -> deleteReview(request, response, listingId, user);
                default -> redirectWithError(request, response, listingId, "invalid");
            }
        } catch (IllegalArgumentException e) {
            redirectWithError(request, response, listingId, "invalid");
        } catch (SQLException e) {
            // Duplicate review for the same completed request.
            if ("create".equals(action) && e.getErrorCode() == 1062) {
                redirectWithError(request, response, listingId, "duplicate");
                return;
            }
            throw new ServletException("Failed to manage review.", e);
        }
    }

    /**
     * Creates a review for an eligible completed service request.
     * @param request the HTTP request
     * @param response the response
     * @param listingId the listing id
     * @param user the user
     * @throws SQLException if the database operation fails
     * @throws IOException if an I/O operation fails
     */
    private void createReview(HttpServletRequest request,
                              HttpServletResponse response,
                              int listingId, User user)
            throws SQLException, IOException {
        ReviewFields fields = validatedFields(request);

        if (!reviewDAO.createForEligibleRequest(listingId, user.getUserId(),
                fields.title(), fields.description(), fields.rating())) {
            redirectWithError(request, response, listingId, "ineligible");
            return;
        }
        redirectWithSuccess(request, response, listingId, "created");
    }

    /**
     * Updates a review only if it belongs to the signed-in user.
     * @param request the HTTP request
     * @param response the response
     * @param listingId the listing id
     * @param user the user
     * @throws SQLException if the database operation fails
     * @throws IOException if an I/O operation fails
     */
    private void updateReview(HttpServletRequest request,
                              HttpServletResponse response,
                              int listingId, User user)
            throws SQLException, IOException {
        int reviewId = positiveInt(request.getParameter("reviewId"));
        ReviewFields fields = validatedFields(request);

        if (!reviewDAO.update(reviewId, listingId, user.getUserId(),
                fields.title(), fields.description(), fields.rating())) {
            redirectWithError(request, response, listingId, "notowner");
            return;
        }
        redirectWithSuccess(request, response, listingId, "updated");
    }

    /**
     * Deletes a review only if it belongs to the signed-in user.
     * @param request the HTTP request
     * @param response the response
     * @param listingId the listing id
     * @param user the user
     * @throws SQLException if the database operation fails
     * @throws IOException if an I/O operation fails
     */
    private void deleteReview(HttpServletRequest request,
                              HttpServletResponse response,
                              int listingId, User user)
            throws SQLException, IOException {
        int reviewId = positiveInt(request.getParameter("reviewId"));
        if (!reviewDAO.delete(reviewId, listingId, user.getUserId())) {
            redirectWithError(request, response, listingId, "notowner");
            return;
        }
        redirectWithSuccess(request, response, listingId, "deleted");
    }

    /**
     * Parses a positive numeric identifier from an HTTP parameter.
     * @param value the value
     * @return the parsed identifier
     */
    private static int positiveInt(String value) {
        int parsed = Integer.parseInt(value);
        if (parsed <= 0) throw new NumberFormatException("Expected positive ID");
        return parsed;
    }

    /**
     * Validates review title, description, and star rating.
     * @param request the HTTP request
     * @return the validated review fields
     */
    private static ReviewFields validatedFields(HttpServletRequest request) {
        int rating = Integer.parseInt(request.getParameter("rating"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        if (rating < 1 || rating > 5 || title == null
                || description == null) {
            throw new IllegalArgumentException("Invalid review fields");
        }
        title = title.trim();
        description = description.trim();
        if (title.isEmpty() || title.length() > 150 || description.isEmpty()) {
            throw new IllegalArgumentException("Invalid review length");
        }
        return new ReviewFields(title, description, rating);
    }

    /**
     * Contains review form fields validated before persistence.
     */
    private record ReviewFields(String title, String description, int rating) { }

    /**
     * Redirects the customer with a review error indicator.
     * @param request the HTTP request
     * @param response the response
     * @param listingId the listing id
     * @param errorCode the error code
     * @throws IOException if an I/O operation fails
     */
    private void redirectWithError(HttpServletRequest request,
                                   HttpServletResponse response,
                                   int listingId, String errorCode) throws IOException {
        if ("my-reviews".equals(request.getParameter("returnTo"))) {
            response.sendRedirect(request.getContextPath() + "/account/reviews?reviewError="
                    + URLEncoder.encode(errorCode, StandardCharsets.UTF_8));
            return;
        }
        response.sendRedirect(request.getContextPath()
                + "/listing?id=" + listingId + "&reviewError="
                + URLEncoder.encode(errorCode, StandardCharsets.UTF_8)
                + "#reviews");
    }

    /**
     * Redirects the customer with a review success indicator.
     * @param request the HTTP request
     * @param response the response
     * @param listingId the listing id
     * @param result the result
     * @throws IOException if an I/O operation fails
     */
    private void redirectWithSuccess(HttpServletRequest request,
                                     HttpServletResponse response,
                                     int listingId, String result) throws IOException {
        if ("my-reviews".equals(request.getParameter("returnTo"))) {
            response.sendRedirect(request.getContextPath() + "/account/reviews?reviewSuccess="
                    + URLEncoder.encode(result, StandardCharsets.UTF_8));
            return;
        }
        response.sendRedirect(request.getContextPath()
                + "/listing?id=" + listingId + "&reviewSuccess="
                + URLEncoder.encode(result, StandardCharsets.UTF_8)
                + "#reviews");
    }
}
