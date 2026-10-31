
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>

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

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Reset Password - Local Services Marketplace</title>

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

    <section class="auth-card">

        <h1>Reset Password</h1>

        <% if (Boolean.TRUE.equals(
                request.getAttribute("invalidToken"))) { %>

            <p class="message error" role="alert">
                This password reset link is invalid, expired,
                or has already been used.
            </p>

            <p>
                You can request a new password reset link.
            </p>

            <p class="auth-footer">
                <a href="${pageContext.request.contextPath}/forgot-password">
                    Request New Link
                </a>
            </p>

        <% } else { %>

            <p>
                Enter a new password for your account.
            </p>

            <% if (request.getAttribute("error") != null) { %>
                <p class="message error" role="alert">
                    <%= escapeHtml(request.getAttribute("error")) %>
                </p>
            <% } %>

            <form method="post"
                  action="${pageContext.request.contextPath}/reset-password">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= escapeHtml(
                           session.getAttribute("csrfToken")) %>">

                <input type="hidden"
                       name="token"
                       value="<%= escapeHtml(
                           request.getAttribute("token")) %>">

                <label for="password">New Password</label>

                <input type="password"
                       id="password"
                       name="password"
                       autocomplete="new-password"
                       minlength="8"
                       maxlength="128"
                       required>

                <p class="hint">
                    Use 8–128 characters.
                </p>

                <label for="confirmPassword">
                    Confirm New Password
                </label>

                <input type="password"
                       id="confirmPassword"
                       name="confirmPassword"
                       autocomplete="new-password"
                       minlength="8"
                       maxlength="128"
                       required>

                <button type="submit" class="btn btn-primary">
                    Reset Password
                </button>

            </form>

        <% } %>

        <p class="auth-footer">
            <a href="${pageContext.request.contextPath}/login">
                Back to Log In
            </a>
        </p>

    </section>

</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
