
package com.marketplace.servlet;

import com.marketplace.dao.JdbcServiceRequestDAO;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;
import java.util.UUID;

@WebServlet("/requests")
public class ServiceRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final JdbcServiceRequestDAO dao =
            new JdbcServiceRequestDAO();

    private static final Set<String> ACTIONS =
            Set.of("accept", "decline", "complete", "cancel");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");

        User user = loggedInUser(request);

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        HttpSession session = request.getSession();

        if (!(session.getAttribute("csrfToken") instanceof String)) {
            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());
        }

        try {
            request.setAttribute(
                    "sentRequests",
                    dao.findByCustomer(user.getUserId()));

            request.setAttribute(
                    "receivedRequests",
                    dao.findByProvider(user.getUserId()));

            request.getRequestDispatcher(
                            "/WEB-INF/views/service-requests.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Could not load service requests.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        User user = loggedInUser(request);

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

            response.sendError(403, "Invalid form token.");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                createRequest(request, response, user);
                return;
            }

            if (!ACTIONS.contains(action)) {
                response.sendError(400, "Invalid action.");
                return;
            }

            int requestId = parseId(request.getParameter("requestId"));

            String providerResponse =
                    request.getParameter("providerResponse");

            if (providerResponse == null) {
                providerResponse = "";
            }

            providerResponse = providerResponse.trim();

            if (providerResponse.length() > 2000) {
                redirect(request, response, "invalid");
                return;
            }

            boolean updated = dao.updateStatus(
                    requestId,
                    user.getUserId(),
                    action,
                    providerResponse);

            redirect(request, response,
                    updated ? "updated" : "notallowed");

        } catch (IllegalArgumentException e) {
            redirect(request, response, "invalid");

        } catch (SQLException e) {
            throw new ServletException(
                    "Could not process service request.", e);
        }
    }

    private void createRequest(HttpServletRequest request,
                               HttpServletResponse response,
                               User user)
            throws IOException, SQLException {

        int listingId = parseId(request.getParameter("listingId"));

        String message = request.getParameter("message");

        if (message == null || message.isBlank()
                || message.trim().length() > 2000) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/listing?id=" + listingId
                            + "&requestError=invalid");
            return;
        }

        boolean created = dao.create(
                listingId, user.getUserId(), message.trim());

        if (created) {
            redirect(request, response, "created");
        } else {
            response.sendRedirect(
                    request.getContextPath()
                            + "/listing?id=" + listingId
                            + "&requestError=notallowed");
        }
    }

    private int parseId(String value) {
        try {
            int id = Integer.parseInt(value);

            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException ignored) {
        }

        throw new IllegalArgumentException("Invalid ID");
    }

    private User loggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object user = session.getAttribute("user");

        return user instanceof User ? (User) user : null;
    }

    private void redirect(HttpServletRequest request,
                          HttpServletResponse response,
                          String result) throws IOException {

        response.sendRedirect(
                request.getContextPath()
                        + "/requests?result=" + result);
    }
}
