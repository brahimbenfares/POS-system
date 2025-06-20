package com.walid.backend.DAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
//import java.time.temporal.IsoFields;
//import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.walid.backend.Model.Order;
import com.walid.backend.Model.OrderItem;
import com.walid.backend.Model.OrderMinimalSummaryDTO;
import com.walid.backend.Model.OrderSummaryDTO;
import com.walid.backend.Model.OrderDetails;

public class OrderDAO {
    private static final Logger logger = LoggerFactory.getLogger(OrderDAO.class);

    // Connection helper method
    private Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    // OrderDAO.java

    // ✅ Save order with customerId
    public Order saveOrder(Order order, List<OrderItem> orderItems) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Start transaction

            // Insert into orders
            String orderSql = "INSERT INTO orders (order_date, order_time, status, action, delivery, customer_id) VALUES (?, ?, 'unread', 'pending', ?, ?)";
            stmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            stmt.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setTime(2, new java.sql.Time(order.getOrderTime().getTime()));
            stmt.setDouble(3, order.getDelivery()); // Set delivery fee
            if (order.getCustomerId() != null) {
                stmt.setInt(4, order.getCustomerId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                order.setOrderId(generatedKeys.getInt(1));
            }

            // Insert order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, promo_id, discount_percentage, discount_amount, final_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS);

            for (OrderItem item : orderItems) {
                itemStmt.setInt(1, order.getOrderId());
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setObject(4, item.getPromoId(), Types.INTEGER);
                itemStmt.setDouble(5, item.getDiscountPercentage());
                itemStmt.setDouble(6, item.getDiscountAmount());
                itemStmt.setDouble(7, item.getFinalPrice());
                itemStmt.executeUpdate();

                ResultSet itemKeys = itemStmt.getGeneratedKeys();
                if (itemKeys.next()) {
                    item.setOrderItemId(itemKeys.getInt(1));
                }
            }

            conn.commit();  // Commit transaction
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();  // Rollback in case of error
            }
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (stmt != null) stmt.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
        return order;
    }

    
    
    
   
        // ✅ Update order with customerId
        public boolean updateOrder(Order order, List<OrderItem> orderItems) throws SQLException {
            Connection conn = null;
            try {
                conn = getConnection();
                conn.setAutoCommit(false);
    
                // Update orders table
                String orderSql = "UPDATE orders SET order_date = ?, order_time = ?, delivery = ?, customer_id = ? WHERE order_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
                    stmt.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
                    stmt.setTime(2, new java.sql.Time(order.getOrderTime().getTime()));
                    stmt.setDouble(3, order.getDelivery());
                    if (order.getCustomerId() != null) {
                        stmt.setInt(4, order.getCustomerId());
                    } else {
                        stmt.setNull(4, Types.INTEGER);
                    }
                    stmt.setInt(5, order.getOrderId());
    
                    stmt.executeUpdate();
                }
    
                // Delete existing order items
                String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
                    stmt.setInt(1, order.getOrderId());
                    stmt.executeUpdate();
                }
    
                // Insert new order items
                String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, promo_id, discount_percentage, discount_amount, final_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                    for (OrderItem item : orderItems) {
                        itemStmt.setInt(1, order.getOrderId());
                        itemStmt.setInt(2, item.getProductId());
                        itemStmt.setInt(3, item.getQuantity());
                        if (item.getPromoId() != null) {
                            itemStmt.setInt(4, item.getPromoId());
                        } else {
                            itemStmt.setNull(4, Types.INTEGER);
                        }
                        itemStmt.setDouble(5, item.getDiscountPercentage());
                        itemStmt.setDouble(6, item.getDiscountAmount());
                        itemStmt.setDouble(7, item.getFinalPrice());
                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }
    
                conn.commit();
                return true;
            } catch (SQLException e) {
                if (conn != null) conn.rollback();
                throw e;
            } finally {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            }
        }
    
    
    
    
    public boolean deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    


 
        // ✅ Find all orders (with customerId)
        public List<Order> findAllOrders() {
            List<Order> orders = new ArrayList<>();
            String sql = "SELECT * FROM orders";
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
    
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getTime("order_time"),
                        rs.getString("status"),
                        rs.getString("action"),
                        rs.getDouble("delivery"),
                        rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null // ✅ Retrieve customerId
                    );
                    orders.add(order);
                }
            } catch (SQLException e) {
                logger.error("Error retrieving all orders: ", e);
            }
            return orders;
        }
    
    

            // ✅ Find all orders for a specific customer
    public List<Order> findOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getTime("order_time"),
                        rs.getString("status"),
                        rs.getString("action"),
                        rs.getDouble("delivery"),
                        rs.getInt("customer_id")
                    );
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving orders by customerId: ", e);
        }
        return orders;
    }



        // ✅ Find order by ID with customerId
        public Order findOrderById(int orderId) throws SQLException {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
    
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Order(
                            rs.getInt("order_id"),
                            rs.getDate("order_date"),
                            rs.getTime("order_time"),
                            rs.getString("status"),
                            rs.getString("action"),
                            rs.getDouble("delivery"),
                            rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null // ✅ Retrieve customerId
                        );
                    }
                }
            }
            return null;
        }
    

    // Update order action
    public boolean updateOrderAction(int orderId, String action) throws SQLException {
        String sql = "UPDATE orders SET action = ? WHERE order_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    
    
    

    // ✅ Find all unread orders (including customerId)
public List<Order> findAllUnreadOrders() throws SQLException {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM orders WHERE status = 'unread'";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            Order order = new Order(
                rs.getInt("order_id"),
                rs.getDate("order_date"),
                rs.getTime("order_time"),
                rs.getString("status"),
                rs.getString("action"),
                rs.getDouble("delivery"),
                rs.getObject("customer_id") != null ? rs.getInt("customer_id") : null // ✅ Retrieve customerId
            );
            orders.add(order);
        }
    } catch (SQLException e) {
        logger.error("Error retrieving all unread orders: ", e);
    }
    return orders;
}

    // Find order details by ID including discounts
    public OrderDetails findOrderDetailsById(int orderId) throws SQLException {
        OrderDetails orderDetails = null;
    
        String orderSql = "SELECT * FROM orders WHERE order_id = ?";
        String itemsSql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, p.product_name, p.sales_price, " +
                          "oi.quantity, oi.promo_id, pr.description AS promo_description, " +
                          "pr.discount_percentage AS promo_discount_percentage, " +
                          "ad.description AS additional_discount_description, " +
                          "ad.discount_percentage AS additional_discount_percentage, " +
                          "ad.discount_amount AS additional_discount_amount, " +
                          "oi.discount_percentage AS discount_percentage, " +  
                          "oi.discount_amount AS discount_amount, " +          
                          "oi.final_price AS final_price, " +                  
                          "o.order_date, o.order_time, o.delivery " + // Include delivery fee and time
                          "FROM order_items oi " +
                          "JOIN products p ON oi.product_id = p.product_id " +
                          "LEFT JOIN promotions pr ON oi.promo_id = pr.promo_id " +
                          "LEFT JOIN additional_discounts ad ON oi.order_item_id = ad.order_item_id " +
                          "JOIN orders o ON oi.order_id = o.order_id " +
                          "WHERE oi.order_id = ?";
    
        try (Connection conn = getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql)) {
            
            orderStmt.setInt(1, orderId);
    
            try (ResultSet rs = orderStmt.executeQuery()) {
                if (rs.next()) {
                    orderDetails = new OrderDetails();
                    orderDetails.setOrderID(rs.getInt("order_id"));
                    orderDetails.setOrderDate(rs.getDate("order_date").toString());
                    orderDetails.setOrderTime(rs.getTime("order_time").toString());
                    orderDetails.setStatus(rs.getString("status"));
                    orderDetails.setAction(rs.getString("action"));
                    orderDetails.setDelivery(rs.getDouble("delivery")); // Set delivery fee
                }
            }
    
            if (orderDetails != null) {
                try (PreparedStatement itemsStmt = conn.prepareStatement(itemsSql)) {
                    itemsStmt.setInt(1, orderId);
                    try (ResultSet rsItems = itemsStmt.executeQuery()) {
                        List<OrderItem> orderItemDetailsList = new ArrayList<>();
                        while (rsItems.next()) {
                            OrderItem itemDetails = new OrderItem();
                            itemDetails.setOrderItemId(rsItems.getInt("order_item_id"));
                            itemDetails.setOrderId(rsItems.getInt("order_id"));
                            itemDetails.setProductId(rsItems.getInt("product_id"));
                            itemDetails.setProductName(rsItems.getString("product_name"));
                            itemDetails.setSalesPrice(rsItems.getDouble("sales_price"));
                            itemDetails.setQuantity(rsItems.getInt("quantity"));
                            itemDetails.setPromoId(rsItems.getInt("promo_id"));
                            itemDetails.setPromotionDescription(rsItems.getString("promo_description"));
                            itemDetails.setPromoDiscountPercentage(rsItems.getDouble("promo_discount_percentage"));
                            itemDetails.setDiscountPercentage(rsItems.getDouble("discount_percentage"));
                            itemDetails.setDiscountAmount(rsItems.getDouble("discount_amount"));
                            itemDetails.setFinalPrice(rsItems.getDouble("final_price"));
    
                            // Additional discount details
                            itemDetails.setAdditionalDiscountDescription(rsItems.getString("additional_discount_description"));
                            itemDetails.setAdditionalDiscountPercentage(rsItems.getObject("additional_discount_percentage") != null ? rsItems.getDouble("additional_discount_percentage") : null);
                            itemDetails.setAdditionalDiscountAmount(rsItems.getObject("additional_discount_amount") != null ? rsItems.getDouble("additional_discount_amount") : null);
    
                            orderItemDetailsList.add(itemDetails);
                        }
                        orderDetails.setOrderItems(orderItemDetailsList);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving order details for Order ID {}: {}", orderId, e.getMessage());
            throw e;
        }
    
        return orderDetails;
    }
    

    

    // Confirm order and update product quantities along with handling discounts
    public boolean confirmOrderAndUpdateProductQuantity(int orderId) throws SQLException {
        Connection conn = null;
    
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // Start transaction
    
            // Update order action to 'Confirm'
            String orderSql = "UPDATE orders SET action = 'Confirm' WHERE order_id = ?";
            try (PreparedStatement orderStmt = conn.prepareStatement(orderSql)) {
                orderStmt.setInt(1, orderId);
                orderStmt.executeUpdate();
            }
    
            // Fetch order items and update product quantities
            String itemsSql = "SELECT product_id, quantity FROM order_items WHERE order_id = ?";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemsSql)) {
                itemStmt.setInt(1, orderId);
                try (ResultSet rs = itemStmt.executeQuery()) {
                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");
    
                        // Ensure enough stock is available
                        String productSql = "UPDATE products SET quantity = quantity - ? " +
                                            "WHERE product_id = ? AND quantity >= ?";
                        try (PreparedStatement productStmt = conn.prepareStatement(productSql)) {
                            productStmt.setInt(1, quantity);
                            productStmt.setInt(2, productId);
                            productStmt.setInt(3, quantity);
    
                            int rowsAffected = productStmt.executeUpdate();
                            if (rowsAffected == 0) {
                                throw new SQLException("Insufficient stock for product ID: " + productId);
                            }
                        }
                    }
                }
            }
    
            conn.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Rollback in case of error
            }
            logger.error("Error confirming order: ", e);
            return false;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    


    

    // Get sales data for a date range
    // In OrderDAO.java

    public List<Map<String, Object>> getTotalSalesByDate(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> salesData = new ArrayList<>();
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    
        // Determine the grouping based on the date range
        String periodGroupBy = (daysBetween <= 7) ? "DATE(o.order_date)" :
                               (daysBetween <= 31) ? "YEAR(o.order_date), WEEK(o.order_date)" :
                               "YEAR(o.order_date), MONTH(o.order_date)";
    
        

        String sql = "SELECT " + periodGroupBy + " AS period, " +
                     "SUM(oi.final_price) + SUM(o.delivery) AS total_sales " +
                     "FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "WHERE o.order_date BETWEEN ? AND ? AND o.action = 'Confirm' " +
                     "GROUP BY period";
        

    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> record = new HashMap<>();
                    record.put("period", rs.getString("period"));
                    record.put("total_sales", rs.getDouble("total_sales"));
                    salesData.add(record);
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving sales data: ", e);
        }
        return salesData;
    }
    
    

    public double getTotalSalesForDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) {
        double totalSales = 0.0;
        
        String sql = "SELECT SUM(oi.final_price) + SUM(o.delivery) AS total_sales " +
                     "FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE o.order_date BETWEEN ? AND ? AND o.action = 'Confirm'";
                     
    
        if (storeNumber != null) {
            sql += " AND p.store_number = ?";
        }
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            if (storeNumber != null) {
                stmt.setInt(3, storeNumber);
            }
    
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalSales = rs.getDouble("total_sales");
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving total sales for date range: ", e);
        }
        return totalSales;
    }
    

   

