
<%@ page contentType="text/html; charset=UTF-8"
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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Sign Up - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/auth.css">
</head>

<body>

<header class="navbar">
    <div class="container nav-content">

        <a href="${pageContext.request.contextPath}/"
           class="brand">
            Local Services
        </a>

        <nav>
            <a href="${pageContext.request.contextPath}/login"
               class="btn btn-outline">
                Log In
            </a>
        </nav>

    </div>
</header>

<main class="container auth-main">

    <section class="auth-card auth-card-wide">

        <h1>Create your account</h1>

        <p>
            Find local services or offer your own
            with the same account.
        </p>

        <% if (request.getAttribute("error") != null) { %>
            <p class="message error" role="alert">
                <%= escapeHtml(request.getAttribute("error")) %>
            </p>
        <% } %>

        <form method="post"
              action="${pageContext.request.contextPath}/register">

            <input type="hidden"
                   name="csrfToken"
                   value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">

            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   autocomplete="username"
                   minlength="3"
                   maxlength="50"
                   pattern="[A-Za-z0-9_]{3,50}"
                   aria-describedby="usernameHint"
                   value="<%= escapeHtml(request.getParameter("username")) %>"
                   required>

            <p id="usernameHint" class="hint">
                Use 3–50 letters, numbers, or underscores.
            </p>

            <label for="firstName">First Name</label>
            <input type="text"
                   id="firstName"
                   name="firstName"
                   autocomplete="given-name"
                   maxlength="100"
                   value="<%= escapeHtml(request.getParameter("firstName")) %>"
                   required>

            <label for="lastName">Last Name</label>
            <input type="text"
                   id="lastName"
                   name="lastName"
                   autocomplete="family-name"
                   maxlength="100"
                   value="<%= escapeHtml(request.getParameter("lastName")) %>"
                   required>

            <label for="email">Email</label>
            <input type="email"
                   id="email"
                   name="email"
                   autocomplete="email"
                   maxlength="255"
                   value="<%= escapeHtml(request.getParameter("email")) %>"
                   required>

            <label for="phoneNumber">Phone Number (optional)</label>
            <input type="tel"
                   id="phoneNumber"
                   name="phoneNumber"
                   autocomplete="tel"
                   maxlength="30"
                   value="<%= escapeHtml(request.getParameter("phoneNumber")) %>">

            <label for="password">Password</label>
            <input type="password"
                   id="password"
                   name="password"
                   autocomplete="new-password"
                   minlength="8"
                   maxlength="128"
                   aria-describedby="passwordHint"
                   required>

            <p id="passwordHint" class="hint">
                Use 8–128 characters.
            </p>

            <label for="confirmPassword">Confirm Password</label>
            <input type="password"
                   id="confirmPassword"
                   name="confirmPassword"
                   autocomplete="new-password"
                   minlength="8"
                   maxlength="128"
                   required>

            <button type="submit" class="btn btn-primary">
                Create Account
            </button>

        </form>

        <p class="auth-footer">
            Already registered?
            <a href="${pageContext.request.contextPath}/login">
                Log In
            </a>
        </p>

    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
