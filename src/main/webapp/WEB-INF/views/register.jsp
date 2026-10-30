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
    <title>Create account | Local Services Marketplace</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0;
            padding: 32px 16px;
            background: #f3f6f5;
            color: #18352b;
            font-family: Arial, sans-serif;
        }
        main {
            max-width: 520px;
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
            margin-top: 16px;
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
        .error {
            padding: 12px;
            background: #fff0f0;
            color: #9c2020;
            border-radius: 6px;
        }
        .hint { color: #53685e; font-size: 14px; }
    </style>
</head>
<body>
<main>
    <h1>Create your account</h1>
    <p>Find local services or offer your own with the same account.</p>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error" role="alert"><%=
            escapeHtml(request.getAttribute("error"))
        %></p>
    <% } %>

    <form method="post"
          action="<%= request.getContextPath() %>/register">

        <input type="hidden" name="csrfToken"
               value="<%= escapeHtml(
                       session.getAttribute("csrfToken")) %>">

        <label for="username">Username</label>
        <input id="username" name="username"
               autocomplete="username"
               minlength="3" maxlength="50"
               pattern="[A-Za-z0-9_]{3,50}"
               aria-describedby="usernameHint"
               value="<%= escapeHtml(
                       request.getParameter("username")) %>"
               required>
        <p id="usernameHint" class="hint">
            Use 3–50 letters, numbers, or underscores.
        </p>

        <label for="firstName">First name</label>
        <input id="firstName" name="firstName"
               autocomplete="given-name" maxlength="100"
               value="<%= escapeHtml(
                       request.getParameter("firstName")) %>"
               required>

        <label for="lastName">Last name</label>
        <input id="lastName" name="lastName"
               autocomplete="family-name" maxlength="100"
               value="<%= escapeHtml(
                       request.getParameter("lastName")) %>"
               required>

        <label for="email">Email</label>
        <input id="email" name="email" type="email"
               autocomplete="email" maxlength="255"
               value="<%= escapeHtml(
                       request.getParameter("email")) %>"
               required>

        <label for="phoneNumber">Phone number (optional)</label>
        <input id="phoneNumber" name="phoneNumber" type="tel"
               autocomplete="tel" maxlength="30"
               value="<%= escapeHtml(
                       request.getParameter("phoneNumber")) %>">

        <label for="password">Password</label>
        <input id="password" name="password" type="password"
               autocomplete="new-password"
               minlength="8" maxlength="128"
               aria-describedby="passwordHint" required>
        <p id="passwordHint" class="hint">
            Use 8–128 characters.
        </p>

        <label for="confirmPassword">Confirm password</label>
        <input id="confirmPassword" name="confirmPassword"
               type="password" autocomplete="new-password"
               minlength="8" maxlength="128" required>

        <button type="submit">Create account</button>
    </form>

    <p>Already registered?
        <a href="<%= request.getContextPath() %>/login">Log in</a>
    </p>
</main>
</body>
</html>