public List<OrderSummaryDTO> findTodayOrders(LocalDate today, Integer storeNumber) {
    List<OrderSummaryDTO> todayOrders = new ArrayList<>();
    
    String sql = "SELECT o.order_id, o.order_date, o.order_time, o.action AS status, " +
                 "SUM(oi.final_price) + o.delivery AS total_sales " +
                 "FROM orders o " +
                 "JOIN order_items oi ON o.order_id = oi.order_id " +
                 "JOIN products p ON oi.product_id = p.product_id " +
                 "WHERE o.order_date = ? AND o.action = 'Confirm'";
                 

    if (storeNumber != null) {
        sql += " AND p.store_number = ?";
    }

    sql += " GROUP BY o.order_id, o.order_date, o.order_time, o.action " +
           "ORDER BY o.order_time DESC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setDate(1, Date.valueOf(today));
        if (storeNumber != null) {
            stmt.setInt(2, storeNumber);
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Date orderDate = rs.getDate("order_date");
                Time orderTime = rs.getTime("order_time");
                String status = rs.getString("status");
                double totalSales = rs.getDouble("total_sales");

                String orderDateTime = orderDate.toString() + " " + orderTime.toString().substring(0,5);

                OrderSummaryDTO orderSummary = new OrderSummaryDTO(orderId, orderDateTime, totalSales, status);
                todayOrders.add(orderSummary);
            }
        }
    } catch (SQLException e) {
        logger.error("Error retrieving today's orders: ", e);
    }
    return todayOrders;
}


    



    public boolean markOrderAsRead(int orderId) {
        String sql = "UPDATE orders SET status = 'read', updated_at = CURRENT_TIMESTAMP " +
                     "WHERE order_id = ? AND status = 'unread'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking order as read: ", e);
            return false;
        }
    }
    






    
    public Map<String, List<Object>> getSalesData(String filter, Integer storeNumber) throws SQLException {
        Map<String, List<Object>> salesData = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate = today;
    
        String groupBy;
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
    
        switch (filter) {
            case "daily":
                // Last 7 days
                startDate = today.minusDays(6);
                groupBy = "DATE_FORMAT(o.order_date, '%b %d')"; // e.g., "Apr 27"
                labels = getDateLabels(startDate, endDate);
                break;
            case "weekly":
                // Current month, grouped by week
                startDate = today.withDayOfMonth(1);
                groupBy = "CONCAT('Wk ', WEEK(o.order_date, 3), ' ', YEAR(o.order_date))"; // ISO week numbering
                labels = getWeekLabels(startDate, endDate);
                break;
            case "monthly":
                // Last 7 months
                startDate = today.minusMonths(6).withDayOfMonth(1);
                groupBy = "DATE_FORMAT(o.order_date, '%b')"; // e.g., "Apr"
                labels = getMonthLabels(startDate, endDate);
                break;
            default:
                throw new IllegalArgumentException("Invalid filter: " + filter);
        }
    
        logger.debug("Fetching sales data with filter '{}': startDate={}, endDate={}, groupBy={}", 
                     filter, startDate, endDate, groupBy);    
        

        String sql = "SELECT " + groupBy + " AS period, " +
                     "SUM(oi.final_price) + SUM(o.delivery) AS total_sales " +
                     "FROM orders o " +
                     "JOIN order_items oi ON o.order_id = oi.order_id " +
                     "JOIN products p ON oi.product_id = p.product_id " +
                     "WHERE o.order_date BETWEEN ? AND ? AND o.action = 'Confirm'";
                     
    
        if (storeNumber != null) {
            sql += " AND p.store_number = ?";
        }
    
        sql += " GROUP BY " + groupBy + " ORDER BY " + groupBy;
    
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            if (storeNumber != null) {
                stmt.setInt(3, storeNumber);
            }
    
            try (ResultSet rs = stmt.executeQuery()) {
                Map<String, Double> salesMap = new HashMap<>();
                while (rs.next()) {
                    String period = rs.getString("period");
                    double totalSales = rs.getDouble("total_sales");
                    salesMap.put(period, totalSales);
                }
    
                // Populate data list based on labels
                for (String label : labels) {
                    double sale = salesMap.getOrDefault(label, 0.0);
                    data.add(sale);
                }
            }
        }
    
        salesData.put("labels", new ArrayList<>(labels));
        salesData.put("data", new ArrayList<>(data));
        return salesData;
    }
    
    
    // Helper methods to generate labels based on the filter
    private List<String> getDateLabels(LocalDate start, LocalDate end) {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH); // e.g., "Apr 27"
        LocalDate current = start;
        while (!current.isAfter(end)) {
            labels.add(current.format(formatter));
            current = current.plusDays(1);
        }
        return labels;
    }
    
    private List<String> getWeekLabels(LocalDate start, LocalDate end) {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'Wk' w yyyy", Locale.ENGLISH); // e.g., "Wk 42 2024"
        LocalDate current = start;
        while (!current.isAfter(end)) {
            String weekLabel = current.format(formatter); // Utilize the formatter
            labels.add(weekLabel);
            current = current.plusWeeks(1);
        }
        return labels;
    }
    
    private List<String> getMonthLabels(LocalDate start, LocalDate end) {
        List<String> labels = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH); // e.g., "Apr"
        LocalDate current = start;
        while (!current.isAfter(end)) {
            labels.add(current.format(formatter));
            current = current.plusMonths(1);
        }
        return labels;
    }
    
