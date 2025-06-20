package com.walid.backend.DAO;

import com.walid.backend.Model.Customer;
import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerDAO {
    
     @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(AdminDAO.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public Customer createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (username, email, phone_number, password_hash, address) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setString(4, customer.getPasswordHash());  // ✅ Ensure this is always hashed before being saved
            stmt.setString(5, customer.getAddress());
    
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                }
            }
        }
        return customer;
    }
    
    
        // ✅ Find Customer by Email for Login
        public Customer findCustomerByEmail(String email) throws SQLException {
            String sql = "SELECT * FROM customers WHERE email = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password_hash"), // ✅ Now we retrieve password hash
                        rs.getString("address"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
            return null; // Customer not found
        }
    
        // ✅ Update Customer Password
        public boolean updatePassword(int customerId, String passwordHash) throws SQLException {
            String sql = "UPDATE customers SET password_hash = ? WHERE customer_id = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, passwordHash);
                stmt.setInt(2, customerId);
                return stmt.executeUpdate() > 0;
            }
        }
        public Customer findCustomerById(int customerId) throws SQLException {
            String sql = "SELECT * FROM customers WHERE customer_id = ?";
            
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    return new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("password_hash"),
                        rs.getString("address"),
                        rs.getTimestamp("created_at")
                    );
                }
            }
            return null; // Customer not found
        }
        
}
