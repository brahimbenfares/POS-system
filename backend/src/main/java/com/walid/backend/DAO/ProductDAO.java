package com.walid.backend.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.walid.backend.Model.Product;
import com.walid.backend.Model.ProductWithPromotion;

public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    

    public Product save(Product product) {
        // Insert statement in save() method (ProductDAO.java)
String sqlWithBarcode = """
    INSERT INTO products (
        barcode, product_name, purchase_date, purchase_price, sales_price, vendor, 
        store_number, description, quantity, category, is_active
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""";

String sqlWithoutBarcode = """
    INSERT INTO products (
        product_name, purchase_date, purchase_price, sales_price, vendor, 
        store_number, description, quantity, category, is_active
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
""";

    
        String sql = product.getBarcode() != null && !product.getBarcode().isEmpty() ? sqlWithBarcode : sqlWithoutBarcode;
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    
            int paramIndex = 1;
    
            if (product.getBarcode() != null && !product.getBarcode().isEmpty()) {
                stmt.setString(paramIndex++, product.getBarcode());
            }
    
            stmt.setString(paramIndex++, product.getName());
            stmt.setTimestamp(paramIndex++, product.getPurchaseDate());
            stmt.setBigDecimal(paramIndex++, product.getPurchasePrice());
            stmt.setBigDecimal(paramIndex++, product.getSalesPrice());
            stmt.setString(paramIndex++, product.getVendor());
            stmt.setInt(paramIndex++, product.getStoreNumber());
            stmt.setString(paramIndex++, product.getDescription());
            stmt.setInt(paramIndex++, product.getQuantity());
            stmt.setString(paramIndex++, product.getCategory());
            stmt.setBoolean(paramIndex++, product.isActive());
    
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
    
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating product failed, no ID obtained.");
                }
            }
        } catch (SQLException ex) {
            logger.error("Error saving product: ", ex);
            return null; // Indicate failure
        }
        return product;
    }
    

    // Extract product from the result set
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("product_id"));
        product.setBarcode(rs.getString("barcode")); // Set barcode
        product.setName(rs.getString("product_name"));
        product.setPurchaseDate(rs.getTimestamp("purchase_date"));
        product.setPurchasePrice(rs.getBigDecimal("purchase_price"));
        product.setSalesPrice(rs.getBigDecimal("sales_price"));
        product.setVendor(rs.getString("vendor"));
        product.setStoreNumber(rs.getInt("store_number"));
        product.setDescription(rs.getString("description"));
        product.setQuantity(rs.getInt("quantity"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setCategory(rs.getString("category"));
        product.setActive(rs.getBoolean("is_active"));
        return product;
    }


        // Find all active products
        public List<Product> findAllActive() {
            List<Product> products = new ArrayList<>();
            String sql = "SELECT * FROM products WHERE is_active = TRUE ORDER BY created_at DESC";
    
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
    
                while (rs.next()) {
                    products.add(extractProductFromResultSet(rs));
                }
            } catch (SQLException ex) {
                logger.error("Error retrieving active products: ", ex);
            }
            return products;
        }
    


   public List<ProductWithPromotion> findAllWithPromotions() {
    List<ProductWithPromotion> productsWithPromotions = new ArrayList<>();
    String sql = "SELECT p.product_id, p.product_name, p.sales_price, p.quantity, " +
                 "pr.promo_id, pr.discount_percentage, pr.description " +
                 "FROM products p " +
                 "LEFT JOIN product_promotions pp ON p.product_id = pp.product_id " +
                 "LEFT JOIN promotions pr ON pp.promo_id = pr.promo_id " +
                 "WHERE p.is_active = TRUE " +
                 "AND (pr.active = 1 OR pr.promo_id IS NULL) " +
                 "ORDER BY p.created_at DESC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            ProductWithPromotion product = new ProductWithPromotion();
            product.setProductId(rs.getInt("product_id"));
            product.setProductName(rs.getString("product_name"));
            product.setSalesPrice(rs.getBigDecimal("sales_price"));
            product.setQuantity(rs.getInt("quantity"));

            // Check if promotion exists and set promotion fields
            int promoId = rs.getInt("promo_id");
            if (!rs.wasNull()) {
                product.setPromoId(promoId);
                product.setDiscountPercentage(rs.getDouble("discount_percentage"));
                product.setPromotionDescription(rs.getString("description"));
            }

            productsWithPromotions.add(product);
        }
    } catch (SQLException ex) {
        logger.error("Error retrieving products with promotions: ", ex);
    }
    return productsWithPromotions;
}

    
    // Find product by ID
    public Product findById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractProductFromResultSet(rs);
                }
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving product by ID: ", ex);
        }
        return null;
    }

    // Retrieve the most sold products within a date range
    public List<Map<String, Object>> getMostSoldProducts(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT p.product_name, SUM(oi.quantity) AS total_sold 
            FROM order_items oi
            JOIN products p ON oi.product_id = p.product_id
            WHERE oi.order_date BETWEEN ? AND ?
            GROUP BY p.product_name
            ORDER BY total_sold DESC
        """;

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("product_name", rs.getString("product_name"));
                record.put("total_sold", rs.getInt("total_sold"));
                result.add(record);
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving most sold products: ", ex);
        }
        return result;
    }

    // Retrieve the most profitable products within a date range
    public List<Map<String, Object>> getMostProfitableProducts(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT p.product_name, 
                   SUM((oi.quantity * p.sales_price) - (oi.quantity * p.purchase_price)) AS total_profit
            FROM order_items oi
            JOIN products p ON oi.product_id = p.product_id
            WHERE oi.order_date BETWEEN ? AND ?
            GROUP BY p.product_name
            ORDER BY total_profit DESC
        """;

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("product_name", rs.getString("product_name"));
                record.put("total_profit", rs.getBigDecimal("total_profit"));
                result.add(record);
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving most profitable products: ", ex);
        }
        return result;
    }

    // Retrieve the most viewed products within a date range
    public List<Map<String, Object>> getMostViewedProducts(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT product_name, view_count 
            FROM products
            WHERE created_at BETWEEN ? AND ?
            ORDER BY view_count DESC
        """;

        List<Map<String, Object>> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("product_name", rs.getString("product_name"));
                record.put("view_count", rs.getInt("view_count"));
                result.add(record);
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving most viewed products: ", ex);
        }
        return result;
    }

    // Update a product
        // Update Product
        public boolean updateProduct(Product product) {
            String sql = """
                UPDATE products 
                SET product_name = ?, barcode = ?, quantity = ?, purchase_date = ?, 
                    purchase_price = ?, sales_price = ?, vendor = ?, 
                    store_number = ?, description = ?, category = ?, is_active = ?
                WHERE product_id = ?
            """;
            
        
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
        
                // Set product name
                stmt.setString(1, product.getName());
        
                // Set barcode (can be null)
                if (product.getBarcode() != null && !product.getBarcode().isEmpty()) {
                    stmt.setString(2, product.getBarcode());
                } else {
                    stmt.setNull(2, Types.VARCHAR);
                }
        
                // Set quantity
                stmt.setInt(3, product.getQuantity());
        
                // Handle purchase date: Use existing if not provided
                Timestamp purchaseDate = product.getPurchaseDate() != null
                    ? product.getPurchaseDate()
                    : getCurrentPurchaseDateFromDB(product.getId());
                stmt.setTimestamp(4, purchaseDate);
        
                // Set purchase price and sales price
                stmt.setBigDecimal(5, product.getPurchasePrice());
                stmt.setBigDecimal(6, product.getSalesPrice());
        
                // Set vendor
                stmt.setString(7, product.getVendor());
        
                // Set store number
                if (product.getStoreNumber() != null) {
                    stmt.setInt(8, product.getStoreNumber());
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }
        
                // Set description, category, and active status
                stmt.setString(9, product.getDescription());
                stmt.setString(10, product.getCategory());
                stmt.setBoolean(11, product.isActive());
        
                // Set product ID
                stmt.setInt(12, product.getId());
        
                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
        
            } catch (SQLException ex) {
                logger.error("Error updating product: " + ex.getMessage(), ex);
                return false;
            }
        }
        
        private Timestamp getCurrentPurchaseDateFromDB(int productId) {
            String query = "SELECT purchase_date FROM products WHERE product_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    return rs.getTimestamp("purchase_date");
                } else {
                    throw new SQLException("Product not found with ID: " + productId);
                }
            } catch (SQLException ex) {
                logger.error("Error fetching purchase date: " + ex.getMessage(), ex);
                throw new RuntimeException("Failed to fetch purchase date");
            }
        }
        
        
        
    

    // Soft delete product by setting quantity to 0
    public void softDeleteProduct(int productId) {
        String sql = "UPDATE products SET is_active = 0 WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Error deleting product: ", ex);
        }
    }

    // Retrieve low stock products below a given threshold
    public List<Product> getLowStockProducts(int threshold) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity <= ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProductFromResultSet(rs));
                }
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving low stock products: ", ex);
        }

        return products;
    }

    // Retrieve top-selling products with a limit
    public List<Product> getTopSellingProducts(int limit) {
        List<Product> products = new ArrayList<>();
        String sql = """
            SELECT p.*, SUM(oi.quantity) AS total_sold 
            FROM products p 
            JOIN order_items oi ON p.product_id = oi.product_id 
            GROUP BY p.product_id 
            ORDER BY total_sold DESC 
            LIMIT ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(extractProductFromResultSet(rs));
                }
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving top-selling products: ", ex);
        }

        return products;

    }

        // Find all unique store numbers
public List<Integer> findAllStoreNumbers() throws SQLException {
    List<Integer> storeNumbers = new ArrayList<>();
    String sql = "SELECT DISTINCT store_number FROM products WHERE store_number IS NOT NULL";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            storeNumbers.add(rs.getInt("store_number"));
        }
    } catch (SQLException ex) {
        logger.error("Error retrieving store numbers: ", ex);
    }
    return storeNumbers;
}

    




    public List<Product> findLowStockItems(Integer storeNumber) throws SQLException {
        List<Product> lowStockItems = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE quantity < 15 AND is_active = TRUE";
    
        if (storeNumber != null) {
            sql += " AND store_number = ?";
        }
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            if (storeNumber != null) {
                stmt.setInt(1, storeNumber);
            }
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    lowStockItems.add(product);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving low stock items: ", e);
        }
        return lowStockItems;
    }
    
}
