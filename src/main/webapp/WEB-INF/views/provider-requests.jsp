<%@ page contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.ServiceRequest" %>
<%@ page import="java.util.List" %>

<%
    List<ServiceRequest> serviceRequests =
            (List<ServiceRequest>) request.getAttribute("serviceRequests");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Service Requests - Local Services Marketplace</title>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<header>
    <h1>Local Services Marketplace</h1>
    <nav>
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/my-account">My Account</a>
    </nav>
</header>

<main class="container">
    <h2>Incoming Service Requests</h2>

    <% if (serviceRequests == null || serviceRequests.isEmpty()) { %>

        <p>You currently have no service requests.</p>

    <% } else { %>

        <% for (ServiceRequest serviceRequest : serviceRequests) { %>

            <section class="request-card">

                <h3>Request #<%= serviceRequest.getRequestId() %></h3>

                <p>
                    <strong>Listing ID:</strong>
                    <%= serviceRequest.getListingId() %>
                </p>

                <p>
                    <strong>Customer ID:</strong>
                    <%= serviceRequest.getCustomerId() %>
                </p>

                <p>
                    <strong>Request Details:</strong>
                    <%= serviceRequest.getJobDetails() %>
                </p>

                <p>
                    <strong>Status:</strong>
                    <%= serviceRequest.getStatus() %>
                </p>

                <% if (serviceRequest.getResponseMessage() != null
                        && !serviceRequest.getResponseMessage().isBlank()) { %>

                    <p>
                        <strong>Your Response:</strong>
                        <%= serviceRequest.getResponseMessage() %>
                    </p>

                <% } %>

            </section>

        <% } %>

    <% } %>

</main>

</body>
</html>
