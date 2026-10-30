<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="com.marketplace.model.User" %>
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
    User accountUser = (User) session.getAttribute("user");

    if (accountUser == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My account | Local Services Marketplace</title>
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
            max-width: 620px;
            margin: auto;
            padding: 32px;
            background: white;
            border-radius: 14px;
            box-shadow: 0 6px 24px #00000012;
        }
        h1 { margin-top: 0; }
        p { line-height: 1.5; }
        .success {
            padding: 12px;
            background: #eaf6ee;
            color: #216347;
            border-radius: 6px;
        }
        dl { margin: 24px 0; }
        dt {
            margin-top: 18px;
            color: #53685e;
            font-size: 14px;
            font-weight: bold;
        }
        dd {
            margin: 6px 0 0;
            overflow-wrap: anywhere;
        }
        button {
            margin-top: 12px;
            padding: 12px 24px;
            border: 0;
            border-radius: 6px;
            background: #216347;
            color: white;
            font: inherit;
            font-weight: bold;
            cursor: pointer;
        }
    </style>
</head>
<body>
<main>
    <h1>My account</h1>

    <p class="success" role="status">
        You are logged in.
    </p>

    <h2>Welcome, <%= escapeHtml(accountUser.getFirstName()) %>!</h2>

    <dl>
        <dt>Username</dt>
        <dd><%= escapeHtml(accountUser.getUsername()) %></dd>

        <dt>Name</dt>
        <dd><%= escapeHtml(accountUser.getName()) %></dd>

        <dt>Email</dt>
        <dd><%= escapeHtml(accountUser.getEmail()) %></dd>

        <dt>Phone number</dt>
        <dd><%= accountUser.getPhoneNumber() == null
                    || accountUser.getPhoneNumber().isBlank()
                ? "Not provided"
                : escapeHtml(accountUser.getPhoneNumber()) %></dd>
    </dl>

    <form method="post"
          action="<%= request.getContextPath() %>/logout">

        <input type="hidden" name="csrfToken"
               value="<%= escapeHtml(
                       session.getAttribute("csrfToken")) %>">

        <button type="submit">Log out</button>
    </form>
</main>
</body>
</html>