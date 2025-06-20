package com.walid.backend.Controller;

import io.javalin.http.Context;
import com.walid.backend.Model.Admin;
import com.walid.backend.Model.AdminDTO;
import com.walid.backend.Service.AdminService;
import com.walid.backend.Model.AdminJwtUtil;
import com.walid.backend.Model.AdminLoginDTO;
//import com.walid.backend.Model.JwtUtil;
import com.walid.backend.Model.Promotion;
import com.walid.backend.Model.PromotionDTO;
import jakarta.servlet.http.Cookie;

import java.sql.SQLException;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminController {

    private final AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    
    
    public void adminLogin(Context ctx) {
        try {
            String username = null;
            String password = null;
    
            // Log the incoming request body for inspection
            logger.info("Request Body: {}", ctx.body());
    
            String contentType = ctx.contentType();
            if (contentType != null) {
                if (contentType.startsWith("application/x-www-form-urlencoded")) {
                    username = ctx.formParam("username");
                    password = ctx.formParam("password");
                } else if (contentType.startsWith("application/json")) {
                    // Handle JSON input using DTO
                    AdminLoginDTO loginRequest = ctx.bodyAsClass(AdminLoginDTO.class);
                    username = loginRequest.getUsername();
                    password = loginRequest.getPassword();
                }
            }
    
            // Log the extracted values
            logger.info("Received username: {}", username);
            logger.info("Received password: {}", password != null ? "[PROVIDED]" : "null");
    
            // Perform login validation
            Admin admin = adminService.getAdminLogin(username, password);
    
            if (admin != null) {
                // Generate JWT token
                String token = AdminJwtUtil.generateToken(username, admin.getId(), admin.getRoleName());
    
                Cookie cookie = new Cookie("adminToken", token);
                cookie.setPath("/");
                cookie.setMaxAge(7200); // 2 hours
                cookie.setSecure(false); // Set to true for HTTPS
                cookie.setHttpOnly(true); // Prevent JavaScript access
                ctx.res().addCookie(cookie);
    
                ctx.json(Map.of(
                    "username", admin.getUsername(),
                    "role", admin.getRoleName(),
                    "token", token
                )).status(200);
            } else {
                ctx.status(401).result("Unauthorized");
            }
        } catch (Exception e) {
            logger.error("Error during admin login", e);
            ctx.status(500).result("Internal Server Error");
        }
    }
    


    
        

    



    public void getAdminByIdHandler(Context ctx) {

        int adminId = Integer.parseInt(ctx.pathParam("adminId"));
        Admin admin = adminService.getAdminById(adminId);
        if (admin != null) {
            ctx.json(Map.of(
                "username", admin.getUsername(),
                "roleName", admin.getRoleName(),
                "id", admin.getId(),
                "email", admin.getEmail() // Only return what's necessary
            )).status(200);
        } else {
            ctx.status(404).result("Not Found");
        }
    }
    /*private boolean isAuthenticated(Context ctx) {
        String token = ctx.cookie("adminToken");
        if (token == null) {
            return false;
        }
        return AdminJwtUtil.validateToken(token); // Correct method
    }*/

    public void adminLogout(Context ctx) {
        try {
            ctx.removeCookie("adminToken", "/");
            ctx.status(200).result("Logged out successfully");
        } catch (Exception e) {
            logger.error("Error during logout", e);
            ctx.status(500).result("Internal Server Error");
        }
    }



public void getAllAdminsHandler(Context ctx) throws SQLException {
    /*if (!isAuthenticated(ctx)) {
        ctx.status(401).result("Unauthorized");
        return;
    }*/

    // Fetch the list of admins
    List<Admin> admins = adminService.getAllAdmins();

    // Map the admins to a simplified view (username, roleName, id, email)
    List<Map<String, Object>> adminList = admins.stream().map(admin -> {
        Map<String, Object> adminMap = new HashMap<>();
        adminMap.put("username", admin.getUsername());
        adminMap.put("roleName", admin.getRoleName());
        adminMap.put("id", admin.getId());
        adminMap.put("email", admin.getEmail());
        return adminMap;
    }).toList();

    // Log the data for debugging
    logger.info("Admins retrieved: " + adminList);

    // Set the Content-Type to application/json and return the response
    ctx.contentType("application/json");
    ctx.json(adminList).status(200);
}


    public void createAdmin(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            AdminDTO adminDTO = ctx.bodyAsClass(AdminDTO.class);

            // Validate input
            if (adminDTO.getUsername() == null || adminDTO.getUsername().trim().isEmpty()) {
                ctx.status(400).result("Username is required");
                return;
            }

            if (adminDTO.getPassword() == null || adminDTO.getPassword().trim().isEmpty()) {
                ctx.status(400).result("Password is required");
                return;
            }

            if (adminDTO.getEmail() == null || adminDTO.getEmail().trim().isEmpty()) {
                ctx.status(400).result("Email is required");
                return;
            }

            if (adminDTO.getRoleName() == null || adminDTO.getRoleName().trim().isEmpty()) {
                ctx.status(400).result("Role name is required");
                return;
            }

            // Delegate to service to create admin
            Admin createdAdmin = adminService.createAdmin(adminDTO);

            if (createdAdmin != null) {
                ctx.json(Map.of(
                    "adminId", createdAdmin.getId(),
                    "username", createdAdmin.getUsername(),
                    "roleName", createdAdmin.getRoleName(),
                    "email", createdAdmin.getEmail(),
                    "isActive", createdAdmin.isActive()
                )).status(201);
            } else {
                ctx.status(500).result("Failed to create admin");
            }
        } catch (Exception e) {
            logger.error("Error during admin creation", e);
            ctx.status(500).result("Internal Server Error");
        }
    }


    
    public void updatePermissions(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            AdminDTO adminDTO = ctx.bodyAsClass(AdminDTO.class); // Parse JSON to DTO
    
            // Enhanced logging for debugging
            logger.info("AdminDTO data - id: {}, username: {}, roleName: {}, isActive: {}",
                adminDTO.getId(), adminDTO.getUsername(), adminDTO.getRoleName(), adminDTO.isActive());
    
            if (adminDTO.getUsername() == null || adminDTO.getRoleName() == null) {
                ctx.status(400).json(Map.of("message", "Username and role are required"));
                return;
            }
    
            boolean updated = adminService.updateAdminPermissions(
                adminDTO.getUsername(), adminDTO.getRoleName(), adminDTO.isActive()
            );
    
            if (updated) {
                ctx.status(200).json(Map.of("message", "Updated"));
            } else {
                ctx.status(400).json(Map.of("message", "Bad Request"));
            }
        } catch (Exception e) {
            logger.error("Error during permission update", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }
    
    
    

    public void deleteAdmin(Context ctx) {
       /*  if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/

        try {
            String username = ctx.queryParam("username");
            if (adminService.deleteAdmin(username)) {
                ctx.status(200).json(Map.of("message", "Deleted"));
            } else {
                ctx.status(400).json(Map.of("message", "Bad Request"));
            }
        } catch (Exception e) {
            logger.error("Error during admin deletion", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }

    public void requireAuthentication(Context ctx) {

        String authHeader = ctx.header("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = adminService.validateToken(token);

            if (!isValid) {
                ctx.status(401).result("Unauthorized");
                redirectToLogin(ctx);
                return;
            }
        }
    }

    private void redirectToLogin(Context ctx) {
        ctx.redirect("https://localhost/admin_login.html");
    }

      
    
    
    
       // Handler to create a new promotion and link to products
       public void createPromotion(Context ctx) throws SQLException {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            PromotionDTO promotionDTO = ctx.bodyAsClass(PromotionDTO.class);
            
            // Validate date fields
            if (promotionDTO.getStartDate() == null || promotionDTO.getEndDate() == null) {
                ctx.status(400).json(Map.of("message", "Start date and end date are required"));
                return;
            }
            if (promotionDTO.getStartDate().isAfter(promotionDTO.getEndDate())) {
                ctx.status(400).json(Map.of("message", "Start date must be before end date"));
                return;
            }

            // Proceed with creation
            if (adminService.createPromotionWithProducts(promotionDTO)) {
                ctx.status(201).json(Map.of("message", "Promotion Created and Applied to Products"));
            } else {
                ctx.status(400).json(Map.of("message", "Failed to create promotion or link to products"));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error during promotion creation: ", e);
            ctx.status(400).json(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error during promotion creation", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }


    


    
    
        // Handler to update an existing promotion
        public void updatePromotion(Context ctx) throws SQLException {
            /*if (!isAuthenticated(ctx)) {
                ctx.status(401).result("Unauthorized");
                return;
            }*/
            try {
                PromotionDTO promotionDTO = ctx.bodyAsClass(PromotionDTO.class);  // Use PromotionDTO
        
                // Create a Promotion entity from the DTO
                Promotion promotion = new Promotion(
                        promotionDTO.getPromoId(),
                        promotionDTO.getDescription(),
                        promotionDTO.getStartDate(),
                        promotionDTO.getEndDate(),
                        promotionDTO.getDiscountPercentage(),
                        promotionDTO.isActive()
                );
        
                // Update promotion in the database
                if (adminService.updatePromotion(promotion)) {
                    // Link products to the promotion if productIds are provided
                    if (!promotionDTO.getProductIds().isEmpty()) {
                        adminService.linkPromotionToProducts(promotion.getPromoId(), promotionDTO.getProductIds());
                    }
                    ctx.status(200).json(Map.of("message", "Promotion Updated"));
                } else {
                    ctx.status(404).json(Map.of("message", "Promotion not found"));
                }
            } catch (Exception e) {
                logger.error("Unexpected error during promotion update", e);
                ctx.status(500).json(Map.of("message", "Internal Server Error"));
            }
        }
        
    
        // Handler to deactivate (delete) a promotion
        public void deletePromotion(Context ctx) throws SQLException {
            /*if (!isAuthenticated(ctx)) {
                ctx.status(401).result("Unauthorized");
                return;
            }*/
            try {
                int promoId = Integer.parseInt(ctx.pathParam("promo_id"));
                
                if (adminService.deletePromotion(promoId)) {
                    ctx.status(200).json(Map.of("message", "Promotion Deactivated"));
                } else {
                    ctx.status(404).json(Map.of("message", "Promotion not found"));
                }
            } catch (NumberFormatException e) {
                logger.error("Invalid promoId format: {}", ctx.pathParam("promoId"), e);
                ctx.status(400).json(Map.of("message", "Invalid promoId format"));
            } catch (Exception e) {
                logger.error("Unexpected error during promotion deletion", e);
                ctx.status(500).json(Map.of("message", "Internal Server Error"));
            }
        }
    


    

    public void deleteInventoryAlert(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/

        try {
            int alertId = Integer.parseInt(ctx.queryParam("alertId"));
            if (adminService.deleteInventoryAlert(alertId)) {
                ctx.status(200).json(Map.of("message", "Inventory Alert Deleted"));
            } else {
                ctx.status(400).json(Map.of("message", "Bad Request"));
            }
        } catch (Exception e) {
            logger.error("Error deleting inventory alert", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }

    public void getAllPromotions(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            List<Promotion> promotions = adminService.getAllPromotions();
            ctx.json(promotions).status(200);
        } catch (Exception e) {
            logger.error("Error fetching promotions", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }
    
    public void getPromotionById(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int promoId = Integer.parseInt(ctx.pathParam("promo_id"));
            Promotion promotion = adminService.getPromotionById(promoId);
            if (promotion != null) {
                ctx.json(promotion).status(200);
            } else {
                ctx.status(404).json(Map.of("message", "Promotion not found"));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid promoId format: {}", ctx.pathParam("promo_id"), e);
            ctx.status(400).json(Map.of("message", "Invalid promoId format"));
        } catch (Exception e) {
            logger.error("Error fetching promotion by ID", e);
            ctx.status(500).json(Map.of("message", "Internal Server Error"));
        }
    }
    
    
}
