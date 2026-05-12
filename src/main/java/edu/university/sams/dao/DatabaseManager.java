package edu.university.sams.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());

    private final String url = "jdbc:mysql://localhost:3306/sams_db?useSSL=false&serverTimezone=UTC";
    private final String username = "root";
    private final String password = "mysql";

    private DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
            LOGGER.info("Database connected successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Database connection failed: " + e.getMessage(), e);
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
                LOGGER.info("Database reconnected successfully!");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error reconnecting to database: " + e.getMessage(), e);
            throw new RuntimeException("Failed to reconnect to database", e);
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LOGGER.info("Database connection closed");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing database connection: " + e.getMessage(), e);
        }
    }
}