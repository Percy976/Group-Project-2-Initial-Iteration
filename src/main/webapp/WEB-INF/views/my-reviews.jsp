<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.dao.JdbcReviewDAO.MyReviewRow" %>
<%@ page import="com.marketplace.model.ServiceReview" %>
<%@ page import="java.util.List" %>

<%!
    private String escapeHtml(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    List<MyReviewRow> myReviews = (List<MyReviewRow>) request.getAttribute("myReviews");
    String csrf = escapeHtml(session.getAttribute("csrfToken"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Reviews - Local Services Marketplace</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/my-reviews.css">
</head>
<body>
<header class="navbar">
    <div class="container nav-content">
        <a class="brand" href="${pageContext.request.contextPath}/">Local Services</a>
        <nav>
            <a href="${pageContext.request.contextPath}/account">My Account</a>
            <form method="post" action="${pageContext.request.contextPath}/logout">
                <input type="hidden" name="csrfToken" value="<%= csrf %>">
                <button type="submit" class="btn btn-outline">Log Out</button>
            </form>
        </nav>
    </div>
</header>

<main class="container reviews-main">
    <section class="reviews-card">
        <a href="${pageContext.request.contextPath}/account">&larr; Back to My Account</a>
        <h1>My Reviews</h1>
        <p>Manage reviews you've written for completed services.</p>

        <% String success = request.getParameter("reviewSuccess"); %>
        <% if ("updated".equals(success)) { %>
            <p class="message success" role="status">Your review was updated successfully.</p>
        <% } else if ("deleted".equals(success)) { %>
            <p class="message success" role="status">Your review was deleted successfully.</p>
        <% } %>
        <% String error = request.getParameter("reviewError"); %>
        <% if ("invalid".equals(error)) { %>
            <p class="message error" role="alert">Please provide valid review information.</p>
        <% } else if ("notowner".equals(error)) { %>
            <p class="message error" role="alert">Review not found, or you do not have permission to modify it.</p>
        <% } %>

        <% if (myReviews == null || myReviews.isEmpty()) { %>
            <p>You haven't written any reviews yet.</p>
            <a href="${pageContext.request.contextPath}/listings" class="btn btn-primary">Browse Services</a>
        <% } else { %>
            <% for (MyReviewRow item : myReviews) {
                ServiceReview review = item.review();
                int reviewId = review.getReviewId();
            %>
                <article class="review-item">
                    <h2 class="service-name">
                        <a href="${pageContext.request.contextPath}/listing?id=<%= review.getListingId() %>#reviews"><%= escapeHtml(item.listingName()) %></a>
                    </h2>

                    <div id="review-view-<%= reviewId %>" class="review-view">
                        <p class="review-rating"><%= review.getRating() %>/5</p>
                        <h3 class="review-title"><%= escapeHtml(review.getTitle()) %></h3>
                        <p class="review-description"><%= escapeHtml(review.getDescription()) %></p>
                        <div class="review-actions">
                            <button type="button" class="btn btn-outline"
                                    onclick="toggleReviewEdit(<%= reviewId %>, true)">Edit</button>
                            <button type="submit" form="delete-review-<%= reviewId %>" class="btn btn-danger">Delete</button>
                        </div>
                    </div>

                    <div id="review-editor-<%= reviewId %>" class="review-editor" hidden>
                        <form id="edit-review-<%= reviewId %>" method="post" action="${pageContext.request.contextPath}/review">
                            <input type="hidden" name="csrfToken" value="<%= csrf %>">
                            <input type="hidden" name="returnTo" value="my-reviews">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="listingId" value="<%= review.getListingId() %>">
                            <input type="hidden" name="reviewId" value="<%= reviewId %>">

                            <label for="rating-<%= reviewId %>">Rating</label>
                            <select id="rating-<%= reviewId %>" name="rating" required>
                                <% for (int stars = 5; stars >= 1; stars--) { %>
                                    <option value="<%= stars %>" <%= stars == review.getRating() ? "selected" : "" %>><%= stars %> - <%= stars == 5 ? "Excellent" : stars == 4 ? "Good" : stars == 3 ? "Average" : stars == 2 ? "Poor" : "Terrible" %></option>
                                <% } %>
                            </select>
                            <label for="title-<%= reviewId %>">Review Title</label>
                            <input type="text" id="title-<%= reviewId %>" name="title" maxlength="150" value="<%= escapeHtml(review.getTitle()) %>" required>
                            <label for="description-<%= reviewId %>">Review Description</label>
                            <textarea id="description-<%= reviewId %>" name="description" rows="5" required><%= escapeHtml(review.getDescription()) %></textarea>

                            <div class="review-actions">
                                <button type="submit" class="btn btn-primary">Save Changes</button>
                                <button type="button" class="btn btn-outline"
                                        onclick="toggleReviewEdit(<%= reviewId %>, false)">Cancel</button>
                                <button type="submit" form="delete-review-<%= reviewId %>" class="btn btn-danger">Delete</button>
                            </div>
                        </form>
                    </div>

                    <form id="delete-review-<%= reviewId %>" class="delete-form" method="post"
                          action="${pageContext.request.contextPath}/review"
                          onsubmit="return confirm('Delete this review permanently?');">
                        <input type="hidden" name="csrfToken" value="<%= csrf %>">
                        <input type="hidden" name="returnTo" value="my-reviews">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="listingId" value="<%= review.getListingId() %>">
                        <input type="hidden" name="reviewId" value="<%= reviewId %>">
                    </form>
                </article>
            <% } %>
        <% } %>
    </section>
</main>
<footer><p>© 2026 Local Services Marketplace</p></footer>
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
