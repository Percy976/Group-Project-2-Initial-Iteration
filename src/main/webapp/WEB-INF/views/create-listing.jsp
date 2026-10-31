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

    <title>Create a service listing | Local Services Marketplace</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 40px 20px;
            background: #f3f6f5;
            color: #20352d;
            font-family: Arial, sans-serif;
            line-height: 1.6;
        }

        main {
            max-width: 720px;
            margin: 0 auto;
            padding: 36px;
            background: white;
            border-radius: 16px;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
        }

        h1 {
            margin-top: 0;
        }

        label {
            display: block;
            margin-top: 20px;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input,
        textarea,
        select,
        button {
            width: 100%;
            padding: 12px;
            border: 1px solid #a9bdb3;
            border-radius: 6px;
            font: inherit;
        }

        textarea {
            min-height: 150px;
            resize: vertical;
        }

        button {
            margin-top: 26px;
            border: none;
            background: #216347;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        button:hover {
            background: #184d36;
        }

        a {
            color: #216347;
        }

        .message {
            padding: 14px;
            margin-bottom: 20px;
            border-radius: 6px;
        }

        .success {
            background: #e8f5ed;
            color: #185c39;
        }

        .error {
            background: #fff0f0;
            color: #a51c1c;
        }

        .hint {
            margin: 6px 0 0;
            color: #53665c;
            font-size: 0.9rem;
        }

        nav {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 24px;
        }

        @media (max-width: 480px) {
            body {
                padding: 16px;
            }

            main {
                padding: 22px;
            }
        }
    </style>
</head>
<body>
<main>
    <nav aria-label="Main navigation">
        <a href="<%= contextPath %>/">Browse services</a>
        <a href="<%= contextPath %>/account">My account</a>
    </nav>

    <h1>Create a service listing</h1>
    <p>Describe the service you offer so customers can find you.</p>

    <% if (listingCreated) { %>
        <div class="message success" role="status">
            Your service listing was created successfully.
            <a href="<%= contextPath %>/">Browse services</a>
            to find your listing.
        </div>
    <% } %>

    <% if (error != null) { %>
        <div class="message error" role="alert">
            <%= escapeHtml(error) %>
        </div>
    <% } %>

    <form method="post"
          action="<%= contextPath %>/listings/create">

        <input type="hidden"
               name="csrfToken"
               value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">

        <label for="title">Service title</label>
        <input type="text"
               id="title"
               name="title"
               maxlength="150"
               required
               value="<%= escapeHtml(request.getParameter("title")) %>"
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
               value="<%= escapeHtml(request.getParameter("category")) %>"
               placeholder="Example: Gardening">

        <label for="priceType">Price type</label>
        <select id="priceType" name="priceType" required>
            <option value="NEGOTIABLE"
                <%= "NEGOTIABLE".equals(priceType) ? "selected" : "" %>>
                Negotiable
            </option>

            <option value="FIXED"
                <%= "FIXED".equals(priceType) ? "selected" : "" %>>
                Fixed price
            </option>

            <option value="HOURLY"
                <%= "HOURLY".equals(priceType) ? "selected" : "" %>>
                Hourly rate
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
               value="<%= escapeHtml(request.getParameter("price")) %>">

        <p class="hint" id="priceHint">
            Required for fixed or hourly services.
            You may leave it blank when the price is negotiable.
        </p>

        <button type="submit">Create listing</button>
    </form>
</main>
</body>
</html>