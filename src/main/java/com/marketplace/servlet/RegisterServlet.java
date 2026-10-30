package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;
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
 * Displays and processes account registration.
 */
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession();
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
                    "Your form expired. Reload the registration page.");
            return;
        }

        String password = request.getParameter("password");
        String confirmation =
                request.getParameter("confirmPassword");

        if (password == null || !password.equals(confirmation)) {
            request.setAttribute("error", "Passwords do not match.");
            showForm(request, response);
            return;
        }

        User user = new User();
        user.setUsername(request.getParameter("username"));
        user.setEmail(request.getParameter("email"));
        user.setFirstName(request.getParameter("firstName"));
        user.setLastName(request.getParameter("lastName"));
        user.setPhoneNumber(request.getParameter("phoneNumber"));
        user.setBio("");

        try {
            authService.register(user, password);

            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());

            response.sendRedirect(
                    request.getContextPath() + "/login?registered=1");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            showForm(request, response);

        } catch (SQLException e) {
            getServletContext().log("Account registration failed", e);
            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.setAttribute(
                    "error",
                    "Registration is temporarily unavailable. "
                    + "Please try again later.");
            showForm(request, response);
        }
    }

    private void showForm(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/register.jsp")
                .forward(request, response);
    }
}