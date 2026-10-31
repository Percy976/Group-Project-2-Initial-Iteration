
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

        // User must be logged in.
        if (session == null ||
                !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        // Validate CSRF token.
        String submittedCsrf = request.getParameter("csrfToken");
        Object expectedCsrf = session.getAttribute("csrfToken");

        if (submittedCsrf == null ||
                !submittedCsrf.equals(expectedCsrf)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or expired form."
            );
            return;
        }

        User user = (User) session.getAttribute("user");

        // Validate the listing ID first.
        int listingId;

        try {
            listingId = Integer.parseInt(
                    request.getParameter("listingId"));

            if (listingId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(
                    request.getContextPath() + "/listings");
            return;
        }

        try {
            int requestId = Integer.parseInt(
                    request.getParameter("requestId"));

            int rating = Integer.parseInt(
                    request.getParameter("rating"));

            String title = request.getParameter("title");
            String description = request.getParameter("description");

            // Check required fields and valid values.
            if (requestId <= 0 ||
                    rating < 1 || rating > 5 ||
                    title == null || title.isBlank() ||
                    title.length() > 150 ||
                    description == null || description.isBlank()) {

                redirectWithError(
                        request, response, listingId, "invalid");
                return;
            }

            ServiceReview review = new ServiceReview(
                    0,
                    requestId,
                    listingId,
                    user.getUserId(),
                    title.trim(),
                    description.trim(),
                    rating
            );

            boolean created = reviewDAO.create(review);

            if (!created) {
                redirectWithError(
                        request, response, listingId, "ineligible");
                return;
            }

            // Review successfully created.
            response.sendRedirect(
                    request.getContextPath()
                            + "/listing?id=" + listingId
                            + "&reviewSuccess=1"
            );

        } catch (NumberFormatException e) {

            redirectWithError(
                    request, response, listingId, "invalid");

        } catch (SQLException e) {

            // MySQL error 1062: duplicate unique-key value.
            if (e.getErrorCode() == 1062) {
                redirectWithError(
                        request, response, listingId, "duplicate");
                return;
            }

            // Unexpected database errors are genuine server errors.
            throw new ServletException(
                    "Failed to submit review.", e);
        }
    }

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            int listingId,
            String errorCode) throws IOException {

        String encodedError = URLEncoder.encode(
                errorCode, StandardCharsets.UTF_8);

        response.sendRedirect(
                request.getContextPath()
                        + "/listing?id=" + listingId
                        + "&reviewError=" + encodedError
        );
    }
}
