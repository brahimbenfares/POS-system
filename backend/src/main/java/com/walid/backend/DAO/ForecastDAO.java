package com.walid.backend.DAO;

import com.walid.backend.Model.AISalesForecast;
import com.walid.backend.Model.SalesHistory;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ForecastDAO {
    private static final String DB_URL = "jdbc:mysql://127.0.0.2:3306/walid_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "theJunior@05";



      // *************************************//
     //              forcasting              //
    //**************************************//
    // Method to insert AI prediction into the database
    public static boolean saveForecast(AISalesForecast forecast) {
        String sql = "INSERT INTO ai_sales_forecast (product_id, forecast_date, predicted_sales, confidence_level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, forecast.getProductId());
            stmt.setDate(2, Date.valueOf(forecast.getForecastDate()));
            stmt.setDouble(3, forecast.getPredictedSales());
            stmt.setDouble(4, forecast.getConfidenceLevel());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to retrieve AI predictions for a specific product
    public static List<AISalesForecast> getForecastByProduct(int productId) {
        List<AISalesForecast> forecasts = new ArrayList<>();
        String sql = "SELECT * FROM ai_sales_forecast WHERE product_id = ? ORDER BY forecast_date ASC";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                forecasts.add(new AISalesForecast(
                        rs.getInt("forecast_id"),
                        rs.getInt("product_id"),
                        rs.getDate("forecast_date").toLocalDate(),
                        rs.getDouble("predicted_sales"),
                        rs.getDouble("confidence_level"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return forecasts;
    }



      //********************************************************//
     //      Export Sales Data from MySQL for AI Training      //
    //********************************************************//


    public static List<SalesHistory> getSalesHistory() {
        List<SalesHistory> salesData = new ArrayList<>();
        String sql = "SELECT o.order_date, oi.product_id, SUM(oi.quantity) AS total_sold " +
                     "FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "WHERE o.action = 'Confirm' " +
                     "GROUP BY o.order_date, oi.product_id " +
                     "ORDER BY o.order_date ASC";
    
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                salesData.add(new SalesHistory(
                        rs.getDate("order_date").toLocalDate(),
                        rs.getInt("product_id"),
                        rs.getInt("total_sold")
                ));
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesData;
    }
    


     //********************************************//
    //      Compare Forecasts with Actual Sales   //
   //********************************************//

public static List<Map<String, Object>> compareForecastsWithActualSales(int productId) {
    List<Map<String, Object>> comparisons = new ArrayList<>();
    String sql = """
        SELECT f.forecast_date, f.predicted_sales, 
               COALESCE(s.total_sold, 0) AS actual_sales
        FROM ai_sales_forecast f
        LEFT JOIN (
            SELECT o.order_date, oi.product_id, SUM(oi.quantity) AS total_sold
            FROM orders o
            JOIN order_items oi ON o.order_id = oi.order_id
            WHERE o.action = 'Confirm'
            GROUP BY o.order_date, oi.product_id
        ) s ON f.forecast_date = s.order_date AND f.product_id = s.product_id
        WHERE f.product_id = ?
        ORDER BY f.forecast_date ASC
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, productId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Map<String, Object> result = Map.of(
                "forecast_date", rs.getDate("forecast_date").toLocalDate().toString(), // ✅ Converts to "YYYY-MM-DD"
                "predicted_sales", rs.getDouble("predicted_sales"),
                "actual_sales", rs.getDouble("actual_sales")
            );

            comparisons.add(result);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return comparisons;
}


// Identify low-stock risks based on AI forecasts
// Identify low-stock risks based on AI forecasts
public List<Map<String, Object>> identifyStockRisks() {
    List<Map<String, Object>> risks = new ArrayList<>();
    String sql = """
        SELECT f.product_id, p.product_name, f.forecast_date, f.predicted_sales, p.quantity AS stock_quantity
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        WHERE f.forecast_date = CURDATE() AND f.predicted_sales > p.quantity
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> risk = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"), // ✅ Add product name
                "forecast_date", rs.getDate("forecast_date").toString(), 
                "predicted_sales", rs.getDouble("predicted_sales"),
                "stock_quantity", rs.getInt("stock_quantity")
            );
            risks.add(risk);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return risks;
}






  //*************************************************//
 //    ✅ AI-Powered Dynamic Reordering System     //
//*************************************************//

// ✅ Improved AI-Powered Dynamic Reordering System


public List<Map<String, Object>> getReorderRecommendations() {
    List<Map<String, Object>> recommendations = new ArrayList<>();
    String sql = """
        SELECT f.product_id, p.product_name, p.quantity AS stock_quantity, 
               SUM(f.predicted_sales) AS total_predicted_sales,
               CASE 
                   WHEN p.quantity = 0 THEN SUM(f.predicted_sales)  
                   WHEN SUM(f.predicted_sales) > p.quantity THEN SUM(f.predicted_sales) - p.quantity
                   ELSE 0  
               END AS reorder_quantity,
               (SELECT s.SupplierID 
                FROM supplier s 
                WHERE s.SupplierName = p.vendor 
                ORDER BY s.LastShipmentDate DESC 
                LIMIT 1) AS last_supplier_id,
               (SELECT s.SupplierName 
                FROM supplier s 
                WHERE s.SupplierName = p.vendor 
                ORDER BY s.LastShipmentDate DESC 
                LIMIT 1) AS last_supplier_name
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        WHERE f.forecast_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
        GROUP BY f.product_id, p.product_name, p.quantity
        HAVING reorder_quantity > 0;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> reorder = new HashMap<>();
            reorder.put("product_id", rs.getInt("product_id"));
            reorder.put("product_name", rs.getString("product_name"));
            reorder.put("stock_quantity", rs.getInt("stock_quantity"));
            reorder.put("predicted_sales", rs.getDouble("total_predicted_sales"));
            reorder.put("reorder_quantity", Math.max(1, Math.ceil(rs.getDouble("reorder_quantity"))));
            
            // Ensure we get only one supplier (most recent one)
            reorder.put("last_supplier_id", rs.getObject("last_supplier_id") != null ? rs.getInt("last_supplier_id") : null);
            reorder.put("last_supplier_name", rs.getString("last_supplier_name"));

            System.out.println("✅ AI Reorder Suggestion: " + reorder); // Debugging
            recommendations.add(reorder);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return recommendations;
}









   //**************************************************//
  //       ✅ AI-Based Demand Trends Analysis        //
 //*************************************************//

 public List<Map<String, Object>> getDemandTrends() {
    List<Map<String, Object>> trends = new ArrayList<>();
    String sql = """
        SELECT f.product_id, p.product_name, p.sales_price, 
               MONTH(f.forecast_date) AS month, AVG(f.predicted_sales) AS avg_predicted_sales
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        GROUP BY f.product_id, p.product_name, p.sales_price, MONTH(f.forecast_date)
        ORDER BY avg_predicted_sales DESC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> trend = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "sales_price", rs.getBigDecimal("sales_price"),
                "month", rs.getInt("month"),
                "avg_predicted_sales", rs.getBigDecimal("avg_predicted_sales")
            );
            trends.add(trend);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return trends;
}




     //**************************************************//
    // ✅ AI-Powered Auto-Pricing & Discounts          //
   //*************************************************//

public List<Map<String, Object>> getPriceSuggestions() {
    List<Map<String, Object>> priceSuggestions = new ArrayList<>();
    String sql = """
        SELECT f.product_id, p.sales_price, AVG(f.predicted_sales) AS avg_sales,
               COALESCE(SUM(ad.discount_percentage), 0) AS total_discounts
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        LEFT JOIN additional_discounts ad ON p.product_id = ad.order_item_id
        GROUP BY f.product_id, p.sales_price;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            double avgSales = rs.getDouble("avg_sales");
            double currentPrice = rs.getDouble("sales_price");
            double totalDiscounts = rs.getDouble("total_discounts");
            
            // Adjust price based on demand and discounts
            double newPrice;
            if (avgSales > 10) {
                newPrice = currentPrice * 1.1; // Increase price by 10% for high demand
            } else {
                newPrice = currentPrice * 0.9; // Decrease price by 10% for low demand
            }
            
            // Apply discounts if available
            newPrice -= (newPrice * (totalDiscounts / 100));

            Map<String, Object> suggestion = Map.of(
                "product_id", rs.getInt("product_id"),
                "current_price", currentPrice,
                "suggested_price", Math.round(newPrice * 100.0) / 100.0 // Round to 2 decimals
            );
            priceSuggestions.add(suggestion);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return priceSuggestions;
}




   //*******************************************//
  // ✅ AI-Generated Sales Forecast Reports    //
 //********************************************//

  public List<Map<String, Object>> getSalesForecastReport(String period) {
    List<Map<String, Object>> salesReport = new ArrayList<>();
    String sql = """
        SELECT f.product_id, p.product_name, 
               SUM(f.predicted_sales) AS total_predicted_sales,
               SUM(f.predicted_sales * p.sales_price) AS total_predicted_revenue,
               CASE 
                   WHEN ? = 'weekly' THEN WEEK(f.forecast_date)
                   WHEN ? = 'monthly' THEN MONTH(f.forecast_date)
                   ELSE NULL 
               END AS period_group
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        WHERE f.forecast_date >= CURDATE()
        GROUP BY f.product_id, period_group
        ORDER BY period_group, f.product_id;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, period);
        stmt.setString(2, period);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Map<String, Object> report = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "total_predicted_sales", rs.getDouble("total_predicted_sales"),
                "total_predicted_revenue", rs.getDouble("total_predicted_revenue"),
                "period_group", rs.getInt("period_group")
            );
            salesReport.add(report);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return salesReport;
}


//*************************************************//
// ✅ AI-Based Customer Purchase Behavior         //
//*************************************************//

/*public List<Map<String, Object>> getCustomerTrends() {
    List<Map<String, Object>> trends = new ArrayList<>();
    String sql = """
        SELECT oi.product_id, p.product_name, COUNT(oi.order_id) AS total_orders,
               SUM(oi.quantity) AS total_sold,
               HOUR(o.order_time) AS peak_hour
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN products p ON oi.product_id = p.product_id
        WHERE o.action = 'Confirm'
        GROUP BY oi.product_id, p.product_name, HOUR(o.order_time)
        ORDER BY total_sold DESC, peak_hour ASC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> trend = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "total_orders", rs.getInt("total_orders"),
                "total_sold", rs.getInt("total_sold"),
                "peak_hour", rs.getInt("peak_hour")
            );
            trends.add(trend);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return trends;
}
*/

public List<Map<String, Object>> getCustomerTrends(String filter) {
    List<Map<String, Object>> trends = new ArrayList<>();
    String dateCondition = "";

    // 🔥 Apply the date filter
    switch (filter) {
        case "daily":
            dateCondition = " AND o.order_date = CURDATE() ";
            break;
        case "weekly":
            dateCondition = " AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ";
            break;
        case "monthly":
            dateCondition = " AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ";
            break;
        default:
            dateCondition = ""; // No filter (fetch all)
    }

    String sql = """
        SELECT oi.product_id, p.product_name, COUNT(oi.order_id) AS total_orders,
               SUM(oi.quantity) AS total_sold, HOUR(o.order_time) AS peak_hour
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN products p ON oi.product_id = p.product_id
        WHERE o.action = 'Confirm' """ + dateCondition + """
        GROUP BY oi.product_id, p.product_name, HOUR(o.order_time)
        ORDER BY total_sold DESC, peak_hour ASC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> trend = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "total_orders", rs.getInt("total_orders"),
                "total_sold", rs.getInt("total_sold"),
                "peak_hour", rs.getInt("peak_hour")
            );
            trends.add(trend);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return trends;
}


//*************************************************//
// ✅ AI-Driven Stock Optimization & Waste Reduction //
//*************************************************//

public List<Map<String, Object>> getStockOptimization() {
    List<Map<String, Object>> optimizations = new ArrayList<>();
    String sql = """
        SELECT p.product_id, p.product_name, p.quantity,
               SUM(oi.quantity) AS total_sold, 
               (p.quantity - SUM(oi.quantity)) AS excess_stock
        FROM products p
        LEFT JOIN order_items oi ON p.product_id = oi.product_id
        LEFT JOIN orders o ON oi.order_id = o.order_id AND o.action = 'Confirm'
        GROUP BY p.product_id, p.product_name, p.quantity
        HAVING excess_stock > (total_sold * 0.5)  -- Flag products with high unsold stock
        ORDER BY excess_stock DESC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> optimization = Map.of(
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "current_stock", rs.getInt("quantity"),
                "total_sold", rs.getInt("total_sold"),
                "excess_stock", rs.getInt("excess_stock")
            );
            optimizations.add(optimization);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return optimizations;
}


  //*************************************************************//
 // ✅ AI Auto-Replenishment System with Supplier Integration  //
//************************************************************//
public List<Map<String, Object>> getAutoReplenishment() {
    List<Map<String, Object>> replenishments = new ArrayList<>();
    String sql = """
        SELECT 
            p.product_id, 
            p.product_name, 
            p.quantity, 
            MAX(f.predicted_sales) AS predicted_sales, 
            COALESCE(MAX(s.SupplierID), NULL) AS supplier_id, 
            COALESCE(MAX(s.SupplierName), p.vendor) AS supplier_name,  -- Use vendor if no supplier
            CEIL(MAX(f.predicted_sales) - p.quantity) AS suggested_reorder
        FROM ai_sales_forecast f
        JOIN products p ON f.product_id = p.product_id
        LEFT JOIN supplier s ON TRIM(LOWER(p.vendor)) = TRIM(LOWER(s.SupplierName))
        WHERE f.forecast_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
          AND f.predicted_sales > p.quantity
        GROUP BY p.product_id, p.product_name, p.quantity, p.vendor;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            int productId = rs.getInt("product_id");
            String productName = rs.getString("product_name");
            int currentStock = rs.getInt("quantity");
            double predictedSales = rs.getDouble("predicted_sales");
            int suggestedReorder = Math.max(1, rs.getInt("suggested_reorder")); // Ensure at least 1

            Integer supplierId = rs.getObject("supplier_id") != null ? rs.getInt("supplier_id") : null;
            String supplierName = rs.getString("supplier_name") != null ? rs.getString("supplier_name") : "Unknown Supplier";

            Map<String, Object> replenishment = new HashMap<>();
            replenishment.put("product_id", productId);
            replenishment.put("product_name", productName);
            replenishment.put("current_stock", currentStock);
            replenishment.put("predicted_sales", predictedSales);
            replenishment.put("suggested_reorder", suggestedReorder);
            replenishment.put("supplier_id", supplierId);
            replenishment.put("supplier_name", supplierName);

            replenishments.add(replenishment);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return replenishments;
}





  //*****************************************//
 // ✅ AI-Powered Supplier Recommendations //
//****************************************//
public List<Map<String, Object>> getVendorRecommendations() {
    List<Map<String, Object>> recommendations = new ArrayList<>();
    String sql = """
        SELECT s.SupplierID, s.SupplierName, p.product_id, p.product_name,
               COUNT(p.product_id) AS total_orders, AVG(p.purchase_price) AS avg_price, s.LastShipmentDate
        FROM products p
        JOIN supplier s ON p.vendor = s.SupplierName
        GROUP BY s.SupplierID, p.product_id
        ORDER BY total_orders DESC, avg_price ASC, s.LastShipmentDate DESC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> recommendation = Map.of(
                "supplier_id", rs.getInt("SupplierID"),
                "supplier_name", rs.getString("SupplierName"),
                "product_id", rs.getInt("product_id"),
                "product_name", rs.getString("product_name"),
                "total_orders", rs.getInt("total_orders"),
                "average_price", rs.getDouble("avg_price"),
                "last_shipment_date", rs.getDate("LastShipmentDate")
            );
            recommendations.add(recommendation);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return recommendations;
}



    //************************************************//
   // AI-Powered Customer Insights & Personalization //
  //************************************************// 

  public List<Map<String, Object>> getPersonalizedPromotions() {
    List<Map<String, Object>> promotions = new ArrayList<>();
    String sql = """
        SELECT 
            COALESCE(wc.CustomerID, 'General') AS customer_id,
            COALESCE(wc.CustomerName, 'General Customer') AS customer_name,
            p.product_id, 
            p.product_name, 
            pr.promo_id, 
            pr.description, 
            pr.discount_percentage, 
            pr.end_date,
            MAX(o.order_date) AS last_order_date  -- Fix: Use MAX() for ordering
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        JOIN products p ON oi.product_id = p.product_id
        LEFT JOIN product_promotions pp ON p.product_id = pp.product_id
        LEFT JOIN promotions pr ON pp.promo_id = pr.promo_id
        LEFT JOIN wholesalecustomer wc ON o.order_id = wc.order_id  -- Direct join with order_id
        WHERE pr.active = 1 
          AND pr.end_date > CURDATE()
        GROUP BY wc.CustomerID, wc.CustomerName, p.product_id, p.product_name, 
                 pr.promo_id, pr.description, pr.discount_percentage, pr.end_date
        ORDER BY last_order_date DESC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> promo = new HashMap<>();
            promo.put("customer_id", rs.getString("customer_id"));
            promo.put("customer_name", rs.getString("customer_name"));
            promo.put("product_id", rs.getInt("product_id"));
            promo.put("product_name", rs.getString("product_name"));
            promo.put("promo_id", rs.getInt("promo_id"));
            promo.put("description", rs.getString("description"));
            promo.put("discount_percentage", rs.getDouble("discount_percentage"));
            promo.put("end_date", rs.getTimestamp("end_date"));
            promo.put("last_order_date", rs.getDate("last_order_date"));  // Add this for debugging

            promotions.add(promo);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return promotions;
}


public void updatePromotionsDaily() {
    String sql = """
        INSERT INTO product_promotions (promo_id, product_id)
        SELECT pp.promo_id, oi.product_id
        FROM product_promotions pp
        JOIN order_items oi ON oi.product_id = pp.product_id  -- ✅ Correct JOIN condition
        JOIN promotions p ON pp.promo_id = p.promo_id  -- ✅ Ensure valid promotions
        WHERE p.start_date <= NOW() 
        AND p.end_date > NOW()  
        AND p.active = 1
        AND NOT EXISTS (
            SELECT 1 FROM product_promotions pp2 
            WHERE pp2.promo_id = pp.promo_id AND pp2.product_id = oi.product_id
        );
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.executeUpdate();
        System.out.println("✅ Daily promotions updated successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


// ✅ Method to check if a forecast already exists for a product and date
public static boolean forecastExists(int productId, LocalDate forecastDate) {
    String sql = "SELECT COUNT(*) FROM ai_sales_forecast WHERE product_id = ? AND forecast_date = ?";
    
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, productId);
        stmt.setDate(2, Date.valueOf(forecastDate));
        
        ResultSet rs = stmt.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            return true; // Forecast already exists
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false; // No forecast exists
}




/********************************/
//         ReOredering  and display the reOrdering details         /
//******************************/
    // ✅ Save AI Reorder Request
    // ✅ Save Multiple AI Reorder Requests in Bulk
public boolean saveReorders(List<Map<String, Object>> reorderList) {
    String sql = "INSERT INTO reorders (product_id, supplier_id, reorder_quantity) VALUES (?, ?, ?)";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        for (Map<String, Object> reorder : reorderList) {
            stmt.setInt(1, (int) reorder.get("productId"));
            stmt.setInt(2, (int) reorder.get("supplierId"));
            stmt.setInt(3, (int) reorder.get("reorderQty"));
            stmt.addBatch(); // Add to batch execution
        }

        int[] rowsAffected = stmt.executeBatch();
        return rowsAffected.length > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public List<Map<String, Object>> getPastReorders() {
    List<Map<String, Object>> reorderHistory = new ArrayList<>();
    String sql = """
        SELECT MIN(reorder_id) AS reorder_id, 
               DATE(reorder_date) AS reorder_date,  -- ✅ Convert timestamp to DATE format
               COUNT(product_id) AS total_products, 
               status
        FROM reorders
        GROUP BY reorder_date, status
        ORDER BY reorder_date DESC;
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            Map<String, Object> reorder = new HashMap<>();
            reorder.put("reorder_id", rs.getInt("reorder_id"));  // Group ID
            reorder.put("reorder_date", rs.getString("reorder_date")); // ✅ Return as string YYYY-MM-DD
            reorder.put("total_products", rs.getInt("total_products"));
            reorder.put("status", rs.getString("status"));
            reorderHistory.add(reorder);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return reorderHistory;
}




public List<Map<String, Object>> getReorderDetails(String reorderDate) {
    List<Map<String, Object>> reorderDetails = new ArrayList<>();
    String sql = """
        SELECT r.reorder_id, r.product_id, p.product_name, r.reorder_quantity, 
               s.SupplierID AS supplier_id, s.SupplierName, s.PhoneNumber AS supplier_phone, s.email AS supplier_email, 
               r.reorder_date, r.status
        FROM reorders r
        JOIN products p ON r.product_id = p.product_id
        JOIN supplier s ON r.supplier_id = s.SupplierID
        WHERE DATE(r.reorder_date) = ?;  
    """;

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, reorderDate); // ✅ Use string since MySQL DATE format can match
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Map<String, Object> reorderItem = new HashMap<>();
            reorderItem.put("reorder_id", rs.getInt("reorder_id"));
            reorderItem.put("product_id", rs.getInt("product_id"));
            reorderItem.put("supplier_id", rs.getInt("supplier_id")); // ✅ Include supplier_id
            reorderItem.put("product_name", rs.getString("product_name"));
            reorderItem.put("reorder_quantity", rs.getInt("reorder_quantity"));
            reorderItem.put("supplier_name", rs.getString("SupplierName"));
            reorderItem.put("supplier_phone", rs.getString("supplier_phone"));
            reorderItem.put("supplier_email", rs.getString("supplier_email"));
            reorderItem.put("reorder_date", rs.getTimestamp("reorder_date"));
            reorderItem.put("status", rs.getString("status"));
            reorderDetails.add(reorderItem);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return reorderDetails;
}



public boolean updateMultipleReorderStatuses(List<Map<String, Object>> updates) {
    String sql = "UPDATE reorders SET status = ? WHERE product_id = ? AND supplier_id = ?";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        for (Map<String, Object> update : updates) {
            stmt.setString(1, (String) update.get("status"));
            stmt.setInt(2, (int) update.get("product_id"));
            stmt.setInt(3, (int) update.get("supplier_id")); // ✅ Use supplier_id directly
            stmt.addBatch();
        }

        stmt.executeBatch();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}







//*************************** */
//           recommendations  //
//************************* */


    // 🔥 Get frequently purchased items for a customer
    public List<Map<String, Object>> getCustomerRecommendations(int customerId) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        String sql = """
            SELECT oi.product_id, p.product_name, COUNT(oi.order_id) AS purchase_count
            FROM order_items oi
            JOIN orders o ON oi.order_id = o.order_id
            JOIN products p ON oi.product_id = p.product_id
            WHERE o.customer_id = ?
            GROUP BY oi.product_id, p.product_name
            ORDER BY purchase_count DESC
            LIMIT 5;
        """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> recommendation = new HashMap<>();
                recommendation.put("product_id", rs.getInt("product_id"));
                recommendation.put("product_name", rs.getString("product_name"));
                recommendation.put("purchase_count", rs.getInt("purchase_count"));
                recommendations.add(recommendation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recommendations;
    }

    /************************* */
    // AI chat bot 
    /********************* */

        // 🔥 Get top-selling product of the day
        public Map<String, Object> getTopSellingProductToday() {
            String sql = """
                SELECT oi.product_id, p.product_name, SUM(oi.quantity) AS total_sold
                FROM order_items oi
                JOIN orders o ON oi.order_id = o.order_id
                JOIN products p ON oi.product_id = p.product_id
                WHERE o.order_date = CURDATE()
                GROUP BY oi.product_id, p.product_name
                ORDER BY total_sold DESC
                LIMIT 1;
            """;
    
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
    
                if (rs.next()) {
                    return Map.of(
                            "product_id", rs.getInt("product_id"),
                            "product_name", rs.getString("product_name"),
                            "total_sold", rs.getInt("total_sold")
                    );
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        /************************** */
        //  fraudlent              //
        /************************* */

            // 🔥 Detect potentially fraudulent orders
            public List<Map<String, Object>> detectFraudulentOrders() {
                List<Map<String, Object>> fraudOrders = new ArrayList<>();
                String sql = """
                    SELECT o.order_id, o.customer_id, SUM(oi.quantity) AS total_items, COUNT(oi.product_id) AS unique_products
                    FROM orders o
                    JOIN order_items oi ON o.order_id = oi.order_id
                    WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                    GROUP BY o.order_id, o.customer_id
                    HAVING total_items > 50 OR unique_products > 10;
                """;
            
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                     PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
            
                    while (rs.next()) {
                        Map<String, Object> fraudOrder = new HashMap<>();
                        fraudOrder.put("order_id", rs.getInt("order_id"));
                        fraudOrder.put("customer_id", rs.getInt("customer_id"));
                        fraudOrder.put("total_items", rs.getInt("total_items"));
                        fraudOrder.put("unique_products", rs.getInt("unique_products"));
                        fraudOrders.add(fraudOrder);
                    }
            
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return fraudOrders;
            }
            


    }


/*
AI table
CREATE TABLE ai_sales_forecast (
    forecast_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    forecast_date DATE NOT NULL,
    predicted_sales DECIMAL(10,2) NOT NULL,
    confidence_level DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);
 */


 /*CREATE TABLE reorders (
    reorder_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    supplier_id INT NOT NULL,
    reorder_quantity INT NOT NULL,
    reorder_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'Pending',
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (supplier_id) REFERENCES supplier(SupplierID)
);
*/
