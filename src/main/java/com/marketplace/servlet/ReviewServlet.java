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
import java.sql.SQLException;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final JdbcReviewDAO reviewDAO = new JdbcReviewDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            int requestId = Integer.parseInt(
                    request.getParameter("requestId"));

            int listingId = Integer.parseInt(
                    request.getParameter("listingId"));

            int rating = Integer.parseInt(
                    request.getParameter("rating"));

            String title = request.getParameter("title");
            String description = request.getParameter("description");

            if (requestId <= 0 || listingId <= 0
                    || rating < 1 || rating > 5
                    || title == null || title.isBlank()
                    || description == null || description.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Please provide valid review information."
                );
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
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "A review could not be submitted. Make sure your service request is completed and belongs to you."
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/listing?id=" + listingId
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid review information."
            );

        } catch (SQLException | IllegalArgumentException e) {

            throw new ServletException(
                    "Failed to submit review.", e
            );
        }
    }
}