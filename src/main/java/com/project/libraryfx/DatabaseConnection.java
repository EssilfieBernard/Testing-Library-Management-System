package com.project.libraryfx;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();

        // Get database credentials from .env file
        String url = dotenv.get("DB_URL");
        String username = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        // Attempt to establish connection
        try {
            var connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully");
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
