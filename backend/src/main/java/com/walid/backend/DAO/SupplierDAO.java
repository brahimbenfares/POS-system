package com.walid.backend.DAO;

import com.walid.backend.Model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    // Helper method to get the database connection
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection(); // Assumes a DatabaseConfig class exists
    }

    public void saveSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Supplier (supplierName, contactPerson, phoneNumber, email, totalAmountPaid, lastShipmentDate, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getEmail());
            stmt.setDouble(5, supplier.getTotalAmountPaid());
    
            // Handle lastShipmentDate
            if (supplier.getLastShipmentDate() != null) {
                stmt.setDate(6, Date.valueOf(supplier.getLastShipmentDate()));
            } else {
                stmt.setNull(6, java.sql.Types.DATE);
            }
    
            // Handle notes
            if (supplier.getNotes() != null && !supplier.getNotes().isEmpty()) {
                stmt.setString(7, supplier.getNotes());
            } else {
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }
    
            stmt.setString(8, supplier.getStatus());
    
            System.out.println("Executing SQL: " + stmt);  // For debugging
            stmt.executeUpdate();
        }
    }
    
    
    

    // Retrieve all suppliers from the database
    public List<Supplier> getAllSuppliers() throws SQLException {
        String sql = "SELECT * FROM Supplier";
        List<Supplier> suppliers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierId(rs.getInt("supplierId"));
                supplier.setSupplierName(rs.getString("supplierName"));
                supplier.setContactPerson(rs.getString("contactPerson"));
                supplier.setPhoneNumber(rs.getString("phoneNumber"));
                supplier.setEmail(rs.getString("email")); // Retrieve email
                supplier.setTotalAmountPaid(rs.getDouble("totalAmountPaid"));
                supplier.setLastShipmentDate(rs.getDate("lastShipmentDate").toLocalDate()); // Convert to LocalDate
                supplier.setNotes(rs.getString("notes"));
                supplier.setStatus(rs.getString("status"));
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }

    // Update an existing supplier in the database
    public void updateSupplier(Supplier supplier) throws SQLException {
        String sql = "UPDATE Supplier SET supplierName = ?, contactPerson = ?, phoneNumber = ?, email = ?, totalAmountPaid = ?, lastShipmentDate = ?, notes = ?, status = ? WHERE supplierId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getPhoneNumber());
            stmt.setString(4, supplier.getEmail()); // New email field
            stmt.setDouble(5, supplier.getTotalAmountPaid());
            stmt.setDate(6, Date.valueOf(supplier.getLastShipmentDate()));
            stmt.setString(7, supplier.getNotes());
            stmt.setString(8, supplier.getStatus());
            stmt.setInt(9, supplier.getSupplierId());
            stmt.executeUpdate();
        }
    }

    // Delete a supplier from the database by ID
    public void deleteSupplier(int supplierId) throws SQLException {
        String sql = "DELETE FROM Supplier WHERE supplierId = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, supplierId);
            stmt.executeUpdate();
        }
    }
}
