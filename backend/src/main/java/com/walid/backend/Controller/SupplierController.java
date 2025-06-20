package com.walid.backend.Controller;

//import com.walid.backend.Model.AdminJwtUtil;
import com.walid.backend.Model.Supplier;
import com.walid.backend.Service.SupplierService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SupplierController {
    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    // Constructor to inject the service
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public void addSupplierHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            Supplier supplier = ctx.bodyAsClass(Supplier.class);
            System.out.println("Received Supplier: " + supplier);  // Debugging
            supplierService.addSupplier(supplier);
            ctx.status(201).result("Supplier added successfully.");
        } catch (Exception e) {
            logger.error("Error adding supplier", e);
            ctx.status(500).json(Map.of("error", "Error adding supplier", "message", e.getMessage()));
        }
    }
    

    // Handler to get all suppliers
    public void getAllSuppliersHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            List<Supplier> suppliers = supplierService.getAllSuppliers();
            ctx.json(suppliers);
        } catch (SQLException e) {
            logger.error("Error retrieving suppliers", e);
            ctx.status(500).json(Map.of("error", "Error retrieving suppliers", "message", e.getMessage()));
        }
    }

    // Handler to update a supplier
    public void updateSupplierHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int supplierId = Integer.parseInt(ctx.pathParam("id"));
            Supplier supplier = ctx.bodyAsClass(Supplier.class);
            supplier.setSupplierId(supplierId); // Set the ID from the path param
            supplierService.updateSupplier(supplier);
            ctx.result("Supplier updated successfully.");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid supplier ID format.");
        } catch (SQLException e) {
            logger.error("Error updating supplier", e);
            ctx.status(500).json(Map.of("error", "Error updating supplier", "message", e.getMessage()));
        }
    }
    

    // Handler to delete a supplier by ID
    public void deleteSupplierHandler(Context ctx) {
       /*  if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int supplierId = Integer.parseInt(ctx.pathParam("id"));
            supplierService.deleteSupplier(supplierId);
            ctx.result("Supplier deleted successfully.");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid supplier ID format.");
        } catch (SQLException e) {
            logger.error("Error deleting supplier", e);
            ctx.status(500).json(Map.of("error", "Error deleting supplier", "message", e.getMessage()));
        }
    }

    /*private boolean isAuthenticated(Context ctx) {
        String token = ctx.cookie("adminToken");
        if (token == null) {
            return false;
        }
        return AdminJwtUtil.validateToken(token); // Correct method
    }*/
}
