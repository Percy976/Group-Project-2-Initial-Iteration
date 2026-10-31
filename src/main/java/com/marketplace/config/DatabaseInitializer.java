
package com.marketplace.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void initialize() {

        String url = "jdbc:mysql://localhost:3306/";
        String user = "marketplace";
        String password = System.getenv("MARKETPLACE_DB_PASSWORD");

        if (password == null) {
            throw new IllegalStateException(
                    "MARKETPLACE_DB_PASSWORD is not configured."
            );
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection connection =
                         DriverManager.getConnection(url, user, password)) {

                executeSqlFile(connection, "db/database_setup.sql");
                executeSqlFile(connection, "db/marketplace_seed_listings.sql");
                executeSqlFile(connection, "db/marketplace_seed_reviews.sql");

                System.out.println(
                        "Database initialization completed successfully."
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Database initialization failed.", e
            );
        }
    }

    private static void executeSqlFile(
            Connection connection, String resourcePath) throws Exception {

        try (InputStream input =
                     DatabaseInitializer.class.getClassLoader()
                             .getResourceAsStream(resourcePath)) {

            if (input == null) {
                throw new IllegalStateException(
                        "SQL file not found: " + resourcePath
                );
            }

            String sql;

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))) {

                sql = reader.lines()
                        .filter(line -> !line.trim().startsWith("--"))
                        .collect(Collectors.joining("\n"));
            }

            try (Statement statement = connection.createStatement()) {

                for (String command : sql.split(";")) {
                    if (!command.isBlank()) {
                        statement.execute(command.trim());
                    }
                }
            }

            System.out.println("Executed: " + resourcePath);
        }
    }
}
