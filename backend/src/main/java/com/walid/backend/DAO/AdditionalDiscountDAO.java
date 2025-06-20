package com.walid.backend.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.backend.Model.AdditionalDiscount;

public class AdditionalDiscountDAO {
    private static final Logger logger = LoggerFactory.getLogger(AdditionalDiscountDAO.class);

    // Connection helper method
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    // Save additional discount
    public void saveAdditionalDiscount(AdditionalDiscount discount) throws SQLException {
        String sql = "INSERT INTO additional_discounts (order_item_id, description, discount_percentage, discount_amount) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, discount.getOrderItemId());
            stmt.setString(2, discount.getDescription());
            stmt.setDouble(3, discount.getDiscountPercentage());
            stmt.setDouble(4, discount.getDiscountAmount());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error saving additional discount: ", e);
            throw e;
        }
    }

    // Retrieve additional discounts by order_item_id
    public List<AdditionalDiscount> getAdditionalDiscountsByOrderItemId(int orderItemId) throws SQLException {
        List<AdditionalDiscount> discounts = new ArrayList<>();
        String sql = "SELECT * FROM additional_discounts WHERE order_item_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AdditionalDiscount discount = new AdditionalDiscount();
                    discount.setDiscountId(rs.getInt("discount_id"));
                    discount.setOrderItemId(rs.getInt("order_item_id"));
                    discount.setDescription(rs.getString("description"));
                    discount.setDiscountPercentage(rs.getDouble("discount_percentage"));
                    discount.setDiscountAmount(rs.getDouble("discount_amount"));
                    discount.setAppliedAt(rs.getTimestamp("applied_at"));
                    discounts.add(discount);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving additional discounts: ", e);
            throw e;
        }

        return discounts;
    }
}
