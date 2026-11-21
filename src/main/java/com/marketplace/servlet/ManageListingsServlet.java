
package com.marketplace.servlet;

import com.marketplace.dao.ManageListingDAO;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

/**
 * Displays and processes changes to listings owned by the user.
 */
@WebServlet("/account/listings")
public class ManageListingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ManageListingDAO dao = new ManageListingDAO();

    private static final Set<String> PRICE_TYPES =
            Set.of("FIXED", "HOURLY", "NEGOTIABLE");

    /**
     * Returns the user.
     * @param request the HTTP request
     * @return the current user
     */
    private User getUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) return null;

        Object user = session.getAttribute("user");
        return user instanceof User ? (User) user : null;
    }

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

        response.setHeader("Cache-Control", "no-store");

        User user = getUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession();

        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());
        }

        try {
            String editId = request.getParameter("edit");

            if (editId != null) {
                int id = parseId(editId);

                ManageListingDAO.Listing listing =
                        dao.findOwned(id, user.getUserId());

                if (listing == null) {
                    response.sendError(404, "Listing not found.");
                    return;
                }

                request.setAttribute("editing", listing);
            } else {
                request.setAttribute(
                        "listings", dao.findByOwner(user.getUserId()));
            }

            showPage(request, response);

        } catch (IllegalArgumentException e) {
            response.sendRedirect(
                    request.getContextPath() + "/account/listings");

        } catch (SQLException e) {
            throw new ServletException(
                    "Could not load your listings.", e);
        }
    }

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
        response.setHeader("Cache-Control", "no-store");

        User user = getUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession(false);

        String csrf = request.getParameter("csrfToken");
        Object expected = session.getAttribute("csrfToken");

        if (!(expected instanceof String)
                || csrf == null || !csrf.equals(expected)) {

            response.sendError(403, "Invalid form token.");
            return;
        }

        int listingId;

        try {
            listingId = parseId(request.getParameter("listingId"));
        } catch (IllegalArgumentException e) {
            response.sendError(400, "Invalid listing ID.");
            return;
        }

        try {
            ManageListingDAO.Listing existing =
                    dao.findOwned(listingId, user.getUserId());

            if (existing == null) {
                response.sendError(404, "Listing not found.");
                return;
            }

            String action = request.getParameter("action");

            if ("deactivate".equals(action)
                    || "activate".equals(action)) {

                boolean activate = "activate".equals(action);

                dao.setActive(listingId, user.getUserId(), activate);

                redirect(request, response,
                        activate ? "activated" : "removed");
                return;
            }

            if (!"update".equals(action)) {
                response.sendError(400, "Invalid action.");
                return;
            }

            try {
                String title = required(
                        request.getParameter("title"), 150);

                String description = required(
                        request.getParameter("description"), 10000);

                String category = required(
                        request.getParameter("category"), 100);

                String priceType =
                        request.getParameter("priceType");

                if (!PRICE_TYPES.contains(priceType)) {
                    throw new IllegalArgumentException(
                            "Select a valid price type.");
                }

                String priceText = request.getParameter("price");
                BigDecimal price = null;

                if (priceText != null && !priceText.isBlank()) {
                    String cleaned = priceText.trim();

                    if (!cleaned.matches(
                            "[0-9]{1,8}(\\.[0-9]{1,2})?")) {
                        throw new IllegalArgumentException(
                                "Enter a valid price.");
                    }

                    price = new BigDecimal(cleaned);
                } else if (!"NEGOTIABLE".equals(priceType)) {
                    throw new IllegalArgumentException(
                            "A price is required for fixed or hourly services.");
                }

                dao.update(listingId, user.getUserId(),
                        title, description, category, price, priceType);

                redirect(request, response, "updated");

            } catch (IllegalArgumentException e) {
                request.setAttribute("error", e.getMessage());

                // Retain submitted values after validation errors.
                ManageListingDAO.Listing submitted =
                        new ManageListingDAO.Listing();

                submitted.id = listingId;
                submitted.title = request.getParameter("title");
                submitted.description = request.getParameter("description");
                submitted.category = request.getParameter("category");
                submitted.priceType = request.getParameter("priceType");
                submitted.status = existing.status;

                String priceText = request.getParameter("price");

                try {
                    if (priceText != null && !priceText.isBlank()) {
                        submitted.price = new BigDecimal(priceText);
                    }
                } catch (NumberFormatException ignored) {
                    // Invalid price is displayed in the error message.
                }

                request.setAttribute("editing", submitted);
                showPage(request, response);
            }

        } catch (SQLException e) {
            throw new ServletException(
                    "Could not update the listing.", e);
        }
    }

    /**
     * Parses a numeric identifier from an HTTP parameter.
     * @param value the value
     * @return the parsed identifier
     */
    private int parseId(String value) {
        try {
            int id = Integer.parseInt(value);
            if (id > 0) return id;
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("Invalid ID");
    }

    /**
     * Validates required text and its maximum length.
     * @param value the value
     * @param maxLength the max length
     * @return the result of the operation
     */
    private String required(String value, int maxLength) {
        if (value == null || value.isBlank()
                || value.trim().length() > maxLength) {
            throw new IllegalArgumentException(
                    "Complete all required fields within their length limits.");
        }

        return value.trim();
    }

    /**
     * Redirects the browser to the appropriate application route.
     * @param request the HTTP request
     * @param response the response
     * @param result the result
     * @throws IOException if an I/O operation fails
     */
    private void redirect(HttpServletRequest request,
                          HttpServletResponse response,
                          String result) throws IOException {

        response.sendRedirect(
                request.getContextPath()
                        + "/account/listings?result=" + result);
    }

    /**
     * Renders the JSP page with the prepared request attributes.
     * @param request the HTTP request
     * @param response the response
     * @throws ServletException if the servlet cannot complete the request
     * @throws IOException if an I/O operation fails
     */
    private void showPage(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                        "/WEB-INF/views/manage-listings.jsp")
                .forward(request, response);
    }
}
