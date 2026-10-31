
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

public class EmailService {

    public void sendPasswordReset(String recipientEmail, String token)
            throws MessagingException {

        String host = requiredEnv("MARKETPLACE_SMTP_HOST");
        String port = requiredEnv("MARKETPLACE_SMTP_PORT");
        String username = requiredEnv("MARKETPLACE_SMTP_USER");
        String password = requiredEnv("MARKETPLACE_SMTP_PASSWORD");
        String sender = requiredEnv("MARKETPLACE_SMTP_FROM");
        String baseUrl = requiredEnv("MARKETPLACE_BASE_URL");

        Properties properties = new Properties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.starttls.required", "true");
        properties.put("mail.smtp.ssl.checkserveridentity", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");
        properties.put("mail.smtp.writetimeout", "10000");

        Session session = Session.getInstance(
                properties,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                username, password);
                    }
                }
        );

        String resetLink = baseUrl.replaceAll("/+$", "")
                + "/reset-password?token="
                + URLEncoder.encode(token, StandardCharsets.UTF_8);

        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(sender));
        message.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(recipientEmail)
        );

        message.setSubject(
                "Reset Your Local Services Marketplace Password",
                "UTF-8"
        );

        message.setText(
                "We received a request to reset your password.\n\n"
                        + "Open this link to choose a new password:\n"
                        + resetLink + "\n\n"
                        + "This link expires in 30 minutes.\n\n"
                        + "If you did not request a password reset, "
                        + "you can ignore this email.",
                "UTF-8"
        );

        Transport.send(message);
    }

    private String requiredEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing email configuration: " + name
            );
        }

        return value;
    }
}
