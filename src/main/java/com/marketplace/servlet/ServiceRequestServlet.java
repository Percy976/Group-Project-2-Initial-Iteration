package com.marketplace.servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.marketplace.model.ServiceRequest;
import com.marketplace.model.User;
import com.marketplace.service.ServiceRequestService;


@WebServlet("/service-request")
public class ServiceRequestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final ServiceRequestService requestService =
            new ServiceRequestService();

    @Override
    protected void doPost(HttpServletRequest request,
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
            int listingId = Integer.parseInt(
                    request.getParameter("listingId"));

            String jobDetails = request.getParameter("jobDetails");

            if (listingId <= 0
                    || jobDetails == null
                    || jobDetails.isBlank()
                    || jobDetails.length() > 5000) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Please provide valid service request information.");
                return;
            }

            ServiceRequest serviceRequest = new ServiceRequest();
            serviceRequest.setListingId(listingId);
            serviceRequest.setCustomerId(user.getUserId());
            serviceRequest.setJobDetails(jobDetails.trim());

            int requestId = requestService.create(serviceRequest);

            if (requestId <= 0) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "The service request could not be created. Check that the listing is active and does not belong to you.");
                return;
            }

            response.sendRedirect(
                    request.getContextPath() + "/listing?id=" + listingId);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid listing information.");

        } catch (SQLException | IllegalArgumentException e) {

            throw new ServletException(
                    "Failed to submit service request.", e);
        }
    }
}
