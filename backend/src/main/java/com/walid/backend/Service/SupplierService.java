package com.walid.backend.Service;

import com.walid.backend.DAO.SupplierDAO;
import com.walid.backend.Model.Supplier;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {
    private final SupplierDAO supplierDAO;

    // Constructor to inject the DAO
    public SupplierService(SupplierDAO supplierDAO) {
        this.supplierDAO = supplierDAO;
    }

    // Add a new supplier
    public void addSupplier(Supplier supplier) throws SQLException {
        supplierDAO.saveSupplier(supplier);
    }

    // Retrieve all suppliers
    public List<Supplier> getAllSuppliers() throws SQLException {
        return supplierDAO.getAllSuppliers();
    }

    // Update a supplier
    public void updateSupplier(Supplier supplier) throws SQLException {
        supplierDAO.updateSupplier(supplier);
    }

    // Delete a supplier by ID
    public void deleteSupplier(int supplierId) throws SQLException {
        supplierDAO.deleteSupplier(supplierId);
    }
}
