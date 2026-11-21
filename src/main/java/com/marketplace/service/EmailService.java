package com.marketplace.service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/** Sends password-reset messages through local Mailpit by default. */
public class EmailService {

    public void sendPasswordReset(String recipientEmail, String token)
            throws MessagingException {
        String baseUrl = envOrDefault(
                "MARKETPLACE_BASE_URL", "http://localhost:8080/local-services-marketplace");
        String resetLink = baseUrl.replaceAll("/+$", "")
                + "/reset-password?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        sendTextEmail(recipientEmail,
                "Reset Your Local Services Marketplace Password",
                "We received a request to reset your password.\n\n"
                        + "Open this link to choose a new password:\n"
                        + resetLink + "\n\n"
                        + "This link expires in 30 minutes.\n\n"
                        + "If you did not request a password reset, "
                        + "you can ignore this email.");
    }

    /** Sends a welcome message after successful account registration. */
    public void sendRegistrationConfirmation(String recipientEmail, String firstName)
            throws MessagingException {
        String greeting = firstName == null || firstName.isBlank()
                ? "Hello" : "Hello " + firstName.trim();
        sendTextEmail(recipientEmail,
                "Welcome to Local Services Marketplace",
                greeting + ",\n\n"
                        + "Your Local Services Marketplace account has been created successfully.\n"
                        + "You can now log in and start exploring local services.\n\n"
                        + "If you did not create this account, please contact the marketplace administrator.\n\n"
                        + "Local Services Marketplace");
    }

    private void sendTextEmail(String recipientEmail, String subject, String body)
            throws MessagingException {
        // Zero-configuration development defaults: Mailpit's local SMTP port.
        String host = envOrDefault("MARKETPLACE_SMTP_HOST", "127.0.0.1");
        String port = envOrDefault("MARKETPLACE_SMTP_PORT", "1025");
        String sender = envOrDefault("MARKETPLACE_SMTP_FROM", "no-reply@marketplace.test");
        String baseUrl = envOrDefault(
                "MARKETPLACE_BASE_URL", "http://localhost:8080/local-services-marketplace");

        // Optional authenticated SMTP, for environments other than local Mailpit.
        String username = System.getenv("MARKETPLACE_SMTP_USER");
        String password = System.getenv("MARKETPLACE_SMTP_PASSWORD");
        boolean authenticated = username != null && !username.isBlank()
                && password != null && !password.isBlank();
        boolean startTls = Boolean.parseBoolean(
                envOrDefault("MARKETPLACE_SMTP_STARTTLS", authenticated ? "true" : "false"));

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", Boolean.toString(authenticated));
        properties.put("mail.smtp.starttls.enable", Boolean.toString(startTls));
        properties.put("mail.smtp.starttls.required", Boolean.toString(startTls));
        properties.put("mail.smtp.ssl.checkserveridentity", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session;
        if (authenticated) {
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        } else {
            session = Session.getInstance(properties);
        }

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject(subject, "UTF-8");
        message.setText(body, "UTF-8");
        Transport.send(message);
    }

    private static String envOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
