
<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.dao.JdbcServiceRequestDAO" %>
<%@ page import="com.marketplace.model.User" %>
<%@ page import="java.util.List" %>

<%!
    private String esc(Object value) {
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
    List<JdbcServiceRequestDAO.RequestRow> sent =
            (List<JdbcServiceRequestDAO.RequestRow>)
                    request.getAttribute("sentRequests");

    List<JdbcServiceRequestDAO.RequestRow> received =
            (List<JdbcServiceRequestDAO.RequestRow>)
                    request.getAttribute("receivedRequests");

    User user = (User) session.getAttribute("user");
    String result = request.getParameter("result");
    String csrf = esc(session.getAttribute("csrfToken"));
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1">

    <title>Service Requests - Local Services Marketplace</title>

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/common.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/service-requests.css">
</head>
<body>

<header class="navbar">
    <div class="container nav-content">

        <a href="${pageContext.request.contextPath}/" class="brand">
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
                       value="<%= csrf %>">
                <button type="submit" class="btn btn-outline">
                    Log Out
                </button>
            </form>
        </nav>
    </div>
</header>

<main class="container requests-main">

    <p>
        <a href="${pageContext.request.contextPath}/account">
            &larr; Back to My Account
        </a>
    </p>

    <h1>Service Requests</h1>

    <% if ("created".equals(result)) { %>
        <p class="request-message success" role="status">
            Your service request was submitted successfully.
        </p>
    <% } else if ("updated".equals(result)) { %>
        <p class="request-message success" role="status">
            The request has been updated.
        </p>
    <% } else if ("notallowed".equals(result)) { %>
        <p class="request-message error" role="alert">
            This request cannot be changed. It may have already
            been updated or you may not have permission.
        </p>
    <% } else if ("invalid".equals(result)) { %>
        <p class="request-message error" role="alert">
            Invalid request information.
        </p>
    <% } %>

    <section class="request-section">

        <h2>Requests I've Sent</h2>

        <% if (sent == null || sent.isEmpty()) { %>

            <p>You haven't requested any services yet.</p>

        <% } else { %>

            <% for (JdbcServiceRequestDAO.RequestRow item : sent) { %>

                <article class="request-card">

                    <div class="request-header">
                        <h3><%= esc(item.getListingTitle()) %></h3>

                        <span class="request-status">
                            <%= esc(item.getStatus()) %>
                        </span>
                    </div>

                    <p>
                        <strong>Provider:</strong>
                        <%= esc(item.getProviderName()) %>
                    </p>

                    <p>
                        <strong>Request #:</strong>
                        <%= item.getRequestId() %>
                    </p>

                    <p>
                        <strong>Submitted:</strong>
                        <%= esc(item.getRequestedAt()) %>
                    </p>

                    <p class="request-text">
                        <strong>My Message:</strong>
                        <%= esc(item.getMessage()) %>
                    </p>

                    <% if (item.getProviderResponse() != null
                            && !item.getProviderResponse().isBlank()) { %>

                        <p class="request-text">
                            <strong>Provider Response:</strong>
                            <%= esc(item.getProviderResponse()) %>
                        </p>
                    <% } %>

                    <div class="request-actions">

                        <% if ("PENDING".equals(item.getStatus())) { %>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/requests"
                                  onsubmit="return confirm('Cancel this request?');">

                                <input type="hidden"
                                       name="csrfToken"
                                       value="<%= csrf %>">

                                <input type="hidden"
                                       name="requestId"
                                       value="<%= item.getRequestId() %>">

                                <input type="hidden"
                                       name="action" value="cancel">

                                <button type="submit"
                                        class="btn btn-outline">
                                    Cancel Request
                                </button>
                            </form>

                        <% } %>

                        <% if ("COMPLETED".equals(item.getStatus())
                                && !item.isReviewed()) { %>

                            <a class="btn btn-primary"
                               href="${pageContext.request.contextPath}/listing?id=<%= item.getListingId() %>&requestId=<%= item.getRequestId() %>#review-form">
                                Write a Review
                            </a>

                        <% } else if (item.isReviewed()) { %>

                            <span class="reviewed-label">
                                Reviewed
                            </span>

                        <% } %>

                    </div>
                </article>

            <% } %>

        <% } %>
    </section>

    <section class="request-section">

        <h2>Requests I've Received</h2>

        <% if (received == null || received.isEmpty()) { %>

            <p>No customers have requested your services yet.</p>

        <% } else { %>

            <% for (JdbcServiceRequestDAO.RequestRow item : received) { %>

                <article class="request-card">

                    <div class="request-header">
                        <h3><%= esc(item.getListingTitle()) %></h3>

                        <span class="request-status">
                            <%= esc(item.getStatus()) %>
                        </span>
                    </div>

                    <p>
                        <strong>Customer:</strong>
                        <%= esc(item.getCustomerName()) %>
                    </p>

                    <p>
                        <strong>Request #:</strong>
                        <%= item.getRequestId() %>
                    </p>

                    <p>
                        <strong>Submitted:</strong>
                        <%= esc(item.getRequestedAt()) %>
                    </p>

                    <p class="request-text">
                        <strong>Customer Message:</strong>
                        <%= esc(item.getMessage()) %>
                    </p>

                    <% if (item.getProviderResponse() != null
                            && !item.getProviderResponse().isBlank()) { %>

                        <p class="request-text">
                            <strong>My Response:</strong>
                            <%= esc(item.getProviderResponse()) %>
                        </p>

                    <% } %>

                    <% if ("PENDING".equals(item.getStatus())) { %>

                        <form class="response-form"
                              method="post"
                              action="${pageContext.request.contextPath}/requests">

                            <input type="hidden"
                                   name="csrfToken" value="<%= csrf %>">

                            <input type="hidden"
                                   name="requestId"
                                   value="<%= item.getRequestId() %>">

                            <label for="response-<%= item.getRequestId() %>">
                                Response (optional)
                            </label>

                            <textarea id="response-<%= item.getRequestId() %>"
                                      name="providerResponse"
                                      rows="3"
                                      maxlength="2000"
                                      placeholder="Add a message for the customer"></textarea>

                            <div class="request-actions">
                                <button type="submit"
                                        name="action"
                                        value="accept"
                                        class="btn btn-primary">
                                    Accept
                                </button>

                                <button type="submit"
                                        name="action"
                                        value="decline"
                                        class="btn btn-outline">
                                    Decline
                                </button>
                            </div>
                        </form>

                    <% } else if ("ACCEPTED".equals(item.getStatus())) { %>

                        <form method="post"
                              action="${pageContext.request.contextPath}/requests"
                              onsubmit="return confirm('Mark this service as completed?');">

                            <input type="hidden"
                                   name="csrfToken"
                                   value="<%= csrf %>">

                            <input type="hidden"
                                   name="requestId"
                                   value="<%= item.getRequestId() %>">

                            <input type="hidden"
                                   name="action"
                                   value="complete">

                            <button type="submit" class="btn btn-primary">
                                Mark Completed
                            </button>
                        </form>

                    <% } %>

                </article>

            <% } %>

        <% } %>
    </section>
</main>

<footer>
    <p>© 2026 Local Services Marketplace</p>
</footer>

</body>
</html>
