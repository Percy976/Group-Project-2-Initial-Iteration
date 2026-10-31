
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.User" %>
<%@ page import="com.marketplace.model.ServiceListing" %>
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
    User provider = (User) request.getAttribute("provider");

    List<ServiceListing> providerListings =
            (List<ServiceListing>)
                    request.getAttribute("providerListings");

    User currentUser =
            session.getAttribute("user") instanceof User
                    ? (User) session.getAttribute("user")
                    : null;

    boolean ownProfile = currentUser != null
            && currentUser.getUserId() == provider.getUserId();

    String providerName = provider.getName().isBlank()
            ? provider.getUsername()
            : provider.getName();
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>
        <%= escapeHtml(providerName) %>
        - Local Services Marketplace
    </title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/provider-profile.css">
</head>

<body>

<header class="navbar">
    <div class="container nav-content">

        <a href="${pageContext.request.contextPath}/"
           class="brand">
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
                           value="<%= escapeHtml(
                               session.getAttribute("csrfToken")) %>">

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

<main class="container provider-main">

    <section class="provider-card">

        <p>
            <a href="${pageContext.request.contextPath}/">
                &larr; Back to Services
            </a>
        </p>

        <h1><%= escapeHtml(providerName) %></h1>
        <p class="provider-subtitle">Service Provider</p>

        <h2>About This Provider</h2>

        <% if (provider.getBio() != null
                && !provider.getBio().isBlank()) { %>

            <p class="provider-bio">
                <%= escapeHtml(provider.getBio()) %>
            </p>

        <% } else { %>

            <p class="provider-empty">
                This provider hasn't added a description yet.
            </p>

        <% } %>

        <% if (ownProfile) { %>
            <a href="${pageContext.request.contextPath}/account"
               class="btn btn-outline">
                Edit My Profile
            </a>
        <% } %>

    </section>

    <section class="provider-card">

        <h2>Services Offered</h2>

        <% if (providerListings == null
                || providerListings.isEmpty()) { %>

            <p class="provider-empty">
                This provider has no active service listings.
            </p>

        <% } else { %>

            <div class="provider-listings">

                <% for (ServiceListing listing : providerListings) { %>

                    <article class="provider-listing">

                        <h3>
                            <%= escapeHtml(listing.getServiceName()) %>
                        </h3>

                        <p class="listing-description">
                            <%= escapeHtml(listing.getDescription()) %>
                        </p>

                        <div class="provider-listing-footer">

                            <strong>
                                <% if (listing.getPrice() != null) { %>
                                    $<%= listing.getPrice() %>
                                <% } else { %>
                                    Contact for pricing
                                <% } %>
                            </strong>

                            <a class="btn btn-primary"
                               href="${pageContext.request.contextPath}/listing?id=<%= listing.getListingId() %>">
                                View Details
                            </a>

                        </div>

                    </article>

                <% } %>

            </div>

        <% } %>

    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
