package com.walid.backend.Controller;

//import com.walid.backend.Model.AdminJwtUtil;
import com.walid.backend.Model.WholesaleCustomer;
import com.walid.backend.Service.WholesaleCustomerService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class WholesaleCustomerController {
    private static final Logger logger = LoggerFactory.getLogger(WholesaleCustomerController.class);
    private final WholesaleCustomerService customerService;

    // Constructor to inject the service
    public WholesaleCustomerController(WholesaleCustomerService customerService) {
        this.customerService = customerService;
    }



    /*private boolean isAuthenticated(Context ctx) {
        String token = ctx.cookie("adminToken");
        if (token == null) {
            return false;
        }
        return AdminJwtUtil.validateToken(token); // Correct method
    }*/
    // Handler to add a new customer
    public void addCustomerHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            WholesaleCustomer customer = ctx.bodyAsClass(WholesaleCustomer.class);
            customerService.addCustomer(customer);
            ctx.status(201).result("Customer added successfully.");
        } catch (Exception e) {
            logger.error("Error adding customer", e);
            ctx.status(500).json(Map.of("error", "Error adding customer", "message", e.getMessage()));
        }
    }

    // Handler to get all customers
    public void getAllCustomersHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            List<WholesaleCustomer> customers = customerService.getAllCustomers();
            ctx.json(customers);
        } catch (SQLException e) {
            logger.error("Error retrieving customers", e);
            ctx.status(500).json(Map.of("error", "Error retrieving customers", "message", e.getMessage()));
        }
    }

    // Handler to update a customer
    public void updateCustomerHandler(Context ctx) {
        
        try {
            WholesaleCustomer customer = ctx.bodyAsClass(WholesaleCustomer.class);
            customerService.updateCustomer(customer);
            ctx.result("Customer updated successfully.");
        } catch (SQLException e) {
            logger.error("Error updating customer", e);
            ctx.status(500).json(Map.of("error", "Error updating customer", "message", e.getMessage()));
        }
    }

    // Handler to delete a customer by ID
    public void deleteCustomerHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int customerId = Integer.parseInt(ctx.pathParam("id"));
            customerService.deleteCustomer(customerId);
            ctx.result("Customer deleted successfully.");
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid customer ID format.");
        } catch (SQLException e) {
            logger.error("Error deleting customer", e);
            ctx.status(500).json(Map.of("error", "Error deleting customer", "message", e.getMessage()));
        }
    }
}
