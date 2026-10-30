
package com.marketplace.servlet;

import com.marketplace.dao.JdbcListingDAO;
import com.marketplace.model.ServiceListing;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/listings")
public class ListingsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 30;

    private final JdbcListingDAO listingDAO = new JdbcListingDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int page = 0;

        // Read the requested page number, if provided.
        String pageParameter = request.getParameter("page");

        if (pageParameter != null) {
            try {
                page = Math.max(0, Integer.parseInt(pageParameter));
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid page number."
                );
                return;
            }
        }

        // Calculate how many listings to skip.
        if (page > Integer.MAX_VALUE / PAGE_SIZE) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Page number is too large."
            );
            return;
        }

        int offset = page * PAGE_SIZE;

        try {
            List<ServiceListing> listings =
                    listingDAO.findActive(PAGE_SIZE, offset);

            request.setAttribute("listings", listings);

            // Tell the JSP whether this is the first page
            // or an additional batch.
            request.setAttribute("isBatch", page > 0);

            request.getRequestDispatcher(
                    "/WEB-INF/views/service-listings.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Failed to retrieve service listings.", e
            );
        }
    }
}
