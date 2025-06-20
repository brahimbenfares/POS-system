package com.walid.backend.Controller;

import com.walid.backend.Service.ProductService;
//import com.walid.backend.Model.AdminJwtUtil;
import com.walid.backend.Model.Product;
import com.walid.backend.Model.ProductDTO;
import com.walid.backend.Model.ProductWithPromotion;

import io.javalin.http.Context;
//import io.javalin.http.UploadedFile;
//import jakarta.servlet.MultipartConfigElement;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.walid.backend.Service.AdminService;

import java.math.BigDecimal;
//import java.sql.SQLException;
//import java.io.IOException;
//import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

//import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService, AdminService adminService) {
        this.productService = productService;
    }

        // Add Product Handler
   /* public void addProductHandler(Context ctx) throws NumberFormatException {
        if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }
        try {
            // Parse JSON body into ProductDTO
            ProductDTO productDTO = ctx.bodyAsClass(ProductDTO.class);

            // Convert ProductDTO to Product entity
            Product product = convertDTOToProduct(productDTO);

            // Add product via service
            Product savedProduct = productService.addProduct(product);

            if (savedProduct != null && savedProduct.getId() != 0) {
                ctx.status(201).json(savedProduct);
            } else {
                ctx.status(500).result("Failed to create product");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            ctx.status(400).json("Validation error: " + e.getMessage());
        } catch (ParseException e) {
            logger.error("Invalid number or date format", e);
            ctx.status(400).json("Invalid number or date format: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding product", e);
            ctx.status(500).json("Server error: " + e.getMessage());
        }
    }

    // Helper method to convert ProductDTO to Product
    private Product convertDTOToProduct(ProductDTO dto) throws ParseException {
        Product product = new Product();
        if (dto.getId() != null) {
            product.setId(dto.getId());
        }
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setSalesPrice(dto.getPrice());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        product.setVendor(dto.getVendor());
    
        // Set purchaseDate as current timestamp
        product.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
    
        // Parse vendorPurchaseDate
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date parsedDate = dateFormat.parse(dto.getVendorPurchaseDate());
        product.setVendorPurchaseDate(new Timestamp(parsedDate.getTime()));
    
        product.setActive(true); // Default to active
    
        return product;
    }*/


    // Add Product Handler
    public void addProductHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            // Parse JSON body into ProductDTO
            ProductDTO productDTO = ctx.bodyAsClass(ProductDTO.class);

            // Validate ProductDTO
            validateProductDTO(productDTO, false); // false indicates creation

            // Convert ProductDTO to Product entity
            Product product = convertDTOToProduct(productDTO);

            // Add product via service
            Product savedProduct = productService.addProduct(product);

            if (savedProduct != null && savedProduct.getId() != 0) {
                ctx.status(201).json(savedProduct);
            } else {
                ctx.status(500).result("Failed to create product");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (ParseException e) {
            logger.error("Invalid date format", e);
            ctx.status(400).json(Map.of("error", "Invalid date format: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding product", e);
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    // Validate ProductDTO
    private void validateProductDTO(ProductDTO dto, boolean isUpdate) {
        // For creation, name, purchasePrice, price, quantity, vendor, vendorPurchaseDate are required
        List<String> missingFields = new ArrayList<>();
    
        if (isUpdate && (dto.getId() == null)) {
            missingFields.add("id");
        }
    
        if (dto.getName() == null || dto.getName().isEmpty()) {
            missingFields.add("name");
        }
        if (dto.getPurchasePrice() == null) {
            missingFields.add("purchasePrice");
        }
        if (dto.getPrice() == null) {
            missingFields.add("price");
        }
        if (dto.getQuantity() <= 0) {
            missingFields.add("quantity");
        }
        if (dto.getVendor() == null || dto.getVendor().isEmpty()) {
            missingFields.add("vendor");
        }
        if (dto.getStoreNumber() == null) {
            missingFields.add("storeNumber");
        }
        
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields: " + String.join(", ", missingFields));
        }
    
        // Additional validations can be added here (e.g., price > 0)
        if (dto.getPrice() != null && dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sale price must be greater than 0.");
        }
        if (dto.getPurchasePrice() != null && dto.getPurchasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Purchase price must be greater than 0.");
        }
    }
    

    // Convert DTO to Entity
    // Convert DTO to Entity
    private Product convertDTOToProduct(ProductDTO dto) throws ParseException {
        Product product = new Product();
    
        // If it's an update operation, set the ID
        if (dto.getId() != null) {
            product.setId(dto.getId());
        }
    
        // Set barcode if provided; else, leave it as null
        if (dto.getBarcode() != null && !dto.getBarcode().isEmpty()) {
            product.setBarcode(dto.getBarcode());
        } else {
            product.setBarcode(null); // Explicitly set to null or handle as per your requirements
        }
    
        // Set other fields
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setSalesPrice(dto.getPrice());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setQuantity(dto.getQuantity());
        product.setCategory(dto.getCategory());
        product.setVendor(dto.getVendor());
    
        // Set purchaseDate as current timestamp for creation; for updates, it might not change
        if (dto.getId() == null) { // Only set for new products
            product.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
        }
    
        // Parse vendorPurchaseDate
        @SuppressWarnings("unused")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // Inside convertDTOToProduct method
        product.setStoreNumber(dto.getStoreNumber());
    
        // Set active status based on 'is_active' field
        if (dto.getIsActive() != null) {
            product.setActive(dto.getIsActive());
        } else {
            product.setActive(true); // Default to active if not specified
        }
    
        return product;
    }
    


    // Check Authentication
    /*private boolean isAuthenticated(Context ctx) {
        String token = ctx.cookie("adminToken");
        if (token == null) {
            return false;
        }
        return AdminJwtUtil.validateToken(token); // Correct method
    }*/

    // Delete Product Handler
    public void deleteProductHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            productService.deleteProduct(productId);
            ctx.status(200).result("Product successfully deleted");
        } catch (NumberFormatException e) {
            logger.error("Invalid product ID format", e);
            ctx.status(400).json(Map.of("error", "Invalid product ID format"));
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            ctx.status(500).json(Map.of("error", "Error deleting product: " + e.getMessage()));
        }
    }

    // Update Product Handler
    public void updateProductHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).json(Map.of("error", "Unauthorized"));
            return;
        }*/
        try {
            // Parse JSON body into ProductDTO
            ProductDTO productDTO = ctx.bodyAsClass(ProductDTO.class);

            // Validate ProductDTO
            validateProductDTO(productDTO, true); // true indicates update

            // Convert ProductDTO to Product entity
            Product productToUpdate = convertDTOToProduct(productDTO);

            // Update product via service
            boolean updated = productService.updateProduct(productToUpdate);
            if (updated) {
                ctx.status(200).json(Map.of("message", "Product successfully updated"));
            } else {
                ctx.status(500).json(Map.of("error", "Failed to update product"));
            }
        } catch (IllegalArgumentException e) {
            logger.error("Validation error: ", e);
            ctx.status(400).json(Map.of("error", e.getMessage()));
        } catch (ParseException e) {
            logger.error("Invalid date format", e);
            ctx.status(400).json(Map.of("error", "Invalid date format: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Error updating product", e);
            ctx.status(500).json(Map.of("error", "Server error: " + e.getMessage()));
        }
    }

    // Get Product by ID Handler
    public void getProductByIdHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            boolean includeInactive = Boolean.parseBoolean(ctx.queryParam("includeInactive"));

            Product product = productService.getProductById(productId, includeInactive);
            if (product != null) {
                ctx.json(product);
            } else {
                ctx.status(404).json(Map.of("error", "Product not found"));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid product ID format", e);
            ctx.status(400).json(Map.of("error", "Invalid product ID format"));
        } catch (Exception e) {
            logger.error("Error fetching product by ID", e);
            ctx.status(500).json(Map.of("error", "Error fetching product: " + e.getMessage()));
        }
    }

    // Get All Products Handler
    public void getAllProductsHandler(Context ctx) {
        try {
            List<Product> products = productService.findAllProducts();
            ctx.json(products);
        } catch (Exception e) {
            logger.error("Error fetching products", e);
            ctx.status(500).json(Map.of("error", "Error fetching products: " + e.getMessage()));
        }
    }

    // Get All Products with Promotions Handler
    public void getAllProductsWithPromotionsHandler(Context ctx) {
        try {
            List<ProductWithPromotion> products = productService.findAllProductsWithPromotions();
            ctx.json(products);
        } catch (Exception e) {
            logger.error("Error fetching products with promotions", e);
            ctx.status(500).json(Map.of("error", "Error fetching products with promotions: " + e.getMessage()));
        }
    }

    // Get Most Sold Products Handler
    public void getMostSoldProductsHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            String startDateStr = ctx.queryParam("startDate");
            String endDateStr = ctx.queryParam("endDate");

            if (startDateStr == null || endDateStr == null) {
                ctx.status(400).json(Map.of("error", "startDate and endDate query parameters are required."));
                return;
            }

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            List<Map<String, Object>> products = productService.getMostSoldProducts(startDate, endDate);
            ctx.json(Map.of("products", products));
        } catch (DateTimeParseException e) {
            ctx.status(400).json(Map.of("error", "Invalid date format. Use YYYY-MM-DD."));
        } catch (Exception e) {
            logger.error("Error retrieving most sold products", e);
            ctx.status(500).json(Map.of("error", e.getMessage()));
        }
    }

    // Deactivate Product Handler
    public void deactivateProductHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            boolean result = productService.deactivateProduct(productId);
            if (result) {
                ctx.status(200).json(Map.of("message", "Product deactivated successfully"));
            } else {
                ctx.status(404).json(Map.of("error", "Product not found"));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid product ID format", e);
            ctx.status(400).json(Map.of("error", "Invalid product ID format"));
        } catch (Exception e) {
            logger.error("Error deactivating product", e);
            ctx.status(500).json(Map.of("error", "Error deactivating product: " + e.getMessage()));
        }
    }

    // Reactivate Product Handler
    public void reactivateProductHandler(Context ctx) {
        /*if (!isAuthenticated(ctx)) {
            ctx.status(401).result("Unauthorized");
            return;
        }*/
        try {
            int productId = Integer.parseInt(ctx.pathParam("productId"));
            boolean result = productService.reactivateProduct(productId);
            if (result) {
                ctx.status(200).json(Map.of("message", "Product reactivated successfully"));
            } else {
                ctx.status(404).json(Map.of("error", "Product not found"));
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid product ID format", e);
            ctx.status(400).json(Map.of("error", "Invalid product ID format"));
        } catch (Exception e) {
            logger.error("Error reactivating product", e);
            ctx.status(500).json(Map.of("error", "Error reactivating product: " + e.getMessage()));
        }
    }


    // Get Store Numbers Handler
public void getStoreNumbersHandler(Context ctx) {
    /*if (!isAuthenticated(ctx)) {
        ctx.status(401).result("Unauthorized");
        return;
    }*/
    try {
        List<Integer> storeNumbers = productService.getAllStoreNumbers();
        ctx.json(storeNumbers);
    } catch (Exception e) {
        logger.error("Error fetching store numbers", e);
        ctx.status(500).json(Map.of("error", "Error fetching store numbers: " + e.getMessage()));
    }


}
// Get Low Stock Items Handler
public void getLowStockItemsHandler(Context ctx) {
    /*if (!isAuthenticated(ctx)) {
        ctx.status(401).result("Unauthorized");
        return;
    }*/
    try {
        String storeNumberParam = ctx.queryParam("storeNumber");
        Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;

        List<Product> lowStockItems = productService.getLowStockItems(storeNumber);
        ctx.json(lowStockItems);
    } catch (Exception e) {
        logger.error("Error fetching low stock items", e);
        ctx.status(500).json(Map.of("error", "Error fetching low stock items: " + e.getMessage()));
    }
}

}
