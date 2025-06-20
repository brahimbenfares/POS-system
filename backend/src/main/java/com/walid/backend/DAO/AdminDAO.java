package com.walid.backend.DAO;

import com.walid.backend.Model.Admin;

import com.walid.backend.Model.Promotion;
import com.walid.backend.Model.PromotionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mindrot.jbcrypt.BCrypt;
import java.util.List;
import java.util.ArrayList;

public class AdminDAO {

    private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public Admin getAdminByLogin(String username, String password) {
        if (username == null || password == null) {
            logger.error("Username or password is null");
            return null;
        }
    
        try (Connection connection = DatabaseConfig.getConnection()) {
            String sql = "SELECT * FROM admins WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHash = resultSet.getString("password_hash");
                        logger.info("Stored Hash for user {}: {}", username, storedHash);
    
                        // Verify the provided password with the stored hash
                        if (BCrypt.checkpw(password, storedHash)) {
                            logger.info("Password verified successfully for user {}", username);
    
                            return new Admin(
                                resultSet.getInt("admin_id"),
                                resultSet.getString("username"),
                                null, // Do not return the password hash
                                resultSet.getString("role_name"),
                                resultSet.getString("email"),
                                resultSet.getBoolean("is_active")
                            );
                        } else {
                            logger.error("Password verification failed for user {}", username);
                        }
                    } else {
                        logger.error("No user found with username {}", username);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQL error when retrieving admin by login", e);
        } catch (NumberFormatException e) {
            logger.error("Number format exception during password verification for user {}", username, e);
        }
        return null;
    }
    

    
    public Admin getAdminByUsername(String username) {
        if (username == null) {
            logger.error("Username is null");
            return null;
        }

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            String sql = "SELECT * FROM admins WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Admin admin = new Admin(
                            resultSet.getInt("admin_id"),
                            resultSet.getString("username"),
                            null, // Do not return the password hash
                            resultSet.getString("role_name"),
                            resultSet.getString("email"),
                            resultSet.getBoolean("is_active")
                        );
                        connection.commit();
                        return admin;
                    }
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving admin by username: ", e);
        }
        return null;
    }

    public Admin saveAdmin(String username, String hashedPassword, String email, String roleName, boolean isActive) {
        String sql = "INSERT INTO admins (username, password_hash, email, role_name, is_active) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, roleName);
            preparedStatement.setBoolean(5, isActive);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                logger.error("Creating admin failed, no rows affected.");
                return null;
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int adminId = generatedKeys.getInt(1);
                    return new Admin(adminId, username, null, roleName, email, isActive);
                } else {
                    logger.error("Creating admin failed, no ID obtained.");
                    return null;
                }
            }

        } catch (SQLException e) {
            logger.error("SQL error when creating admin", e);
            return null;
        }
    }

    

    public boolean updateAdminRole(String username, String role, boolean isActive) {
        if (username == null || role == null) {
            logger.error("Username or role is null");
            return false;
        }
    
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            String sql = "UPDATE admins SET role_name = ?, is_active = ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, role);
                preparedStatement.setBoolean(2, isActive);
                preparedStatement.setString(3, username);
    
                if (preparedStatement.executeUpdate() > 0) {
                    connection.commit();
                    return true;
                }
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error updating admin role: ", e);
        }
        return false;
    }
    
    

    public boolean deleteAdmin(String username) {
        if (username == null) {
            logger.error("Username is null");
            return false;
        }

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            String sql = "DELETE FROM admins WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);

                if (preparedStatement.executeUpdate() > 0) {
                    connection.commit();
                    return true;
                }
                connection.rollback();
            }
        } catch (SQLException e) {
            logger.error("Error deleting admin: ", e);
        }
        return false;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            String sql = "SELECT admin_id, username, role_name, email, is_active FROM admins"; // No password hash
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Admin admin = new Admin(
                        resultSet.getInt("admin_id"),
                        resultSet.getString("username"),
                        null, // Do not return the password hash
                        resultSet.getString("role_name"),
                        resultSet.getString("email"),
                        resultSet.getBoolean("is_active")
                    );
                    admins.add(admin);
                }
                connection.commit();
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all admins: ", e);
        }
        return admins;
    }

    public boolean doesAdminExist(String username) {
        if (username == null) {
            logger.error("Username is null");
            return false;
        }

        try (Connection connection = getConnection()) {
            String sql = "SELECT COUNT(*) AS count FROM admins WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("count") > 0;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking if admin exists: ", e);
        }
        return false;
    }

    public Admin getAdminById(int adminId) {
        Connection connection = null;
        try {
            connection = getConnection();
            String sql = "SELECT admin_id, username, role_name, email, is_active FROM admins WHERE admin_id = ?"; // No password hash
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, adminId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return new Admin(
                            resultSet.getInt("admin_id"),
                            resultSet.getString("username"),
                            null, // Do not return the password hash
                            resultSet.getString("role_name"),
                            resultSet.getString("email"),
                            resultSet.getBoolean("is_active")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("SQL error when retrieving admin by ID", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Failed to close connection", e);
                }
            }
        }
        return null;
    }


    public List<Promotion> getAllPromotions() throws SQLException {
        String sql = "SELECT * FROM promotions";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
    
            List<Promotion> promotions = new ArrayList<>();
            while (resultSet.next()) {
                Promotion promotion = new Promotion(
                    resultSet.getInt("promo_id"),
                    resultSet.getString("description"),
                    resultSet.getTimestamp("start_date").toLocalDateTime(),
                    resultSet.getTimestamp("end_date").toLocalDateTime(),
                    resultSet.getDouble("discount_percentage"),
                    resultSet.getBoolean("active")
                );
                promotions.add(promotion);
            }
            return promotions;
        } catch (SQLException e) {
            logger.error("Error fetching promotions: ", e);
            throw e;
        }
    }
    

    public int createPromotion(PromotionDTO promotionDTO) throws SQLException {
        String sql = "INSERT INTO promotions (description, start_date, end_date, discount_percentage, active) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
             
            preparedStatement.setString(1, promotionDTO.getDescription());

            // Convert LocalDateTime to Timestamp
            Timestamp startTimestamp = Timestamp.valueOf(promotionDTO.getStartDate());
            Timestamp endTimestamp = Timestamp.valueOf(promotionDTO.getEndDate());

            preparedStatement.setTimestamp(2, startTimestamp);
            preparedStatement.setTimestamp(3, endTimestamp);
            preparedStatement.setDouble(4, promotionDTO.getDiscountPercentage());
            preparedStatement.setBoolean(5, promotionDTO.isActive());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating promotion failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);  // Retrieve the generated promotion ID
                } else {
                    throw new SQLException("Creating promotion failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating promotion: ", e);
            throw e;  // Propagate the exception to handle it in the service layer
        }
    }


    
    

    public boolean linkPromotionToProducts(int promoId, List<Integer> productIds) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM product_promotions pp " +
                          "JOIN promotions pr ON pp.promo_id = pr.promo_id " +
                          "WHERE pp.product_id = ? AND pr.active = 1";
        String insertSql = "INSERT INTO product_promotions (promo_id, product_id) VALUES (?, ?)";
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
    
            try (PreparedStatement checkStmt = connection.prepareStatement(checkSql);
                 PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
    
                for (Integer productId : productIds) {
                    // Check if the product already has an active promotion
                    checkStmt.setInt(1, productId);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            logger.warn("Product ID {} already has an active promotion. Skipping linking.", productId);
                            continue; // Skip linking to prevent multiple active promotions
                        }
                    }
    
                    // Link promotion to product
                    insertStmt.setInt(1, promoId);
                    insertStmt.setInt(2, productId);
                    insertStmt.addBatch();
                }
    
                int[] results = insertStmt.executeBatch();
                connection.commit();
                return results.length > 0;
            }
        } catch (SQLException e) {
            logger.error("Error linking promotion to products: ", e);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back transaction", ex);
                }
            }
            throw e; // Propagate exception
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ex) {
                    logger.error("Error resetting auto-commit or closing connection", ex);
                }
            }
        }
    }
    
    
    

    public boolean updatePromotion(Promotion promotion) throws SQLException {
        String sql = "UPDATE promotions SET description = ?, start_date = ?, end_date = ?, discount_percentage = ?, active = ? WHERE promo_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
    
            preparedStatement.setString(1, promotion.getDescription() != null ? promotion.getDescription() : "");
            preparedStatement.setTimestamp(2, promotion.getStartDate() != null ? Timestamp.valueOf(promotion.getStartDate()) : null);
            preparedStatement.setTimestamp(3, promotion.getEndDate() != null ? Timestamp.valueOf(promotion.getEndDate()) : null);
            preparedStatement.setDouble(4, promotion.getDiscountPercentage());
            preparedStatement.setBoolean(5, promotion.isActive());
            preparedStatement.setInt(6, promotion.getPromoId());
    
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No promotion found with ID {}", promotion.getPromoId());
                return false;
            }
            logger.info("Promotion ID {} updated successfully.", promotion.getPromoId());
            return true;
        } catch (SQLException e) {
            logger.error("Error updating promotion: ", e);
            throw e; // Re-throw to handle it in the service layer
        }
    }
    
    

    public boolean deletePromotion(int promoId) throws SQLException {
        String sql = "UPDATE promotions SET active = 0 WHERE promo_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, promoId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("No promotion found with ID {}", promoId);
                return false;
            }
            logger.info("Promotion ID {} deactivated successfully.", promoId);
            return true;
        } catch (SQLException e) {
            logger.error("Error deactivating promotion: ", e);
            throw e;  // Propagate exception to handle it in the service layer
        }
    }
    
    
    public Promotion getPromotionById(int promoId) throws SQLException {
        String promoSql = "SELECT * FROM promotions WHERE promo_id = ?";
        String productIdsSql = "SELECT product_id FROM product_promotions WHERE promo_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement promoStmt = connection.prepareStatement(promoSql);
             PreparedStatement productIdsStmt = connection.prepareStatement(productIdsSql)) {
    
            promoStmt.setInt(1, promoId);
            try (ResultSet promoRs = promoStmt.executeQuery()) {
                if (promoRs.next()) {
                    Promotion promotion = new Promotion(
                        promoRs.getInt("promo_id"),
                        promoRs.getString("description"),
                        promoRs.getTimestamp("start_date").toLocalDateTime(),
                        promoRs.getTimestamp("end_date").toLocalDateTime(),
                        promoRs.getDouble("discount_percentage"),
                        promoRs.getBoolean("active")
                    );
    
                    // Fetch associated product IDs
                    productIdsStmt.setInt(1, promoId);
                    try (ResultSet productIdsRs = productIdsStmt.executeQuery()) {
                        List<Integer> productIds = new ArrayList<>();
                        while (productIdsRs.next()) {
                            productIds.add(productIdsRs.getInt("product_id"));
                        }
                        promotion.setProductIds(productIds);
                    }
    
                    return promotion;
                } else {
                    return null; // Promotion not found
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching promotion by ID: ", e);
            throw e;
        }
    }
    
    public boolean deleteInventoryAlert(int alertId) {
        String sql = "DELETE FROM inventory_alerts WHERE alert_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, alertId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting inventory alert: ", e);
            return false;
        }
    }

}
