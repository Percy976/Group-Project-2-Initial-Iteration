
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

    <title>Log In - Local Services Marketplace</title>

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
            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-outline">
                Sign Up
            </a>
        </nav>

    </div>
</header>

<main class="container auth-main">

    <section class="auth-card">

        <h1>Welcome back</h1>
        <p>Log in to your Local Services Marketplace account.</p>

        <% if ("1".equals(request.getParameter("registered"))) { %>
            <p class="message success" role="status">
                Your account has been created. You can now log in.
            </p>
        <% } %>

        <% if ("1".equals(request.getParameter("loggedOut"))) { %>
            <p class="message success" role="status">
                You have been logged out.
            </p>
        <% } %>

        <% if ("1".equals(request.getParameter("reset"))) { %>
            <p class="message success" role="status">
                Your password has been reset. You can now log in.
            </p>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <p class="message error" role="alert">
                <%= escapeHtml(request.getAttribute("error")) %>
            </p>
        <% } %>

        <form method="post"
              action="${pageContext.request.contextPath}/login">

            <input type="hidden"
                   name="csrfToken"
                   value="<%= escapeHtml(session.getAttribute("csrfToken")) %>">

            <label for="username">Username</label>
            <input type="text"
                   id="username"
                   name="username"
                   autocomplete="username"
                   maxlength="50"
                   value="<%= escapeHtml(request.getParameter("username")) %>"
                   required>

            <label for="password">Password</label>
            <input type="password"
                   id="password"
                   name="password"
                   autocomplete="current-password"
                   maxlength="128"
                   required>

            <div class="auth-help">
                <a href="${pageContext.request.contextPath}/forgot-password">
                    Forgot Password?
                </a>
            </div>

            <button type="submit" class="btn btn-primary">
                Log In
            </button>

        </form>

        <p class="auth-footer">
            New here?
            <a href="${pageContext.request.contextPath}/register">
                Create an account
            </a>
        </p>

    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
