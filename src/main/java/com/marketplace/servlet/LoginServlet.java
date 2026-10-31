package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.marketplace.model.User;
import com.marketplace.service.AuthService;

/**
 * Displays the login form and authenticates users.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession();

        if (session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());
        }

        showForm(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);

        String submittedToken = request.getParameter("csrfToken");
        Object expectedToken = session == null
                ? null : session.getAttribute("csrfToken");

        if (submittedToken == null
                || !submittedToken.equals(expectedToken)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Your form expired. Reload the login page.");
            return;
        }

        try {
            Optional<User> result = authService.login(
                    request.getParameter("username"),
                    request.getParameter("password"));

            if (result.isEmpty()) {
                request.setAttribute(
                        "error", "Invalid username or password.");
                showForm(request, response);
                return;
            }

            // Replace the previous session after successful login.
            session.invalidate();

            HttpSession newSession = request.getSession(true);
            newSession.setMaxInactiveInterval(30 * 60);
            newSession.setAttribute("user", result.get());
            newSession.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());

            response.sendRedirect(
                    request.getContextPath() + "/");

        } catch (SQLException e) {
            getServletContext().log("Login failed", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            request.setAttribute(
                    "error",
                    "Login is temporarily unavailable. "
                    + "Please try again later.");

            showForm(request, response);
        }
    }

    private void showForm(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/login.jsp")
                .forward(request, response);
    }
}