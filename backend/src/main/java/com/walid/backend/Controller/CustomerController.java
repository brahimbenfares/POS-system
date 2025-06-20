package com.walid.backend.Controller;

import com.walid.backend.Model.Customer;
import com.walid.backend.Model.JwtUtil;
import com.walid.backend.Service.CustomerService;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private CustomerService customerService = new CustomerService();

    // ✅ Handle Creating a New Customer
    public void addCustomerHandler(Context ctx) {
        try {
            Customer customer = ctx.bodyAsClass(Customer.class);
            logger.info("Received Customer Data: {}", customer);

            Customer createdCustomer = customerService.createCustomer(
                    customer.getUsername(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getPasswordHash(),
                    customer.getAddress()
            );

            ctx.status(201).json(Map.of("customerId", createdCustomer.getCustomerId()));
        } catch (Exception e) {
            logger.error("Error adding customer", e);
            ctx.status(500).json(Map.of("error", "Error adding customer", "message", e.getMessage()));
        }
 
   }


    // ✅ Customer Login Handler
    public void loginHandler(Context ctx) {
        try {
            // ✅ Parse JSON request body
            Customer loginRequest = ctx.bodyAsClass(Customer.class);
    
            String email = loginRequest.getEmail();
            String password = loginRequest.getPasswordHash(); // Password field in JSON
    
            String token = customerService.loginCustomer(email, password);
    
            if (token != null) {
                ctx.status(200).json(Map.of("token", token));
            } else {
                ctx.status(401).json(Map.of("error", "Invalid email or password"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }
    

    // ✅ Customer Registration Handler (New Account)
    public void registerHandler(Context ctx) {
        try {
            Customer customer = ctx.bodyAsClass(Customer.class);  // ✅ Read JSON correctly
            logger.info("Received Customer Registration: {}", customer);

            if (customer.getUsername() == null || customer.getEmail() == null) {
                ctx.status(400).json(Map.of("error", "Username and email are required"));
                return;
            }

            Customer newCustomer = customerService.registerCustomer(
                    customer.getUsername(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getPasswordHash(),  // Password should be hashed in the service layer
                    customer.getAddress()
            );

            if (newCustomer != null) {
                ctx.status(201).json(Map.of("customerId", newCustomer.getCustomerId(), "message", "Account created successfully"));
            } else {
                ctx.status(400).json(Map.of("error", "Email already exists"));
            }
        } catch (Exception e) {
            logger.error("Error registering customer", e);
            ctx.status(500).json(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }


    // ✅ Set Password for Existing Customer
    public void setPasswordHandler(Context ctx) {
        int customerId = Integer.parseInt(ctx.formParam("customerId"));
        String password = ctx.formParam("password");

        try {
            boolean success = customerService.setPassword(customerId, password);
            if (success) {
                ctx.status(200).json(Map.of("message", "Password updated successfully"));
            } else {
                ctx.status(400).json(Map.of("error", "Failed to update password"));
            }
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Internal Server Error", "message", e.getMessage()));
        }
    }

    public void getCustomerProfile(Context ctx) {
    String token = ctx.header("Authorization");
    if (token == null || !token.startsWith("Bearer ")) {
        ctx.status(401).json(Map.of("error", "Unauthorized"));
        return;
    }

    try {
        token = token.replace("Bearer ", "");
        int customerId = JwtUtil.getAccountIdFromToken(token);
        Customer customer = customerService.getCustomerById(customerId);

        if (customer != null) {
            ctx.json(customer);
        } else {
            ctx.status(404).json(Map.of("error", "Customer not found"));
        }
    } catch (Exception e) {
        ctx.status(500).json(Map.of("error", "Internal Server Error", "message", e.getMessage()));
    }
}

}


