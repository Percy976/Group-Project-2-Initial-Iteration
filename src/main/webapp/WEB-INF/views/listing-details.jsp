
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.ServiceListing" %>
<%@ page import="com.marketplace.model.ServiceReview" %>
<%@ page import="com.marketplace.model.User" %>
<%@ page import="com.marketplace.dao.JdbcReviewDAO" %>
<%@ page import="java.util.List" %>

<%!
    private String escapeHtml(Object value) {
        if (value == null) return "";

        return value.toString()
                .replace("&", "&amp;")
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

    User currentUser = session.getAttribute("user") instanceof User
            ? (User) session.getAttribute("user")
            : null;

    boolean isOwner = currentUser != null
            && currentUser.getUserId() == listing.getProviderId();

    JdbcReviewDAO.ReviewEligibility reviewEligibility =
            (JdbcReviewDAO.ReviewEligibility) request.getAttribute("reviewEligibility");

    String csrf = escapeHtml(session.getAttribute("csrfToken"));
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

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

        <a href="${pageContext.request.contextPath}/" class="brand">
            Local Services
        </a>

        <nav>
            <% if (currentUser != null) { %>

                <a href="${pageContext.request.contextPath}/account">
                    My Account
                </a>

                <form method="post"
                      action="${pageContext.request.contextPath}/logout">

                    <input type="hidden"
                           name="csrfToken"
                           value="<%= csrf %>">

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
            <a href="${pageContext.request.contextPath}/provider?id=<%= listing.getProviderId() %>">
                <strong>
                    <%= escapeHtml(listing.getProviderName()) %>
                </strong>
            </a>
        </p>

        <p class="listing-category">
            <strong>Category:</strong>
            <%= escapeHtml(listing.getCategory()) %>
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

        <% if (!isOwner) { %>

            <section id="request-service"
                     style="margin-top: 30px;">

                <h2>Request This Service</h2>

                <% if ("invalid".equals(
                        request.getParameter("requestError"))) { %>

                    <p role="alert" style="color: #a51c1c;">
                        Please enter a message of 1–2000 characters.
                    </p>

                <% } else if ("notallowed".equals(
                        request.getParameter("requestError"))) { %>

                    <p role="alert" style="color: #a51c1c;">
                        This listing is no longer available
                        for service requests.
                    </p>

                <% } %>

                <% if (currentUser == null) { %>

                    <p>
                        Log in to send a request to this provider.
                    </p>

                    <a href="${pageContext.request.contextPath}/login"
                       class="btn btn-primary">
                        Log In to Request Service
                    </a>

                <% } else { %>

                    <p>
                        Tell the provider what work you need done.
                    </p>

                    <form method="post"
                          action="${pageContext.request.contextPath}/requests">

                        <input type="hidden"
                               name="csrfToken"
                               value="<%= csrf %>">

                        <input type="hidden"
                               name="action"
                               value="create">

                        <input type="hidden"
                               name="listingId"
                               value="<%= listing.getListingId() %>">

                        <label for="request-message">
                            Your Message
                        </label>

                        <textarea id="request-message"
                                  name="message"
                                  rows="5"
                                  maxlength="2000"
                                  required
                                  style="display:block;width:100%;box-sizing:border-box;padding:12px;margin:10px 0 16px;border:1px solid #cbd5d1;border-radius:6px;font:inherit;"></textarea>

                        <button type="submit" class="btn btn-primary">
                            Submit Service Request
                        </button>
                    </form>

                <% } %>

            </section>

        <% } %>

    </section>

    <section class="reviews" id="reviews">

        <h2>Customer Reviews</h2>

        <% if (averageRating != null) { %>

            <p>
                <strong>Average Rating:</strong>
                <%= String.format("%.1f", averageRating) %> / 5
            </p>

        <% } else { %>

            <p>No ratings yet.</p>

        <% } %>

        <% String reviewFeedback = request.getParameter("reviewSuccess"); %>
        <% if ("1".equals(reviewFeedback) || "created".equals(reviewFeedback)) { %>
            <p class="review-feedback success" role="status">Your review was submitted successfully.</p>
        <% } else if ("updated".equals(reviewFeedback)) { %>
            <p class="review-feedback success" role="status">Your review was updated successfully.</p>
        <% } else if ("deleted".equals(reviewFeedback)) { %>
            <p class="review-feedback success" role="status">Your review was deleted successfully.</p>
        <% } %>

        <% String reviewErrorMessage = request.getParameter("reviewError"); %>
        <% if ("notowner".equals(reviewErrorMessage)) { %>
            <p class="review-feedback error" role="alert">Review not found, or you do not have permission to modify it.</p>
        <% } else if ("invalid".equals(reviewErrorMessage)) { %>
            <p class="review-feedback error" role="alert">Please provide valid review information.</p>
        <% } else if ("ineligible".equals(reviewErrorMessage)) { %>
            <p class="review-feedback error" role="alert">You can only review your own completed service requests for this listing.</p>
        <% } else if ("duplicate".equals(reviewErrorMessage)) { %>
            <p class="review-feedback error" role="alert">You have already reviewed this request.</p>
        <% } %>

        <% if (reviews != null && !reviews.isEmpty()) { %>
            <% for (ServiceReview review : reviews) {
                int reviewId = review.getReviewId();
                boolean ownsReview = currentUser != null
                        && currentUser.getUserId() == review.getCustomerId();
            %>
                <article class="review">
                    <div id="review-view-<%= reviewId %>" class="review-view">
                        <p class="review-rating"><%= review.getRating() %>/5</p>
                        <h3 class="review-title"><%= escapeHtml(review.getTitle()) %></h3>
                        <p class="review-description"><%= escapeHtml(review.getDescription()) %></p>

                        <% if (ownsReview) { %>
                            <div class="review-actions">
                                <button type="button" class="btn btn-outline"
                                        onclick="toggleReviewEdit(<%= reviewId %>, true)">Edit</button>
                                <button type="submit" form="delete-review-<%= reviewId %>"
                                        class="btn btn-danger">Delete</button>
                            </div>
                        <% } %>
                    </div>

                    <% if (ownsReview) { %>
                        <div id="review-editor-<%= reviewId %>" class="review-editor" hidden>
                            <form method="post" action="${pageContext.request.contextPath}/review">
                                <input type="hidden" name="csrfToken" value="<%= csrf %>">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="listingId" value="<%= listing.getListingId() %>">
                                <input type="hidden" name="reviewId" value="<%= reviewId %>">

                                <label for="review-rating-<%= reviewId %>">Rating</label>
                                <select id="review-rating-<%= reviewId %>" name="rating" required>
                                    <% for (int stars = 5; stars >= 1; stars--) {
                                        String ratingLabel;
                                        switch (stars) {
                                            case 5: ratingLabel = "Excellent"; break;
                                            case 4: ratingLabel = "Good"; break;
                                            case 3: ratingLabel = "Average"; break;
                                            case 2: ratingLabel = "Poor"; break;
                                            default: ratingLabel = "Terrible";
                                        }
                                    %>
                                        <option value="<%= stars %>" <%= stars == review.getRating() ? "selected" : "" %>><%= stars %> - <%= ratingLabel %></option>
                                    <% } %>
                                </select>

                                <label for="review-title-<%= reviewId %>">Review Title</label>
                                <input id="review-title-<%= reviewId %>" type="text" name="title"
                                       maxlength="150" value="<%= escapeHtml(review.getTitle()) %>" required>

                                <label for="review-description-<%= reviewId %>">Review Description</label>
                                <textarea id="review-description-<%= reviewId %>" name="description"
                                          rows="5" required><%= escapeHtml(review.getDescription()) %></textarea>

                                <div class="review-actions">
                                    <button type="submit" class="btn btn-primary">Save Changes</button>
                                    <button type="button" class="btn btn-outline"
                                            onclick="toggleReviewEdit(<%= reviewId %>, false)">Cancel</button>
                                    <button type="submit" form="delete-review-<%= reviewId %>"
                                            class="btn btn-danger">Delete</button>
                                </div>
                            </form>
                        </div>

                        <form id="delete-review-<%= reviewId %>" class="delete-form"
                              method="post" action="${pageContext.request.contextPath}/review"
                              onsubmit="return confirm('Delete this review permanently?');">
                            <input type="hidden" name="csrfToken" value="<%= csrf %>">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="listingId" value="<%= listing.getListingId() %>">
                            <input type="hidden" name="reviewId" value="<%= reviewId %>">
                        </form>
                    <% } %>
                </article>
            <% } %>
        <% } else { %>
            <p>There are no reviews for this service yet.</p>
        <% } %>

    </section>

    <section class="review-form" id="review-form">
        <h2>Write a Review</h2>

        <% if (currentUser == null) { %>
            <p>Log in to review a service after completing a service request.</p>
        <% } else if (isOwner) { %>
            <p>You cannot review your own service listing.</p>
        <% } else if (reviewEligibility == JdbcReviewDAO.ReviewEligibility.NO_REQUEST) { %>
            <p>You cannot write a review because you have not requested this service.</p>
        <% } else if (reviewEligibility == JdbcReviewDAO.ReviewEligibility.NOT_COMPLETED) { %>
            <p>You can review this service after your service request has been marked completed.</p>
        <% } else if (reviewEligibility == JdbcReviewDAO.ReviewEligibility.ALREADY_REVIEWED) { %>
            <p>You have already reviewed your completed service request. You can edit or delete your review above.</p>
        <% } else if (reviewEligibility == JdbcReviewDAO.ReviewEligibility.ELIGIBLE) { %>
            <form method="post" action="${pageContext.request.contextPath}/review">
                <input type="hidden" name="csrfToken" value="<%= csrf %>">
                <input type="hidden" name="action" value="create">
                <input type="hidden" name="listingId" value="<%= listing.getListingId() %>">

                <label for="rating">Rating</label>
                <select id="rating" name="rating" required>
                    <option value="">Choose a rating</option>
                    <option value="5">5 - Excellent</option>
                    <option value="4">4 - Good</option>
                    <option value="3">3 - Average</option>
                    <option value="2">2 - Poor</option>
                    <option value="1">1 - Terrible</option>
                </select>

                <label for="title">Review Title</label>
                <input type="text" id="title" name="title" maxlength="150" required>

                <label for="description">Review Description</label>
                <textarea id="description" name="description" rows="5" required></textarea>

                <button type="submit" class="btn btn-primary">Submit Review</button>
            </form>
        <% } %>
    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

<script>
    function toggleReviewEdit(reviewId, editing) {
        const display = document.getElementById('review-view-' + reviewId);
        const editor = document.getElementById('review-editor-' + reviewId);
        if (!display || !editor) return;
        display.hidden = editing;
        editor.hidden = !editing;
        if (editing) {
            editor.querySelector('select').focus();
        } else {
            editor.querySelector('form').reset();
            display.querySelector('button').focus();
        }
    }
</script>
</body>
</html>
