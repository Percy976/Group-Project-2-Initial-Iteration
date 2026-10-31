
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.ServiceListing" %>

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
            <a href="${pageContext.request.contextPath}/login">
                Log In
            </a>

            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-outline">
                Sign Up
            </a>
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

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
