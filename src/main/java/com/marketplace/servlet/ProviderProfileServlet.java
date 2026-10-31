
package com.marketplace.servlet;

import com.marketplace.dao.PublicProviderDAO;
import com.marketplace.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/provider")
public class ProviderProfileServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final PublicProviderDAO providerDAO =
            new PublicProviderDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");

        int providerId;

        try {
            providerId = Integer.parseInt(
                    request.getParameter("id"));

            if (providerId <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid provider ID.");
            return;
        }

        try {
            Optional<User> provider =
                    providerDAO.findProvider(providerId);

            if (provider.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Provider not found.");
                return;
            }

            request.setAttribute("provider", provider.get());

            request.setAttribute(
                    "providerListings",
                    providerDAO.findActiveListings(providerId));

            request.getRequestDispatcher(
                    "/WEB-INF/views/provider-profile.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Failed to retrieve provider profile.", e);
        }
    }
}
