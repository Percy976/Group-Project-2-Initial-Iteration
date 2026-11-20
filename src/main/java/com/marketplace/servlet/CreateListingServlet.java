package com.marketplace.servlet;

import com.marketplace.model.User;
import com.marketplace.service.ListingCreationService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/listings/create")
public class CreateListingServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ListingCreationService listingService =
            new ListingCreationService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");

        User user = getLoggedInUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        ensureCsrfToken(request.getSession());
        showForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        User user = getLoggedInUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);
        String submittedToken = request.getParameter("csrfToken");
        Object expectedToken = session.getAttribute("csrfToken");

        if (!(expectedToken instanceof String)
                || submittedToken == null
                || !submittedToken.equals(expectedToken)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid form token. Reload the form and try again.");
            return;
        }

        try {
            // Ownership comes from the authenticated user.
            int listingId = listingService.create(
                    user.getUserId(),
                    request.getParameter("title"),
                    request.getParameter("description"),
                    request.getParameter("category"),
                    request.getParameter("price"),
                    request.getParameter("priceType"));

            response.sendRedirect(
                    request.getContextPath()
                            + "/listing?id=" + listingId);

        } catch (IllegalArgumentException exception) {
            request.setAttribute("error", exception.getMessage());
            showForm(request, response);

        } catch (SQLException exception) {
            log("Could not create service listing.", exception);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            request.setAttribute(
                    "error",
                    "The listing could not be saved. Please try again.");

            showForm(request, response);
        }
    }

    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object value = session.getAttribute("user");
        return value instanceof User ? (User) value : null;
    }

    private void ensureCsrfToken(HttpSession session) {
        if (!(session.getAttribute("csrfToken") instanceof String)) {
            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());
        }
    }

    private void showForm(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                        "/WEB-INF/views/create-listing.jsp")
                .forward(request, response);
    }
}