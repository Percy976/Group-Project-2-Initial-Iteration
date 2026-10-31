
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"
         import="com.marketplace.model.User" %>

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

    <title>My Account - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/account.css">
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

<main class="container account-main">

    <section class="account-card">

        <h1>My Account</h1>

        <% if ("1".equals(request.getParameter("updated"))) { %>
            <p class="success" role="status">
                Your profile has been updated successfully.
            </p>
        <% } %>

        <div id="profile-view">

            <dl>
                <dt>Username</dt>
                <dd><%= escapeHtml(accountUser.getUsername()) %></dd>

                <dt>Name</dt>
                <dd><%= escapeHtml(accountUser.getName()) %></dd>

                <dt>Email</dt>
                <dd><%= escapeHtml(accountUser.getEmail()) %></dd>

                <dt>Phone number</dt>
                <dd>
                    <%= accountUser.getPhoneNumber() == null
                            || accountUser.getPhoneNumber().isBlank()
                            ? "Not provided"
                            : escapeHtml(accountUser.getPhoneNumber()) %>
                </dd>

                <dt>Bio</dt>
                <dd>
                    <%= accountUser.getBio() == null
                            || accountUser.getBio().isBlank()
                            ? "Not provided"
                            : escapeHtml(accountUser.getBio()) %>
                </dd>
            </dl>

            <button type="button"
                    id="edit-profile-button"
                    class="btn btn-primary">
                Edit Profile
            </button>

        </div>

        <section id="profile-edit" class="edit-profile" hidden>

            <form id="edit-profile-form"
                  method="post"
                  action="${pageContext.request.contextPath}/account/update">

                <input type="hidden"
                       name="csrfToken"
                       value="<%= escapeHtml(
                           session.getAttribute("csrfToken")) %>">

                <label for="firstName">First Name</label>

                <input type="text"
                       id="firstName"
                       name="firstName"
                       maxlength="100"
                       value="<%= escapeHtml(accountUser.getFirstName()) %>"
                       required>

                <label for="lastName">Last Name</label>

                <input type="text"
                       id="lastName"
                       name="lastName"
                       maxlength="100"
                       value="<%= escapeHtml(accountUser.getLastName()) %>"
                       required>

                <label for="email">Email</label>

                <input type="email"
                       id="email"
                       name="email"
                       maxlength="255"
                       value="<%= escapeHtml(accountUser.getEmail()) %>"
                       required>

                <label for="phone">Phone Number</label>

                <input type="tel"
                       id="phone"
                       name="phone"
                       maxlength="30"
                       value="<%= escapeHtml(accountUser.getPhoneNumber()) %>">

                <label for="bio">Bio</label>

                <textarea id="bio"
                          name="bio"
                          rows="4"
                          maxlength="2000"><%= escapeHtml(accountUser.getBio()) %></textarea>

                <div class="profile-actions">

                    <button type="submit" class="btn btn-primary">
                        Save Changes
                    </button>

                    <button type="button"
                            id="cancel-edit-button"
                            class="btn btn-outline">
                        Cancel
                    </button>

                </div>
            </form>
        </section>

        <section class="edit-profile">

            <h2>My Service Listings</h2>

            <p>
                View, edit, or remove the services you offer
                in the marketplace.
            </p>

            <div class="profile-actions">

                <a href="${pageContext.request.contextPath}/account/listings"
                   class="btn btn-primary">
                    Manage My Listings
                </a>

                <a href="${pageContext.request.contextPath}/listings/create"
                   class="btn btn-outline">
                    Create Listing
                </a>

            </div>
        </section>

        <section class="edit-profile">

            <h2>Service Requests</h2>

            <p>
                Manage requests you've sent to providers
                and requests customers have sent to you.
            </p>

            <div class="profile-actions">
                <a href="${pageContext.request.contextPath}/requests"
                   class="btn btn-primary">
                    Manage Service Requests
                </a>
            </div>

        </section>

    </section>
</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

<script>
    const profileView = document.getElementById("profile-view");
    const profileEdit = document.getElementById("profile-edit");
    const editButton = document.getElementById("edit-profile-button");
    const cancelButton = document.getElementById("cancel-edit-button");
    const editForm = document.getElementById("edit-profile-form");

    editButton.addEventListener("click", () => {
        profileView.hidden = true;
        profileEdit.hidden = false;
        document.getElementById("firstName").focus();
    });

    cancelButton.addEventListener("click", () => {
        editForm.reset();
        profileEdit.hidden = true;
        profileView.hidden = false;
    });
</script>

</body>
</html>
