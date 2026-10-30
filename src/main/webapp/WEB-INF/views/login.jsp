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
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Log in | Local Services Marketplace</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            padding: 48px 16px;
            background: #f3f6f5;
            color: #18352b;
            font-family: Arial, sans-serif;
        }
        main {
            max-width: 460px;
            margin: auto;
            padding: 32px;
            background: white;
            border-radius: 14px;
            box-shadow: 0 6px 24px #00000012;
        }
        h1 { margin-top: 0; }
        p { line-height: 1.5; }
        label {
            display: block;
            margin-top: 18px;
            font-weight: bold;
        }
        input {
            width: 100%;
            margin-top: 6px;
            padding: 12px;
            border: 1px solid #aabbb3;
            border-radius: 6px;
            font: inherit;
        }
        button {
            width: 100%;
            margin-top: 24px;
            padding: 13px;
            border: 0;
            border-radius: 6px;
            background: #216347;
            color: white;
            font: inherit;
            font-weight: bold;
            cursor: pointer;
        }
        a { color: #216347; }
        .message {
            padding: 12px;
            border-radius: 6px;
        }
        .error {
            background: #fff0f0;
            color: #9c2020;
        }
        .success {
            background: #eaf6ee;
            color: #216347;
        }
    </style>
</head>
<body>
<main>
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

    <% if (request.getAttribute("error") != null) { %>
        <p class="message error" role="alert"><%=
            escapeHtml(request.getAttribute("error"))
        %></p>
    <% } %>

    <form method="post"
          action="<%= request.getContextPath() %>/login">

        <input type="hidden" name="csrfToken"
               value="<%= escapeHtml(
                       session.getAttribute("csrfToken")) %>">

        <label for="username">Username</label>
        <input id="username" name="username"
               autocomplete="username" maxlength="50"
               value="<%= escapeHtml(
                       request.getParameter("username")) %>"
               required>

        <label for="password">Password</label>
        <input id="password" name="password" type="password"
               autocomplete="current-password"
               maxlength="128" required>

        <button type="submit">Log in</button>
    </form>

    <p>New here?
        <a href="<%= request.getContextPath() %>/register">
            Create an account
        </a>
    </p>
</main>
</body>
</html>