// In OrderDAO.java

public List<OrderSummaryDTO> findConfirmedOrdersByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) throws SQLException {
    List<OrderSummaryDTO> orders = new ArrayList<>();
    
    String sql = "SELECT o.order_id, o.order_date, o.order_time, o.action AS status, " +
                 "SUM(oi.final_price) + o.delivery AS total_sales " +
                 "FROM orders o " +
                 "JOIN order_items oi ON o.order_id = oi.order_id " +
                 "JOIN products p ON oi.product_id = p.product_id " +
                 "WHERE o.order_date BETWEEN ? AND ? AND o.action = 'Confirm'";
    

    if (storeNumber != null) {
        sql += " AND p.store_number = ?";
    }

    sql += " GROUP BY o.order_id, o.order_date, o.order_time, o.action " +
           "ORDER BY o.order_date ASC, o.order_time ASC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        int paramIndex = 1;
        stmt.setDate(paramIndex++, Date.valueOf(startDate));
        stmt.setDate(paramIndex++, Date.valueOf(endDate));

        if (storeNumber != null) {
            stmt.setInt(paramIndex++, storeNumber);
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Date orderDate = rs.getDate("order_date");
                Time orderTime = rs.getTime("order_time");
                String orderStatus = rs.getString("status");
                double totalSales = rs.getDouble("total_sales");

                String orderDateTime = orderDate.toString() + " " + orderTime.toString().substring(0,5);

                OrderSummaryDTO orderSummary = new OrderSummaryDTO(orderId, orderDateTime, totalSales, orderStatus);
                orders.add(orderSummary);
            }
        }
    }

    return orders;
}




