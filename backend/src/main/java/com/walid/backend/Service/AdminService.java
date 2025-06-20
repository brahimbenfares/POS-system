package com.walid.backend.Service;

import com.walid.backend.Model.Admin;
//import com.walid.backend.Model.AnalyticsEvent;
//import com.walid.backend.Model.CustomerFeedback;
//import com.walid.backend.Model.InternalMessage;
//import com.walid.backend.Model.InventoryAlert;
import com.walid.backend.Model.AdminDTO;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.backend.DAO.AdminDAO;
import com.walid.backend.Model.JwtUtil;
import com.walid.backend.Model.Promotion;
import com.walid.backend.Model.PromotionDTO;

public class AdminService {

    private AdminDAO adminDAO;
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public Admin getAdminLogin(String username, String password) {
        logger.info("Fetching admin with username: {}", username);
        Admin admin = adminDAO.getAdminByLogin(username, password);
        if (admin != null) {
            admin.setPasswordHash(null);  // Do not return the password hash
        }
        return admin;
    }

    public Admin createAdmin(AdminDTO adminDTO) {
        // Hash the password once
        String hashedPassword = BCrypt.hashpw(adminDTO.getPassword(), BCrypt.gensalt());

        // Delegate to DAO to save admin
        return adminDAO.saveAdmin(adminDTO.getUsername(), hashedPassword, adminDTO.getEmail(), adminDTO.getRoleName(), adminDTO.isActive());
    }


    public Admin getAdminByUsername(String username) {
        try {
            Admin admin = adminDAO.getAdminByUsername(username);
            if (admin != null) {
                admin.setPasswordHash(null);  // Remove password hash before returning admin
            }
            return admin;
        } catch (Exception e) {
            logger.error("Error retrieving admin by username: ", e);
            return null;
        }
    }

    public boolean doesAdminExist(String username) {
        return adminDAO.doesAdminExist(username);
    }

    public boolean updateAdminPermissions(String username, String role, boolean isActive) {
        return adminDAO.updateAdminRole(username, role, isActive);
    }
    

    public boolean deleteAdmin(String username) {
        return adminDAO.deleteAdmin(username);
    }

    public String generateAdminToken(String username) {
        return JwtUtil.generateToken(username, 0);  // Ensure the token is generated securely
    }

    public boolean validateToken(String token) {
        return JwtUtil.validateToken(token);  // Ensure token validation is secure
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = adminDAO.getAllAdmins();
        for (Admin admin : admins) {
            admin.setPasswordHash(null);  // Remove password hash before returning list
        }
        return admins;
    }



    public boolean deleteInventoryAlert(int alertId) throws SQLException {
        return adminDAO.deleteInventoryAlert(alertId);
    }


    public Admin getAdminById(int adminId) {
        try {
            Admin admin = adminDAO.getAdminById(adminId);
            if (admin != null) {
                admin.setPasswordHash(null);  // Remove password hash before returning admin
            }
            return admin;
        } catch (Exception e) {
            logger.error("Error retrieving admin by ID from service layer: ", e);
            return null;
        }
    }


    public void linkPromotionToProducts(int promoId, List<Integer> productIds) throws SQLException {
        adminDAO.linkPromotionToProducts(promoId, productIds);
    }
    
    // Create promotion and link to products
    public boolean createPromotionWithProducts(PromotionDTO promotionDTO) {
        try {
            int promoId = adminDAO.createPromotion(promotionDTO);
            if (promoId != -1) {  // Promotion created successfully
                boolean linked = adminDAO.linkPromotionToProducts(promoId, promotionDTO.getProductIds());
                if (linked) {
                    logger.info("Promotion ID {} created and linked to products successfully.", promoId);
                    return true;
                } else {
                    logger.error("Promotion ID {} created but failed to link to products.", promoId);
                    return false;
                }
            } else {
                logger.error("Failed to create promotion.");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error creating promotion with products: ", e);
            return false;
        }
    }

    // Update promotion
    public boolean updatePromotion(Promotion promotion) {
        try {
            boolean updated = adminDAO.updatePromotion(promotion);
            if (updated) {
                logger.info("Promotion ID {} updated successfully.", promotion.getPromoId());
            } else {
                logger.warn("No promotion found with ID {} to update.", promotion.getPromoId());
            }
            return updated;
        } catch (SQLException e) {
            logger.error("Error updating promotion: ", e);
            return false;
        }
    }

    // Deactivate promotion
    public boolean deletePromotion(int promoId) {
        try {
            boolean deactivated = adminDAO.deletePromotion(promoId);
            if (deactivated) {
                logger.info("Promotion ID {} deactivated successfully.", promoId);
            } else {
                logger.warn("No promotion found with ID {} to deactivate.", promoId);
            }
            return deactivated;
        } catch (SQLException e) {
            logger.error("Error deactivating promotion: ", e);
            return false;
        }
    }

    public List<Promotion> getAllPromotions() {
    try {
        return adminDAO.getAllPromotions();
    } catch (SQLException e) {
        logger.error("Error fetching promotions: ", e);
        return Collections.emptyList();
    }
}


public Promotion getPromotionById(int promoId) {
    try {
        return adminDAO.getPromotionById(promoId);
    } catch (SQLException e) {
        logger.error("Error fetching promotion by ID: ", e);
        return null;
    }
}

    
}
