package com.walid.backend.DAO;

import com.walid.backend.Model.WholesaleCustomer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WholesaleCustomerDAO {
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public void saveCustomer(WholesaleCustomer customer) throws SQLException {
        String sql = "INSERT INTO WholesaleCustomer (customerName, phoneNumber, totalPurchases, loyaltyPoints, lastPurchaseDate, loyaltyStatus, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setDouble(3, customer.getTotalPurchases());
            stmt.setInt(4, customer.getLoyaltyPoints());
            stmt.setString(5, customer.getLastPurchaseDate());
            stmt.setString(6, customer.getLoyaltyStatus());
            stmt.setString(7, customer.getNotes());
            stmt.executeUpdate();
        }
    }

    public List<WholesaleCustomer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM WholesaleCustomer";
        List<WholesaleCustomer> customers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                WholesaleCustomer customer = new WholesaleCustomer();
                customer.setCustomerId(rs.getInt("customerId"));
                customer.setCustomerName(rs.getString("customerName"));
                customer.setPhoneNumber(rs.getString("phoneNumber"));
                customer.setTotalPurchases(rs.getDouble("totalPurchases"));
                customer.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
                customer.setLastPurchaseDate(rs.getString("lastPurchaseDate"));
                customer.setLoyaltyStatus(rs.getString("loyaltyStatus"));
                customer.setNotes(rs.getString("notes"));
                customers.add(customer);
            }
        }
        return customers;
    }

    public void updateCustomer(WholesaleCustomer customer) throws SQLException {
        String sql = "UPDATE WholesaleCustomer SET customerName = ?, phoneNumber = ?, totalPurchases = ?, loyaltyPoints = ?, lastPurchaseDate = ?, loyaltyStatus = ?, notes = ? WHERE customerId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getCustomerName());
            stmt.setString(2, customer.getPhoneNumber());
            stmt.setDouble(3, customer.getTotalPurchases());
            stmt.setInt(4, customer.getLoyaltyPoints());
            stmt.setString(5, customer.getLastPurchaseDate());
            stmt.setString(6, customer.getLoyaltyStatus());
            stmt.setString(7, customer.getNotes());
            stmt.setInt(8, customer.getCustomerId());
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM WholesaleCustomer WHERE customerId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.executeUpdate();
        }
    }
}