public List<OrderMinimalSummaryDTO> findOrdersWithMinimalItemDetails(Integer storeNumber) throws SQLException {
    List<OrderMinimalSummaryDTO> orders = new ArrayList<>();
    /*String sql = "SELECT o.order_id, o.order_date, o.order_time, o.action AS status, p.store_number, " +
                 "CASE WHEN MIN(oi.quantity) >= 5 THEN 'wholesale' ELSE 'each' END AS sale_type, " +
                 "SUM(oi.final_price) AS total_sales " +
                 "FROM orders o " +
                 "JOIN order_items oi ON o.order_id = oi.order_id " +
                 "JOIN products p ON oi.product_id = p.product_id ";*/

    String sql = "SELECT o.order_id, o.order_date, o.order_time, o.action AS status, p.store_number, " +
             "CASE WHEN MIN(oi.quantity) >= 5 THEN 'wholesale' ELSE 'each' END AS sale_type, " +
             "SUM(oi.final_price) + o.delivery AS total_sales " +
             "FROM orders o " +
             "JOIN order_items oi ON o.order_id = oi.order_id " +
             "JOIN products p ON oi.product_id = p.product_id ";


    // Optionally filter by store number
    if (storeNumber != null) {
        sql += "WHERE p.store_number = ?";
    }

    // Group by order details and order results by order_id in descending order
    sql += " GROUP BY o.order_id, o.order_date, o.order_time, o.action, p.store_number" +
           " ORDER BY o.order_id DESC";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        if (storeNumber != null) {
            stmt.setInt(1, storeNumber);
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Date orderDate = rs.getDate("order_date");
                Time orderTime = rs.getTime("order_time");
                String status = rs.getString("status");
                String saleType = rs.getString("sale_type");
                double totalSales = rs.getDouble("total_sales");
                Integer storeNum = rs.getInt("store_number");

                OrderMinimalSummaryDTO orderSummary = new OrderMinimalSummaryDTO(
                    orderId, orderDate.toString() + " " + orderTime.toString(), totalSales, status, saleType, storeNum
                );
                orders.add(orderSummary);
            }
        }
    }

    return orders;
}



