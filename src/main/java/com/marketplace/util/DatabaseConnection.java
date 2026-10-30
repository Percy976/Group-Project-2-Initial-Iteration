package com.marketplace.util;

 import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/local_services_marketplace";

    private static final String USER = "marketplace";

    private static final String PASSWORD = System.getenv("MARKETPLACE_DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "MySQL JDBC driver class could not be loaded.", e);
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}