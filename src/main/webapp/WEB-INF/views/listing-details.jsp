<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.ServiceListing" %>
<%@ page import="com.marketplace.model.ServiceReview" %>
<%@ page import="java.util.List" %>

<%!
    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }
%>

<%
    ServiceListing listing =
            (ServiceListing) request.getAttribute("listing");

    List<ServiceReview> reviews =
            (List<ServiceReview>) request.getAttribute("reviews");

    Double averageRating =
            (Double) request.getAttribute("averageRating");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>
        <%= escapeHtml(listing.getServiceName()) %>
        - Local Services Marketplace
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/listing-details.css">
</head>

<body>

<header class="navbar">
    <div class="container nav-content">

        <a href="${pageContext.request.contextPath}/"
           class="brand">
            Local Services
        </a>


        <nav>
            <% if (session.getAttribute("user") != null) { %>

                <a href="${pageContext.request.contextPath}/account">
                    My Account
                </a>

                <form method="post"
                      action="${pageContext.request.contextPath}/logout">

                    <input type="hidden"
                           name="csrfToken"
                           value="<%= escapeHtml((String) session.getAttribute("csrfToken")) %>">

                    <button type="submit" class="btn btn-outline">
                        Log Out
                    </button>
                </form>

            <% } else { %>

                <a href="${pageContext.request.contextPath}/login">
                    Log In
                </a>

                <a href="${pageContext.request.contextPath}/register"
                   class="btn btn-outline">
                    Sign Up
                </a>

            <% } %>
        </nav>


    </div>
</header>

<main class="container">

    <section class="listing-details">

        <p>
            <a href="${pageContext.request.contextPath}/">
                &larr; Back to Services
            </a>
        </p>

        <h1><%= escapeHtml(listing.getServiceName()) %></h1>

        <p class="provider">
            Provided by
            <strong><%= escapeHtml(listing.getProviderName()) %></strong>
        </p>

        <h2>About This Service</h2>

        <p class="description">
            <%= escapeHtml(listing.getDescription()) %>
        </p>

        <h2>Pricing</h2>

        <p>
            <% if (listing.getPrice() != null) { %>
                $<%= listing.getPrice() %>
            <% } else { %>
                Contact for pricing
            <% } %>
        </p>

        <% if (session.getAttribute("user") == null) { %>

            <a href="${pageContext.request.contextPath}/login"
               class="btn btn-primary">
                Request Service
            </a>

        <% } else { %>

            <button type="button" class="btn btn-primary" disabled>
                Request Service
            </button>

        <% } %>

    </section>


    <section class="reviews">

        <h2>Customer Reviews</h2>

        <% if (averageRating != null) { %>

            <p>
                <strong>Average Rating:</strong>
                <%= String.format("%.1f", averageRating) %> / 5
            </p>

        <% } else { %>

            <p>No ratings yet.</p>

        <% } %>


        <% if (reviews != null && !reviews.isEmpty()) { %>

            <% for (ServiceReview review : reviews) { %>

                <div class="review">

                    <h3>
                        <%= escapeHtml(review.getTitle()) %>
                    </h3>

                    <p>
                        <strong>Rating:</strong>
                        <%= review.getRating() %> / 5
                    </p>

                    <p>
                        <%= escapeHtml(review.getDescription()) %>
                    </p>

                </div>

            <% } %>

        <% } else { %>

            <p>There are no reviews for this service yet.</p>

        <% } %>

    </section>

    <% if (session.getAttribute("user") instanceof com.marketplace.model.User
            && ((com.marketplace.model.User) session.getAttribute("user")).getUserId()
                    != listing.getProviderId()) { %>

    <section class="review-form">

        <h2>Write a Review</h2>


        <%
            String reviewError = request.getParameter("reviewError");
        %>

        <% if ("invalid".equals(reviewError)) { %>

            <p class="message error" role="alert">
                Please provide valid review information.
                All required fields must be completed.
            </p>

        <% } else if ("ineligible".equals(reviewError)) { %>

            <p class="message error" role="alert">
                Your review could not be submitted.
                Make sure the service request belongs to you,
                matches this listing, and has been completed.
            </p>

        <% } else if ("duplicate".equals(reviewError)) { %>

            <p class="message error" role="alert">
                You have already submitted a review for
                this service request.
            </p>

        <% } %>

        <% if ("1".equals(request.getParameter("reviewSuccess"))) { %>

            <p class="message success" role="status">
                Your review was submitted successfully.
            </p>

        <% } %>


        <form action="${pageContext.request.contextPath}/review"
              method="post">

            <input type="hidden"
                   name="csrfToken"
                   value="<%= escapeHtml((String) session.getAttribute("csrfToken")) %>">

            <input type="hidden"
                   name="listingId"
                   value="<%= listing.getListingId() %>">

            <label for="requestId">Completed Request ID</label>
            <input type="number"
                   id="requestId"
                   name="requestId"
                   min="1"
                   required>

            <label for="title">Review Title</label>
            <input type="text"
                   id="title"
                   name="title"
                   maxlength="150"
                   required>

            <label for="description">Review Description</label>
            <textarea id="description"
                      name="description"
                      rows="5"
                      required></textarea>

            <label for="rating">Rating</label>
            <select id="rating" name="rating" required>
                <option value="">Choose a rating</option>
                <option value="5">5 - Excellent</option>
                <option value="4">4 - Good</option>
                <option value="3">3 - Average</option>
                <option value="2">2 - Poor</option>
                <option value="1">1 - Terrible</option>
            </select>

            <button type="submit" class="btn btn-primary">
                Submit Review
            </button>

        </form>

    </section>

<% } %>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>