public List<Map<String, Object>> getProfitByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber, String category, String productName) {
    List<Map<String, Object>> profitData = new ArrayList<>();
    String sql = "SELECT DATE(o.order_date) AS order_date, " +
                 "SUM(oi.quantity * p.sales_price) AS total_original_price, " +
                 "SUM(oi.final_price) AS total_final_price, " +
                 "SUM(p.purchase_price * oi.quantity) AS total_cost, " +
                 "SUM(oi.quantity * p.sales_price) - SUM(oi.final_price) AS total_discount, " +
                 "SUM(oi.discount_amount) AS total_additional_discount, " +
                 "(SUM(oi.quantity * p.sales_price) - SUM(oi.final_price)) - SUM(oi.discount_amount) AS total_promo_discount, " +
                 "SUM(oi.final_price) - SUM(p.purchase_price * oi.quantity) AS net_profit " +
                 "FROM orders o " +
                 "JOIN order_items oi ON o.order_id = oi.order_id " +
                 "JOIN products p ON oi.product_id = p.product_id " +
                 "WHERE o.order_date BETWEEN ? AND ? AND o.action = 'Confirm' ";

    if (storeNumber != null) {
        sql += " AND p.store_number = ? ";
    }
    if (category != null && !category.isEmpty()) {
        sql += " AND p.category = ? ";
    }
    if (productName != null && !productName.isEmpty()) {
        sql += " AND p.product_name LIKE ? ";
    }

    sql += "GROUP BY DATE(o.order_date) ORDER BY DATE(o.order_date)";

    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        int paramIndex = 1;
        stmt.setDate(paramIndex++, Date.valueOf(startDate));
        stmt.setDate(paramIndex++, Date.valueOf(endDate));
        if (storeNumber != null) {
            stmt.setInt(paramIndex++, storeNumber);
        }
        if (category != null && !category.isEmpty()) {
            stmt.setString(paramIndex++, category);
        }
        if (productName != null && !productName.isEmpty()) {
            stmt.setString(paramIndex++, "%" + productName + "%");
        }

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> record = new HashMap<>();
                record.put("order_date", rs.getDate("order_date"));
                record.put("total_original_price", rs.getDouble("total_original_price"));
                record.put("total_final_price", rs.getDouble("total_final_price"));
                record.put("total_cost", rs.getDouble("total_cost"));
                record.put("total_discount", rs.getDouble("total_discount"));
                record.put("total_additional_discount", rs.getDouble("total_additional_discount"));
                record.put("total_promo_discount", rs.getDouble("total_promo_discount"));
                record.put("net_profit", rs.getDouble("net_profit"));
                profitData.add(record);
            }
        }
    } catch (SQLException e) {
        logger.error("Error retrieving profit data: ", e);
    }
    return profitData;
}



}




