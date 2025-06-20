package com.walid.backend.Service;

import com.walid.backend.DAO.OrderDAO;
import com.walid.backend.Model.Order;
import com.walid.backend.Model.OrderDTO;
import com.walid.backend.Model.OrderDetails;
import com.walid.backend.Model.OrderItem;
import com.walid.backend.Model.OrderMinimalSummaryDTO;
import com.walid.backend.Model.OrderSummaryDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.Date;
import java.sql.Time;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderDAO orderDAO;

    // Constructor to inject the DAO
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    // ✅ Create a new order (supports customerId)
    public Order createOrder(Order order, List<OrderItem> orderItems) throws SQLException {
        order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
        order.setOrderTime(new java.sql.Time(System.currentTimeMillis()));

        // Ensure customerId is handled properly
        if (order.getCustomerId() == null) {
            logger.warn("Creating an order with no customerId.");
        }

        // Save order
        Order createdOrder = orderDAO.saveOrder(order, orderItems);
        logger.info("Order created successfully: {}", createdOrder);
        return createdOrder;
    }
    

    // ✅ Retrieve an order by ID (includes customerId)
    public Order getOrderById(int orderId) throws SQLException {
        Order order = orderDAO.findOrderById(orderId);
        if (order == null) {
            logger.warn("Order with ID {} not found", orderId);
        }
        return order;
    }

    // ✅ Retrieve all orders (including customerId)
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = orderDAO.findAllOrders();
        if (orders.isEmpty()) {
            logger.warn("No orders found in the database.");
        }
        return orders;
    }

    // ✅ Retrieve orders by Customer ID
    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = orderDAO.findOrdersByCustomerId(customerId);
        if (orders.isEmpty()) {
            logger.warn("No orders found for customerId: {}", customerId);
        }
        return orders;
    }

    // ✅ Update order (now supports customerId)
    public boolean updateOrder(int orderId, OrderDTO orderDTO) throws SQLException {
        Order order = new Order();
        order.setOrderId(orderId);
        order.setOrderDate(Date.valueOf(orderDTO.getDate()));
        order.setOrderTime(Time.valueOf(orderDTO.getTime() + ":00"));
        order.setDelivery(orderDTO.getDelivery());
        order.setCustomerId(orderDTO.getCustomerId()); // ✅ Include customerId

        List<OrderItem> orderItems = convertDtoToOrderItems(orderDTO.getOrderItems());
        return orderDAO.updateOrder(order, orderItems);
    }

    // Helper method to convert DTO to OrderItem list
    private List<OrderItem> convertDtoToOrderItems(List<OrderDTO.OrderItemDTO> orderItemDTOs) {
        return orderItemDTOs.stream().map(dto -> {
            OrderItem item = new OrderItem();
            item.setProductId(dto.getProductId());
            item.setQuantity(dto.getQuantity());
            item.setPromoId(dto.getPromoId());
            item.setDiscountPercentage(dto.getDiscountPercentage());
            item.setDiscountAmount(dto.getDiscountAmount());
            item.setFinalPrice(dto.getFinalPrice());
            item.setAdditionalDiscountDescription(dto.getAdditionalDiscountDescription());
            return item;
        }).collect(Collectors.toList());
    }

    // ✅ Retrieve Order Details by ID
    public OrderDetails getOrderDetailsById(int orderId) throws SQLException {
        OrderDetails orderDetails = orderDAO.findOrderDetailsById(orderId);
        if (orderDetails == null) {
            logger.warn("Order details not found for ID {}", orderId);
        }
        return orderDetails;
    }

    // ✅ Confirm order and update product quantity
    public boolean confirmOrderAndUpdateProductQuantity(int orderId) {
        try {
            return orderDAO.confirmOrderAndUpdateProductQuantity(orderId);
        } catch (SQLException e) {
            logger.error("Error confirming order ID {}: {}", orderId, e.getMessage());
            return false;
        }
    }

    // ✅ Find all unread orders (including customerId)
    public List<Order> getAllUnreadOrders() throws SQLException {
        List<Order> unreadOrders = orderDAO.findAllUnreadOrders();
        if (unreadOrders.isEmpty()) {
            logger.warn("No unread orders found.");
        }
        return unreadOrders;
    }

    // ✅ Delete order
    public boolean deleteOrder(int orderId) throws SQLException {
        return orderDAO.deleteOrder(orderId);
    }

    // ✅ Update order action and ensure confirmation is processed
    public boolean updateOrderAction(int orderId, String action) throws SQLException {
        boolean updated = orderDAO.updateOrderAction(orderId, action);
        
        if (updated && "Confirm".equalsIgnoreCase(action)) {
            return confirmOrderAndUpdateProductQuantity(orderId);
        }
        return updated;
    }

    // ✅ Get total sales for a date range
    public double getTotalSalesForDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) throws SQLException {
        return orderDAO.getTotalSalesForDateRange(startDate, endDate, storeNumber);
    }

    // ✅ Get sales summary (daily, weekly, monthly)
    public Map<String, Double> getSalesSummary(Integer storeNumber) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate monthStart = today.withDayOfMonth(1);

        double todaySales = getTotalSalesForDateRange(today, today, storeNumber);
        double weekSales = getTotalSalesForDateRange(weekStart, today, storeNumber);
        double monthSales = getTotalSalesForDateRange(monthStart, today, storeNumber);

        return Map.of(
            "todaySales", todaySales,
            "weekSales", weekSales,
            "monthSales", monthSales
        );
    }

    // ✅ Get today's orders
    public List<OrderSummaryDTO> getTodayOrders(Integer storeNumber) throws SQLException {
        LocalDate today = LocalDate.now();
        return orderDAO.findTodayOrders(today, storeNumber);
    }

    // ✅ Get confirmed orders by date range
    public List<OrderSummaryDTO> getConfirmedOrdersByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) throws SQLException {
        return orderDAO.findConfirmedOrdersByDateRange(startDate, endDate, storeNumber);
    }

    // ✅ Get minimal order details (filtered by store)
    public List<OrderMinimalSummaryDTO> getOrdersWithMinimalItemDetails(Integer storeNumber) throws SQLException {
        return orderDAO.findOrdersWithMinimalItemDetails(storeNumber);
    }

    // ✅ Get profit by date range
    public List<Map<String, Object>> getProfitByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber, String category, String productName) throws SQLException {
        return orderDAO.getProfitByDateRange(startDate, endDate, storeNumber, category, productName);
    }

    // ✅ Get sales data for charts
    public Map<String, List<Object>> getSalesData(String filter, Integer storeNumber) throws SQLException {
        return orderDAO.getSalesData(filter, storeNumber);
    }

    // ✅ Mark order as read
    public boolean markOrderAsRead(int orderId) {
        return orderDAO.markOrderAsRead(orderId);
    }

    // Get sales data for a date range
    public List<Map<String, Object>> getTotalSalesByDate(LocalDate startDate, LocalDate endDate) {
        return orderDAO.getTotalSalesByDate(startDate, endDate);
    }
}




