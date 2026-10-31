package com.marketplace.servlet;

import com.marketplace.dao.JdbcListingDAO;
import com.marketplace.model.User;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Set;
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

    private static final Set<String> PRICE_TYPES =
            Set.of("FIXED", "HOURLY", "NEGOTIABLE");

    private final JdbcListingDAO listingDAO = new JdbcListingDAO();

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
            String title = requiredText(
                    request.getParameter("title"), "Title", 150);

            String description = requiredText(
                    request.getParameter("description"),
                    "Description", 10000);

            String category = requiredText(
                    request.getParameter("category"), "Category", 100);

            String priceType = text(request.getParameter("priceType"));

            if (!PRICE_TYPES.contains(priceType)) {
                throw new IllegalArgumentException(
                        "Select a valid price type.");
            }

            BigDecimal price = parsePrice(
                    request.getParameter("price"), priceType);

            // Ownership always comes from the authenticated session.
            int listingId = listingDAO.create(
                    user.getUserId(),
                    title,
                    description,
                    category,
                    price,
                    priceType);

            // Redirect after saving so refreshing cannot repeat the POST.
            response.sendRedirect(
                    request.getContextPath()
                            + "/listings/create?created=" + listingId);

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

    private static String text(String value) {
        return value == null ? "" : value.trim();
    }

    private static String requiredText(
            String value, String label, int maximumLength) {

        String cleaned = text(value);

        if (cleaned.isEmpty()) {
            throw new IllegalArgumentException(
                    label + " is required.");
        }

        if (cleaned.length() > maximumLength) {
            throw new IllegalArgumentException(
                    label + " must be "
                            + maximumLength + " characters or fewer.");
        }

        return cleaned;
    }

    private static BigDecimal parsePrice(
            String value, String priceType) {

        String cleaned = text(value);

        if (cleaned.isEmpty()) {
            if ("NEGOTIABLE".equals(priceType)) {
                return null;
            }

            throw new IllegalArgumentException(
                    "Enter a price for fixed or hourly services.");
        }

        if (!cleaned.matches("[0-9]{1,8}(\\.[0-9]{1,2})?")) {
            throw new IllegalArgumentException(
                    "Enter a valid price with up to two decimal places "
                            + "and a maximum of 99999999.99.");
        }

        return new BigDecimal(cleaned)
                .setScale(2, RoundingMode.UNNECESSARY);
    }
}