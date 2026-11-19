package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.marketplace.model.User;
import com.marketplace.service.ServiceRequestService;

@WebServlet("/provider-requests")
public class ProviderRequestsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ServiceRequestService requestService =
            new ServiceRequestService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(
                    request.getContextPath() + "/login");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            request.setAttribute(
                    "serviceRequests",
                    requestService.findByProviderId(user.getUserId()));

            request.getRequestDispatcher(
                    "/WEB-INF/views/provider-requests.jsp")
                    .forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Failed to retrieve provider service requests.", e);
        }
    }
}
