
package com.marketplace.servlet;

import com.marketplace.dao.JdbcUserDAO;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/account/update")
public class UpdateProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final JdbcUserDAO userDAO = new JdbcUserDAO();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");

        // 1. Verify the user is logged in.
        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("user") instanceof User)) {

            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // 2. Verify the CSRF token.
        String submittedToken = request.getParameter("csrfToken");
        Object expectedToken = session.getAttribute("csrfToken");

        if (submittedToken == null
                || !submittedToken.equals(expectedToken)) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or expired form."
            );
            return;
        }

        // 3. Read the submitted information.
        String firstName = trimmed(request.getParameter("firstName"));
        String lastName = trimmed(request.getParameter("lastName"));
        String email = trimmed(request.getParameter("email"));
        String phone = trimmed(request.getParameter("phone"));
        String bio = trimmed(request.getParameter("bio"));

        // 4. Validate the submitted values.
        if (firstName.isEmpty() || firstName.length() > 100
                || lastName.isEmpty() || lastName.length() > 100
                || email.isEmpty() || email.length() > 255
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
                || phone.length() > 30
                || bio.length() > 2000) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid profile information."
            );
            return;
        }

        User currentUser = (User) session.getAttribute("user");

        try {
            // 5. Check whether another account uses this email.
            Optional<User> existingUser = userDAO.findByEmail(email);

            if (existingUser.isPresent()
                    && existingUser.get().getUserId()
                    != currentUser.getUserId()) {

                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Email address is already in use."
                );
                return;
            }

            // 6. Load a fresh copy of the account.
            Optional<User> result =
                    userDAO.findById(currentUser.getUserId());

            if (result.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Account not found."
                );
                return;
            }

            User updatedUser = result.get();

            updatedUser.setFirstName(firstName);
            updatedUser.setLastName(lastName);
            updatedUser.setEmail(email);
            updatedUser.setPhoneNumber(
                    phone.isEmpty() ? null : phone
            );
            updatedUser.setBio(bio);

            // 7. Save the changes to MySQL.
            if (!userDAO.updateProfile(updatedUser)) {
                throw new SQLException(
                        "Profile update did not affect a user record."
                );
            }

            // 8. Refresh the user stored in the session.
            session.setAttribute("user", updatedUser);

            // 9. Redirect back to the account page.
            response.sendRedirect(
                    request.getContextPath() + "/account?updated=1"
            );

        } catch (SQLException e) {
            getServletContext().log("Profile update failed", e);

            // MySQL duplicate-key error (e.g. concurrent email change).
            if (e.getErrorCode() == 1062) {
                response.sendError(
                        HttpServletResponse.SC_CONFLICT,
                        "Email address is already in use."
                );
                return;
            }

            throw new ServletException(
                    "Unable to update account information.", e
            );
        }
    }

    private String trimmed(String value) {
        return value == null ? "" : value.trim();
    }
}
