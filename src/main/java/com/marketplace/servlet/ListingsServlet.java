
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

        String searchQuery = request.getParameter("q");

        if (searchQuery == null) {
            searchQuery = "";
        }

        searchQuery = searchQuery.trim();

        if (searchQuery.length() > 100) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Search query is too long."
            );
            return;
        }

        int page = 0;
        String pageParameter = request.getParameter("page");

        if (pageParameter != null) {
            try {
                page = Integer.parseInt(pageParameter);

                if (page < 0 || page > Integer.MAX_VALUE / PAGE_SIZE) {
                    response.sendError(
                            HttpServletResponse.SC_BAD_REQUEST,
                            "Invalid page number."
                    );
                    return;
                }

            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid page number."
                );
                return;
            }
        }

        int offset = page * PAGE_SIZE;

        try {
            List<ServiceListing> listings =
                    listingDAO.findActive(
                            PAGE_SIZE, offset, searchQuery);

            request.setAttribute("listings", listings);
            request.setAttribute("isBatch", page > 0);
            request.setAttribute("searchQuery", searchQuery);

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