/*package com.walid.backend.Service;

import com.walid.backend.DAO.OrderDAO;
import com.walid.backend.Model.Order;
import com.walid.backend.Model.OrderDTO;
import com.walid.backend.Model.OrderDetails;
import com.walid.backend.Model.OrderItem;
import com.walid.backend.Model.OrderMinimalSummaryDTO;
//import com.walid.backend.Model.AdditionalDiscount;
import com.walid.backend.Model.OrderSummaryDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.sql.Date;
import java.sql.Time;


public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderDAO orderDAO;

    // Constructor to inject the DAO
    public OrderService(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

  
    // Method to create a new order
    // OrderService.java
public Order createOrder(Order order, List<OrderItem> orderItems) throws SQLException {
    // Set the current date and time for the order
    order.setOrderDate(new java.sql.Date(System.currentTimeMillis()));
    order.setOrderTime(new java.sql.Time(System.currentTimeMillis()));

    // Save the order with its items
    Order createdOrder = orderDAO.saveOrder(order, orderItems); // Pass the delivery fee
    logger.info("Order created successfully: {}", createdOrder);

    return createdOrder;
}




        // Retrieve an order by its ID with a check
        public Order getOrderById(int orderId) throws SQLException {
            Order order = orderDAO.findOrderById(orderId);
            if (order == null) {
                logger.warn("Order with ID {} not found", orderId);
            }
            return order;
        }


      



        
        public List<Order> getAllOrders() throws SQLException {
            List<Order> orders = orderDAO.findAllOrders();
            if (orders.isEmpty()) {
                logger.warn("No orders found in the database.");
            }
            return orders;
        }
        



        
     

public double getTotalSalesForDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) throws SQLException {
    return orderDAO.getTotalSalesForDateRange(startDate, endDate, storeNumber);
}



public Map<String, Double> getSalesSummary(Integer storeNumber) throws SQLException {
    LocalDate today = LocalDate.now();
    LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1); // Start of the week (Monday)
    LocalDate monthStart = today.withDayOfMonth(1); // Start of the month

    double todaySales = getTotalSalesForDateRange(today, today, storeNumber);
    double weekSales = getTotalSalesForDateRange(weekStart, today, storeNumber);
    double monthSales = getTotalSalesForDateRange(monthStart, today, storeNumber);

    return Map.of(
        "todaySales", todaySales,
        "weekSales", weekSales,
        "monthSales", monthSales
    );
}



public boolean deleteOrder(int orderId) throws SQLException {
    return orderDAO.deleteOrder(orderId);
}


public boolean updateOrder(int orderId, OrderDTO orderDTO) throws SQLException {
    // Convert DTO to Order and OrderItems
    Order order = new Order();
    order.setOrderId(orderId);
    order.setOrderDate(Date.valueOf(orderDTO.getDate())); // Expects 'YYYY-MM-DD'
    order.setOrderTime(Time.valueOf(orderDTO.getTime() + ":00")); // Expects 'HH:mm'
    order.setDelivery(orderDTO.getDelivery()); // Set delivery price from DTO

    List<OrderItem> orderItems = convertDtoToOrderItems(orderDTO.getOrderItems());

    // Update the order in the database
    return orderDAO.updateOrder(order, orderItems);
}



private List<OrderItem> convertDtoToOrderItems(List<OrderDTO.OrderItemDTO> orderItemDTOs) {
    return orderItemDTOs.stream().map(dto -> {
        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setPromoId(dto.getPromoId());  // Ensure promoId is set correctly
        item.setDiscountPercentage(dto.getDiscountPercentage());
        item.setDiscountAmount(dto.getDiscountAmount());
        item.setFinalPrice(dto.getFinalPrice());
        item.setAdditionalDiscountDescription(dto.getAdditionalDiscountDescription());
        return item;
    }).collect(Collectors.toList());
}


public List<OrderSummaryDTO> getTodayOrders(Integer storeNumber) throws SQLException {
    LocalDate today = LocalDate.now();
    return orderDAO.findTodayOrders(today, storeNumber);
}







        // Retrieve order details by ID with a null check
        public OrderDetails getOrderDetailsById(int orderId) throws SQLException {
            OrderDetails orderDetails = orderDAO.findOrderDetailsById(orderId);
            if (orderDetails == null) {
                logger.warn("Order details not found for ID {}", orderId);
            }
            return orderDetails;
        }

    // Update order action
    public boolean updateOrderAction(int orderId, String action) throws SQLException {
        return orderDAO.updateOrderAction(orderId, action);
    }

    public boolean confirmOrderAndUpdateProductQuantity(int orderId) {
        try {
            return orderDAO.confirmOrderAndUpdateProductQuantity(orderId);
        } catch (SQLException e) {
            logger.error("Error confirming order ID {}: {}", orderId, e.getMessage());
            return false;
        }
    }
    

    // Get sales data for a date range
    public List<Map<String, Object>> getTotalSalesByDate(LocalDate startDate, LocalDate endDate) {
        return orderDAO.getTotalSalesByDate(startDate, endDate);
    }

    // Mark order as read
    public boolean markOrderAsRead(int orderId) {
        return orderDAO.markOrderAsRead(orderId);

    }


//for the Chart 




public Map<String, List<Object>> getSalesData(String filter, Integer storeNumber) throws SQLException {
    return orderDAO.getSalesData(filter, storeNumber);
}

// In OrderService.java

public List<OrderSummaryDTO> getConfirmedOrdersByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber) throws SQLException {
    return orderDAO.findConfirmedOrdersByDateRange(startDate, endDate, storeNumber);
}




// In OrderService.java
public List<OrderMinimalSummaryDTO> getOrdersWithMinimalItemDetails(Integer storeNumber) throws SQLException {
    return orderDAO.findOrdersWithMinimalItemDetails(storeNumber);
}

public List<Map<String, Object>> getProfitByDateRange(LocalDate startDate, LocalDate endDate, Integer storeNumber, String category, String productName) throws SQLException {
    return orderDAO.getProfitByDateRange(startDate, endDate, storeNumber, category, productName);
}


}*/