/*public Order saveOrder(Order order, List<OrderItem> orderItems) throws SQLException {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet generatedKeys = null;

    try {
        conn = getConnection();
        conn.setAutoCommit(false);  // Start transaction

        // Insert into orders
        String orderSql = "INSERT INTO orders (order_date, order_time, status, action, delivery) VALUES (?, ?, 'unread', 'pending', ?)";
        stmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
        stmt.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
        stmt.setTime(2, new java.sql.Time(order.getOrderTime().getTime()));
        stmt.setDouble(3, order.getDelivery()); // Set delivery fee

        int affectedRows = stmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating order failed, no rows affected.");
        }

        generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            order.setOrderId(generatedKeys.getInt(1));
        }

        // Insert order items
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, promo_id, discount_percentage, discount_amount, final_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement itemStmt = conn.prepareStatement(itemSql, Statement.RETURN_GENERATED_KEYS);

        for (OrderItem item : orderItems) {
            itemStmt.setInt(1, order.getOrderId());
            itemStmt.setInt(2, item.getProductId());
            itemStmt.setInt(3, item.getQuantity());
            itemStmt.setObject(4, item.getPromoId(), Types.INTEGER);
            itemStmt.setDouble(5, item.getDiscountPercentage());
            itemStmt.setDouble(6, item.getDiscountAmount());
            itemStmt.setDouble(7, item.getFinalPrice());
            itemStmt.executeUpdate();

            ResultSet itemKeys = itemStmt.getGeneratedKeys();
            if (itemKeys.next()) {
                item.setOrderItemId(itemKeys.getInt(1));
            }
        }

        conn.commit();  // Commit transaction
    } catch (SQLException e) {
        if (conn != null) {
            conn.rollback();  // Rollback in case of error
        }
        throw e;
    } finally {
        if (generatedKeys != null) generatedKeys.close();
        if (stmt != null) stmt.close();
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    return order;
}
*/

    /*public boolean updateOrder(Order order, List<OrderItem> orderItems) throws SQLException {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
    
            // Update orders table
            String orderSql = "UPDATE orders SET order_date = ?, order_time = ?, delivery = ? WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(orderSql)) {
                stmt.setDate(1, new java.sql.Date(order.getOrderDate().getTime()));
                stmt.setTime(2, new java.sql.Time(order.getOrderTime().getTime()));
                stmt.setInt(3, order.getOrderId());
                stmt.setDouble(4, order.getDelivery()); // Update delivery price

                stmt.executeUpdate();
            }
    
            // Delete existing order items
            String deleteItemsSql = "DELETE FROM order_items WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteItemsSql)) {
                stmt.setInt(1, order.getOrderId());
                stmt.executeUpdate();
            }
    
            // Insert new order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, promo_id, discount_percentage, discount_amount, final_price) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
                for (OrderItem item : orderItems) {
                    itemStmt.setInt(1, order.getOrderId());
                    itemStmt.setInt(2, item.getProductId());
                    itemStmt.setInt(3, item.getQuantity());
                    if (item.getPromoId() != null) {
                        itemStmt.setInt(4, item.getPromoId());
                    } else {
                        itemStmt.setNull(4, Types.INTEGER);
                    }
                    itemStmt.setDouble(5, item.getDiscountPercentage());
                    itemStmt.setDouble(6, item.getDiscountAmount());
                    itemStmt.setDouble(7, item.getFinalPrice());
                    itemStmt.addBatch();
                }
                itemStmt.executeBatch();
            }
    
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }*/
    // Find all orders
    /*public List<Order> findAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getDate("order_date"),
                    rs.getTime("order_time"),
                    rs.getString("status"),
                    rs.getString("action"),
                    rs.getDouble("delivery") // Retrieve delivery price
                
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all orders: ", e);
        }
        return orders;
    }*/
    // Find order by ID
    /*public Order findOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Order(
                        rs.getInt("order_id"),
                        rs.getDate("order_date"),
                        rs.getTime("order_time"),
                        rs.getString("status"),
                        rs.getString("action"),
                        rs.getDouble("delivery") // Retrieve delivery price
                    
                    );
                }
            }
        }
        return null;
    }*/
    // Find all unread orders
    /*public List<Order> findAllUnreadOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE status = 'unread'";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getDate("order_date"),
                    rs.getTime("order_time"),
                    rs.getString("status"),
                    rs.getString("action"),
                    rs.getDouble("delivery") // Retrieve delivery price
                );
                
                orders.add(order);
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all unread orders: ", e);
        }
        return orders;
    }*/
