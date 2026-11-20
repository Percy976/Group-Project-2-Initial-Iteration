
package com.marketplace.servlet;

import com.marketplace.service.PasswordResetService;
import com.marketplace.service.EmailService;
import jakarta.mail.MessagingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PasswordResetService resetService =
            new PasswordResetService();
    private final EmailService emailService = new EmailService();

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
                    "Invalid or expired form."
            );
            return;
        }

        String email = request.getParameter("email");

        if (email == null || email.isBlank()
                || email.length() > 255) {

            request.setAttribute(
                    "error", "Please enter a valid email address.");
            showForm(request, response);
            return;
        }

        try {
            // Only known accounts generate tokens; the response is identical either way.
            java.util.Optional<String> token = resetService.requestReset(email.trim());
            if (token.isPresent()) {
                try {
                    emailService.sendPasswordReset(email.trim(), token.get());
                } catch (MessagingException | IllegalStateException e) {
                    // Do not reveal account existence or reset tokens in the response/logs.
                    getServletContext().log("Password reset email delivery failed", e);
                }
            }

            // Do not reveal whether the email is registered.
            request.setAttribute("submitted", true);

            showForm(request, response);

        } catch (SQLException e) {
            getServletContext().log(
                    "Password reset request failed", e);

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            request.setAttribute(
                    "error",
                    "Password recovery is temporarily unavailable."
            );

            showForm(request, response);
        }
    }

    private void showForm(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/forgot-password.jsp"
        ).forward(request, response);
    }
}
