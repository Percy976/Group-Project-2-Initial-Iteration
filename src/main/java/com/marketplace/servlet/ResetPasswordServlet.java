
package com.marketplace.servlet;

import com.marketplace.service.PasswordResetService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PasswordResetService resetService =
            new PasswordResetService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        setSecurityHeaders(response);

        HttpSession session = request.getSession();

        if (session.getAttribute("csrfToken") == null) {
            session.setAttribute(
                    "csrfToken", UUID.randomUUID().toString());
        }

        String token = request.getParameter("token");

        try {
            if (resetService.validateToken(token).isEmpty()) {
                request.setAttribute("invalidToken", true);
            } else {
                request.setAttribute("token", token);
            }

            showPage(request, response);

        } catch (SQLException e) {
            getServletContext().log(
                    "Password reset token validation failed", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        setSecurityHeaders(response);

        HttpSession session = request.getSession(false);

        String submittedCsrf = request.getParameter("csrfToken");
        Object expectedCsrf = session == null
                ? null : session.getAttribute("csrfToken");

        if (submittedCsrf == null
                || !submittedCsrf.equals(expectedCsrf)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or expired form.");
            return;
        }

        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (token == null || token.isBlank()) {
            request.setAttribute("invalidToken", true);
            showPage(request, response);
            return;
        }

        if (password == null
                || password.length() < 8
                || password.length() > 128) {

            showError(request, response, token,
                    "Password must contain between 8 and 128 characters.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError(request, response, token,
                    "Passwords do not match.");
            return;
        }

        try {
            boolean updated =
                    resetService.resetPassword(token, password);

            if (!updated) {
                request.setAttribute("invalidToken", true);
                showPage(request, response);
                return;
            }

            // Redirect to login after a successful password reset.
            response.sendRedirect(
                    request.getContextPath() + "/login?reset=1");

        } catch (SQLException e) {
            getServletContext().log(
                    "Password reset failed", e);

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        } catch (IllegalArgumentException e) {
            showError(request, response, token, e.getMessage());
        }
    }

    private void showError(HttpServletRequest request,
                           HttpServletResponse response,
                           String token,
                           String message)
            throws ServletException, IOException {

        request.setAttribute("token", token);
        request.setAttribute("error", message);
        showPage(request, response);
    }

    private void showPage(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/reset-password.jsp"
        ).forward(request, response);
    }

    private void setSecurityHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Referrer-Policy", "no-referrer");
    }
}
