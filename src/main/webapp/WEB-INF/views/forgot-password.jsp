
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
          content="width=device-width, initial-scale=1.0">

    <title>Forgot Password - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/auth.css">
</head>

<body>

<jsp:include page="/WEB-INF/views/components/navbar.jsp">
    <jsp:param name="navHideLogin" value="true" />
</jsp:include>

<main class="container auth-main">

    <section class="auth-card">

        <h1>Forgot Password?</h1>

        <p>
            Enter your registered email address to request
            a password reset link.
        </p>

        <% if (Boolean.TRUE.equals(
                request.getAttribute("submitted"))) { %>

            <p class="message success" role="status">
                If an account exists with that email address,
                a password reset link will be sent.
            </p>

        <% } else { %>

            <% if (request.getAttribute("error") != null) { %>

                <p class="message error" role="alert">
                    <%= escapeHtml(request.getAttribute("error")) %>
                </p>

            <% } %>

            <form method="post"
                  action="${pageContext.request.contextPath}/forgot-password">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= escapeHtml(
                           session.getAttribute("csrfToken")) %>">

                <label for="email">Email Address</label>

                <input type="email"
                       id="email"
                       name="email"
                       maxlength="255"
                       autocomplete="email"
                       required>

                <button type="submit" class="btn btn-primary">
                    Request Password Reset
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
