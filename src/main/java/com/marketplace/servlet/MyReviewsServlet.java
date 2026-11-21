package com.marketplace.servlet;

import com.marketplace.dao.JdbcReviewDAO;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

/** Displays reviews belonging to the signed-in customer (PR0019). */
/**
 * Displays the logged-in user’s reviews.
 */
@WebServlet("/account/reviews")
public class MyReviewsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final JdbcReviewDAO reviewDAO = new JdbcReviewDAO();

    /**
     * Handles HTTP GET requests for this servlet.
     * @param request the HTTP request
     * @param response the response
     * @throws ServletException if the servlet cannot complete the request
     * @throws IOException if an I/O operation fails
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("user") instanceof User user)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute("csrfToken", UUID.randomUUID().toString());
        }

        try {
            request.setAttribute("myReviews", reviewDAO.findByCustomerId(user.getUserId()));
            request.getRequestDispatcher("/WEB-INF/views/my-reviews.jsp")
                    .forward(request, response);
        } catch (SQLException e) {
            throw new ServletException("Unable to load your reviews.", e);
        }
    }
}
