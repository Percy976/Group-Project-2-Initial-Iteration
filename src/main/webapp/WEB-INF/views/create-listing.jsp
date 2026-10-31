
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

<%!
    private String escapeHtml(Object value) {
        if (value == null) {
            return "";
        }

        return value.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>

<%
    String contextPath = request.getContextPath();
    String priceType = request.getParameter("priceType");

    if (priceType == null) {
        priceType = "NEGOTIABLE";
    }

    String created = request.getParameter("created");
    boolean listingCreated = created != null
            && created.matches("[1-9][0-9]*");

    Object error = request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Create a Service Listing - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/create-listing.css">
</head>

<body>

<header class="navbar">
    <div class="container nav-content">

        <a href="${pageContext.request.contextPath}/"
           class="brand">
            Local Services
        </a>

        <nav>
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
        </nav>

    </div>
</header>

<main class="container create-listing-main">

    <section class="create-listing-card">

        <p>
            <a href="${pageContext.request.contextPath}/">
                &larr; Back to Services
            </a>
        </p>

        <h1>Create a Service Listing</h1>

        <p>
            Describe the service you offer so customers
            can find you.
        </p>

        <% if (listingCreated) { %>

            <div class="message success" role="status">
                Your service listing was created successfully.
                <a href="${pageContext.request.contextPath}/">
                    Browse services
                </a>
                to find your listing.
            </div>

        <% } %>

        <% if (error != null) { %>

            <div class="message error" role="alert">
                <%= escapeHtml(error) %>
            </div>

        <% } %>

        <form method="post"
              action="${pageContext.request.contextPath}/listings/create">

            <input type="hidden"
                   name="csrfToken"
                   value="<%= escapeHtml(
                       session.getAttribute("csrfToken")) %>">

            <label for="title">Service Title</label>

            <input type="text"
                   id="title"
                   name="title"
                   maxlength="150"
                   required
                   value="<%= escapeHtml(
                       request.getParameter("title")) %>"
                   placeholder="Example: Lawn mowing in Ottawa">

            <label for="description">Description</label>

            <textarea id="description"
                      name="description"
                      maxlength="10000"
                      required
                      placeholder="Explain what you offer and what is included."
            ><%= escapeHtml(request.getParameter("description")) %></textarea>

            <label for="category">Category</label>

            <input type="text"
                   id="category"
                   name="category"
                   maxlength="100"
                   required
                   value="<%= escapeHtml(
                       request.getParameter("category")) %>"
                   placeholder="Example: Gardening">

            <label for="priceType">Price Type</label>

            <select id="priceType"
                    name="priceType"
                    required>

                <option value="NEGOTIABLE"
                    <%= "NEGOTIABLE".equals(priceType)
                            ? "selected" : "" %>>
                    Negotiable
                </option>

                <option value="FIXED"
                    <%= "FIXED".equals(priceType)
                            ? "selected" : "" %>>
                    Fixed Price
                </option>

                <option value="HOURLY"
                    <%= "HOURLY".equals(priceType)
                            ? "selected" : "" %>>
                    Hourly Rate
                </option>

            </select>

            <label for="price">Price (CAD)</label>

            <input type="number"
                   id="price"
                   name="price"
                   min="0"
                   max="99999999.99"
                   step="0.01"
                   aria-describedby="priceHint"
                   value="<%= escapeHtml(
                       request.getParameter("price")) %>">

            <p class="hint" id="priceHint">
                Required for fixed or hourly services.
                You may leave it blank when the price is negotiable.
            </p>

            <button type="submit" class="btn btn-primary">
                Create Listing
            </button>

        </form>

    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
