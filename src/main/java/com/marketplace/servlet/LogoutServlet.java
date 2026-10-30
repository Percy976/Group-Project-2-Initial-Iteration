package com.marketplace.servlet;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Ends the authenticated session.
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {

        response.setHeader("Cache-Control", "no-store");

        HttpSession session = request.getSession(false);

        String submittedToken = request.getParameter("csrfToken");
        Object expectedToken = session == null
                ? null : session.getAttribute("csrfToken");

        if (submittedToken == null
                || !submittedToken.equals(expectedToken)) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Your session expired. Reload the account page.");
            return;
        }

        session.invalidate();

        response.sendRedirect(
                request.getContextPath() + "/login?loggedOut=1");
    }
}