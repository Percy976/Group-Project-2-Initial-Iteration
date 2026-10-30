
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
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
    List<ServiceListing> listings =
            (List<ServiceListing>) request.getAttribute("listings");

    boolean isBatch =
            Boolean.TRUE.equals(request.getAttribute("isBatch"));

    String searchQuery =
            (String) request.getAttribute("searchQuery");

    boolean searching =
            searchQuery != null && !searchQuery.isBlank();
%>

<% if (!isBatch) { %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Local Services Marketplace</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/service-listings.css">
</head>
<body>

<header class="navbar">
    <div class="container nav-content">
        <a href="#" class="brand">Local Services</a>

        <nav>
            <a href="${pageContext.request.contextPath}/listings">
                Browse Services
            </a>
            <a href="#">Log In</a>
            <a href="#" class="btn btn-outline">Sign Up</a>
        </nav>
    </div>
</header>

<section class="hero">
    <div class="container">
        <h1>Find services in your community</h1>
        <p>Connect with local professionals for everyday jobs.</p>

        <form class="search-form"
              action="${pageContext.request.contextPath}/listings"
              method="get">

            <input type="search"
                   name="q"
                   value="<%= escapeHtml(searchQuery) %>"
                   placeholder="Search services or providers...">

            <button type="submit" class="btn btn-primary">
                Search
            </button>
        </form>
    </div>
</section>

<main class="container">

    <section class="browse-header">
        <div>

            <h2>
                <%= searching ? "Search Results" : "Available Services" %>
            </h2>

            <p>
                <% if (searching) { %>
                    Showing results for
                    <strong><%= escapeHtml(searchQuery) %></strong>
                    —
                    <a href="${pageContext.request.contextPath}/listings">
                        Clear search
                    </a>
                <% } else { %>
                    Explore services offered by local providers.
                <% } %>
            </p>

        </div>
    </section>

    <section class="listing-grid" id="listing-grid">
<% } %>

<%
    if (listings != null) {
        for (ServiceListing listing : listings) {
%>

    <article class="listing-card">

        <div class="listing-body">

            <h3><%= escapeHtml(listing.getServiceName()) %></h3>

            <p class="description">
                <%= escapeHtml(listing.getDescription()) %>
            </p>

            <p class="provider">
                Provided by <%= escapeHtml(listing.getProviderName()) %>
            </p>

            <div class="listing-footer">

                <strong>
                    <% if (listing.getPrice() != null) { %>
                        $<%= listing.getPrice() %>
                    <% } else { %>
                        Contact for pricing
                    <% } %>
                </strong>

                <a href="#" class="btn btn-primary">
                    View Details
                </a>

            </div>
        </div>
    </article>

<%
        }
    }
%>

<% if (!isBatch) { %>
    </section>

    <% if (listings == null || listings.isEmpty()) { %>
        <p>
            <%= searching
                    ? "No matching services found. Try a different keyword."
                    : "No services are currently available." %>
        </p>
    <% } %>

    <div id="scroll-trigger"></div>

    <p id="results-message" hidden aria-live="polite">
        You've reached the end of the results.
    </p>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

<script>
    const grid = document.getElementById("listing-grid");
    const trigger = document.getElementById("scroll-trigger");
    const resultsMessage = document.getElementById("results-message");

    const pageSize = 30;

    // Preserve the search when requesting additional pages.
    const searchQuery =
        new URLSearchParams(window.location.search).get("q") || "";

    let nextPage = 1;
    let loading = false;
    let hasMore = <%= listings != null && listings.size() == 30 %>;

    function showEndMessage() {
        resultsMessage.textContent =
            "You've reached the end of the results.";
        resultsMessage.hidden = false;
    }

    const observer = new IntersectionObserver(async (entries) => {

        if (!entries[0].isIntersecting || loading || !hasMore) {
            return;
        }

        loading = true;
        observer.unobserve(trigger);

        resultsMessage.textContent = "Loading more services...";
        resultsMessage.hidden = false;

        try {
            // Include both pagination and search parameters.
            const params = new URLSearchParams({
                page: String(nextPage),
                q: searchQuery
            });

            const response = await fetch(
                "${pageContext.request.contextPath}/listings?"
                + params.toString()
            );

            if (!response.ok) {
                throw new Error("Failed to load listings");
            }

            const html = await response.text();

            const template = document.createElement("template");
            template.innerHTML = html.trim();

            const newCards =
                template.content.querySelectorAll(".listing-card");

            const count = newCards.length;

            grid.appendChild(template.content);

            nextPage++;
            hasMore = count === pageSize;

            if (hasMore) {
                resultsMessage.hidden = true;
                observer.observe(trigger);
            } else {
                showEndMessage();
            }

        } catch (error) {
            console.error("Error loading listings:", error);

            resultsMessage.textContent =
                "Unable to load more services. Please refresh to try again.";
            resultsMessage.hidden = false;

            // Prevent repeated failed requests.
            hasMore = false;

        } finally {
            loading = false;
        }

    }, {
        rootMargin: "300px"
    });

    if (hasMore) {
        observer.observe(trigger);
    } else if (grid.querySelectorAll(".listing-card").length > 0) {
        showEndMessage();
    }

</script>

</body>
</html>
<% } %>
