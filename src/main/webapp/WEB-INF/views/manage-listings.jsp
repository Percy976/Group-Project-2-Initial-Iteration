
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.dao.ManageListingDAO" %>
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
    List<ManageListingDAO.Listing> listings =
            (List<ManageListingDAO.Listing>)
                    request.getAttribute("listings");

    ManageListingDAO.Listing editing =
            (ManageListingDAO.Listing)
                    request.getAttribute("editing");

    String result = request.getParameter("result");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>My Listings - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/manage-listings.css">
</head>

<body>

<header class="navbar">
    <div class="container nav-content">
        <a href="${pageContext.request.contextPath}/"
           class="brand">Local Services</a>

        <nav>
            <a href="${pageContext.request.contextPath}/account">
                My Account
            </a>

            <form method="post"
                  action="${pageContext.request.contextPath}/logout">
                <input type="hidden" name="csrfToken"
                       value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">
                <button class="btn btn-outline" type="submit">
                    Log Out
                </button>
            </form>
        </nav>
    </div>
</header>

<main class="container manage-main">
    <section class="manage-card">

        <p>
            <a href="${pageContext.request.contextPath}/account">
                &larr; Back to My Account
            </a>
        </p>

        <% if (editing != null) { %>

            <h1>Edit Listing</h1>

            <% if (request.getAttribute("error") != null) { %>
                <p class="message error" role="alert">
                    <%= escapeHtml(request.getAttribute("error")) %>
                </p>
            <% } %>

            <form method="post"
                  class="listing-edit-form"
                  action="${pageContext.request.contextPath}/account/listings">

                <input type="hidden" name="csrfToken"
                       value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">

                <input type="hidden" name="listingId"
                       value="<%= editing.id %>">

                <input type="hidden" name="action" value="update">

                <label for="title">Service Title</label>
                <input type="text" id="title" name="title"
                       maxlength="150" required
                       value="<%= escapeHtml(editing.title) %>">

                <label for="description">Description</label>
                <textarea id="description" name="description"
                          rows="6" maxlength="10000"
                          required><%= escapeHtml(editing.description) %></textarea>

                <label for="category">Category</label>
                <input type="text" id="category" name="category"
                       maxlength="100" required
                       value="<%= escapeHtml(editing.category) %>">

                <label for="priceType">Price Type</label>
                <select id="priceType" name="priceType" required>
                    <option value="NEGOTIABLE"
                        <%= "NEGOTIABLE".equals(editing.priceType)
                                ? "selected" : "" %>>
                        Negotiable
                    </option>
                    <option value="FIXED"
                        <%= "FIXED".equals(editing.priceType)
                                ? "selected" : "" %>>
                        Fixed Price
                    </option>
                    <option value="HOURLY"
                        <%= "HOURLY".equals(editing.priceType)
                                ? "selected" : "" %>>
                        Hourly Rate
                    </option>
                </select>

                <label for="price">Price (CAD)</label>
                <input type="number" id="price" name="price"
                       min="0" max="99999999.99" step="0.01"
                       value="<%= escapeHtml(editing.price) %>">

                <p class="hint">
                    Required for fixed and hourly services.
                    Optional for negotiable pricing.
                </p>

                <div class="listing-actions">
                    <button type="submit" class="btn btn-primary">
                        Save Changes
                    </button>
                    <a href="${pageContext.request.contextPath}/account/listings"
                       class="btn btn-outline">
                        Cancel
                    </a>
                </div>
            </form>

        <% } else { %>

            <div class="manage-heading">
                <h1>My Listings</h1>
                <a href="${pageContext.request.contextPath}/listings/create"
                   class="btn btn-primary">
                    Create Listing
                </a>
            </div>

            <% if ("updated".equals(result)) { %>
                <p class="message success" role="status">
                    Your listing was updated successfully.
                </p>
            <% } else if ("removed".equals(result)) { %>
                <p class="message success" role="status">
                    Your listing has been removed from public browsing.
                </p>
            <% } else if ("activated".equals(result)) { %>
                <p class="message success" role="status">
                    Your listing is active again.
                </p>
            <% } %>

            <% if (listings == null || listings.isEmpty()) { %>

                <p>
                    You haven't created any service listings yet.
                </p>

            <% } else { %>

                <% for (ManageListingDAO.Listing listing : listings) { %>

                    <article class="managed-listing">
                        <div>
                            <h2><%= escapeHtml(listing.title) %></h2>

                            <p class="listing-meta">
                                <%= escapeHtml(listing.category) %>
                                &middot;
                                <%= "ACTIVE".equals(listing.status)
                                    ? "Active" : "Inactive" %>
                                &middot;
                                <%= listing.price == null
                                    ? "Negotiable"
                                    : "$" + escapeHtml(listing.price) %>
                            </p>
                        </div>

                        <div class="listing-actions">

                            <% if ("ACTIVE".equals(listing.status)) { %>
                                <a class="btn btn-outline"
                                   href="${pageContext.request.contextPath}/listing?id=<%= listing.id %>">
                                    View
                                </a>
                            <% } %>

                            <a class="btn btn-outline"
                               href="${pageContext.request.contextPath}/account/listings?edit=<%= listing.id %>">
                                Edit
                            </a>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/account/listings"
                                  onsubmit="return confirm('Change this listing\'s availability?');">

                                <input type="hidden" name="csrfToken"
                                       value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">
                                <input type="hidden" name="listingId"
                                       value="<%= listing.id %>">

                                <% if ("ACTIVE".equals(listing.status)) { %>
                                    <input type="hidden" name="action"
                                           value="deactivate">
                                    <button type="submit"
                                            class="btn btn-danger">
                                        Remove
                                    </button>
                                <% } else { %>
                                    <input type="hidden" name="action"
                                           value="activate">
                                    <button type="submit"
                                            class="btn btn-primary">
                                        Restore
                                    </button>
                                <% } %>
                            </form>
                        </div>
                    </article>

                <% } %>

            <% } %>

        <% } %>

    </section>
</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
