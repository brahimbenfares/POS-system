package com.walid.backend.Service;

import com.walid.backend.Model.Product;
import com.walid.backend.Model.ProductWithPromotion;
import com.walid.backend.DAO.ProductDAO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product addProduct(Product product) {
        return productDAO.save(product);
    }

    // Delete this method if no file upload is needed
    public String uploadProductPicture(String productName, byte[] fileData, String originalFilename) {
        logger.warn("File upload is not supported as cloud storage is removed.");
        return "Upload not supported";
    }

    public void deleteProduct(int productId) {
        productDAO.softDeleteProduct(productId); // Use soft delete
    }

    public boolean updateProduct(Product product) {
        return productDAO.updateProduct(product);  // Return the result of the DAO update operation
    }
    

    public List<Product> findAllProducts() throws SQLException {
        return productDAO.findAllActive();
    }

    public List<ProductWithPromotion> findAllProductsWithPromotions() throws SQLException {
        return productDAO.findAllWithPromotions();
    }
    

 


    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.getLowStockProducts(threshold);
    }

    public List<Product> getTopSellingProducts(int limit) {
        return productDAO.getTopSellingProducts(limit);
    }

    public boolean adjustProductStock(int productId, int adjustment) {
        Product product = productDAO.findById(productId);
        if (product != null) {
            int newQuantity = product.getQuantity() + adjustment;
            product.setQuantity(newQuantity);
            productDAO.updateProduct(product);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getMostSoldProducts(LocalDate startDate, LocalDate endDate) throws SQLException {
        return productDAO.getMostSoldProducts(startDate, endDate);
    }

    public List<Map<String, Object>> getMostProfitableProducts(LocalDate startDate, LocalDate endDate) throws SQLException {
        return productDAO.getMostProfitableProducts(startDate, endDate);
    }

    public List<Map<String, Object>> getMostViewedProducts(LocalDate startDate, LocalDate endDate) throws SQLException {
        return productDAO.getMostViewedProducts(startDate, endDate);
    }

    public boolean deactivateProduct(int productId) {
        Product product = productDAO.findById(productId);
        if (product != null) {
            product.setActive(false);
            productDAO.updateProduct(product);
            return true;
        }
        return false;
    }

    public Product getProductById(int productId, boolean includeInactive) {
        return productDAO.findById(productId);
    }

    public boolean reactivateProduct(int productId) {
        Product product = getProductById(productId, true);
        if (product != null) {
            product.setActive(true);
            return productDAO.updateProduct(product);
        }
        return false;
    }

    public boolean bulkUpdateProducts(List<Product> products) {
        boolean allUpdated = true;
        for (Product product : products) {
            boolean updateResult = productDAO.updateProduct(product);
            if (!updateResult) {
                allUpdated = false;
                logger.error("Failed to update product with ID: " + product.getId());
            }
        }
        return allUpdated;
    }

    public List<Product> findAllActiveProducts(boolean isAdmin) throws SQLException {
        return productDAO.findAllActive();
    }

    public List<Integer> getAllStoreNumbers() throws SQLException {
        return productDAO.findAllStoreNumbers();
    }
    
    public List<Product> getLowStockItems(Integer storeNumber) throws SQLException {
        return productDAO.findLowStockItems(storeNumber);
    }
    
}
