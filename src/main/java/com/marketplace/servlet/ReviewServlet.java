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

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final JdbcReviewDAO reviewDAO = new JdbcReviewDAO();

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

    private void createReview(HttpServletRequest request,
                              HttpServletResponse response,
                              int listingId, User user)
            throws SQLException, IOException {
        int requestId = positiveInt(request.getParameter("requestId"));
        ReviewFields fields = validatedFields(request);

        ServiceReview review = new ServiceReview(0, requestId, listingId,
                user.getUserId(), fields.title(), fields.description(),
                fields.rating());

        if (!reviewDAO.create(review)) {
            redirectWithError(request, response, listingId, "ineligible");
            return;
        }
        redirectWithSuccess(request, response, listingId, "created");
    }

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

    private static int positiveInt(String value) {
        int parsed = Integer.parseInt(value);
        if (parsed <= 0) throw new NumberFormatException("Expected positive ID");
        return parsed;
    }

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

    private record ReviewFields(String title, String description, int rating) { }

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
