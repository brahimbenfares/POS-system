package com.walid.backend.DAO;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource ds;

    static {
        initializeDataSource();
    }

    // Initialize the DataSource in a method to avoid static block clutter
    private static void initializeDataSource() {
        Properties properties = new Properties();

        // Try loading the dbconfig.properties file from resources
        try (InputStream input = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("dbconfig.properties")) {

            if (input == null) {
                logger.error("Failed to find dbconfig.properties file in resources.");
                throw new RuntimeException("dbconfig.properties file not found");
            }

            properties.load(input);

            String dbUrl = properties.getProperty("dbUrl");
            String dbUsername = properties.getProperty("dbUsername");
            String dbPassword = properties.getProperty("dbPassword");

            logger.info("Database URL: {}", dbUrl);  // Log the URL to verify it's correct

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUsername);
            config.setPassword(dbPassword);
            config.setMaximumPoolSize(10);  // Adjust based on application load

            ds = new HikariDataSource(config);

            // Test the connection during startup
            testDatabaseConnection();
        } catch (IOException e) {
            logger.error("Error reading dbconfig.properties", e);
            throw new RuntimeException("Failed to load database configuration", e);
        }
    }

    // Helper method to test the database connection on startup
    private static void testDatabaseConnection() {
        try (Connection conn = ds.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                logger.info("Successfully connected to the database.");
            }
        } catch (SQLException e) {
            logger.error("Database connection failed. Verify the credentials and database URL.", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    // Method to get a database connection from the HikariCP pool
    public static Connection getConnection() throws SQLException {
        if (ds == null) {
            throw new RuntimeException("DataSource is not initialized.");
        }
        return ds.getConnection();
    }
}
