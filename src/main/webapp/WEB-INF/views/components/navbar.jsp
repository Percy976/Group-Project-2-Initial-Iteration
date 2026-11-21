<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.marketplace.model.User" %>
<%!
    private String navEscape(Object value) {
        if (value == null) return "";
        return value.toString().replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    // Customer and provider are behaviours of the same authenticated user.
    boolean navLoggedIn = session.getAttribute("user") instanceof User;
    boolean hideLogin = "true".equals(request.getParameter("navHideLogin"));
    boolean hideSignup = "true".equals(request.getParameter("navHideSignup"));
%>
<header class="navbar">
    <div class="container nav-content">
        <a class="brand" href="${pageContext.request.contextPath}/">Local Services</a>
        <nav aria-label="Main navigation">
            <% if (navLoggedIn) { %>
                <a href="${pageContext.request.contextPath}/account">My Account</a>
                <form method="post" action="${pageContext.request.contextPath}/logout">
                    <input type="hidden" name="csrfToken"
                           value="<%= navEscape(session.getAttribute("csrfToken")) %>">
                    <button type="submit" class="btn btn-outline">Log Out</button>
                </form>
            <% } else { %>
                <% if (!hideLogin) { %>
                    <a href="${pageContext.request.contextPath}/login">Log In</a>
                <% } %>
                <% if (!hideSignup) { %>
                    <a href="${pageContext.request.contextPath}/register"
                       class="btn btn-outline">Sign Up</a>
                <% } %>
            <% } %>
        </nav>
    </div>
</header>
