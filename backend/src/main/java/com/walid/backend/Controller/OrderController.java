package com.walid.backend.Controller;

import com.walid.backend.Service.OrderService;
import com.walid.backend.Model.Order;
import com.walid.backend.Model.OrderDTO;
import com.walid.backend.Model.OrderDetails;
import com.walid.backend.Model.OrderItem;
import com.walid.backend.Model.OrderMinimalSummaryDTO;
import com.walid.backend.Model.OrderSummaryDTO;

import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    // Constructor to inject the service
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }




    
    // Handler to create a new order with optional additional discounts
/*public void createOrderHandler(Context ctx) {
    try {
        // Deserialize JSON into OrderDTO
        OrderDTO orderDTO = ctx.bodyAsClass(OrderDTO.class);
        logger.info("Received OrderDTO: {}", orderDTO);

        // Validate order items
        if (orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
            ctx.status(400).result("Order items are missing");
            return;
        }

        // Convert DTO to Order and OrderItems
        Order order = new Order();
        order.setDelivery(orderDTO.getDelivery()); // Set delivery fee from DTO
        List<OrderItem> orderItems = convertDtoToOrderItems(orderDTO.getOrderItems());

        // Create the order using the service
        Order createdOrder = orderService.createOrder(order, orderItems);
        logger.info("Created Order: {}", createdOrder);

        // Return success response
        ctx.status(201).json(Map.of("orderId", createdOrder.getOrderId()));
    } catch (Exception e) {
        logger.error("Error creating order", e);
        ctx.status(500).json(Map.of("error", "Error creating order", "message", e.getMessage()));
    }
}*/

    // ✅ Handler to create a new order with customerId
    public void createOrderHandler(Context ctx) {
        try {
            OrderDTO orderDTO = ctx.bodyAsClass(OrderDTO.class);
            logger.info("Received OrderDTO: {}", orderDTO);

            // Validate order items
            if (orderDTO.getOrderItems() == null || orderDTO.getOrderItems().isEmpty()) {
                ctx.status(400).result("Order items are missing");
                return;
            }

            // Convert DTO to Order and OrderItems
            Order order = new Order();
            order.setDelivery(orderDTO.getDelivery());
            order.setCustomerId(orderDTO.getCustomerId()); // ✅ Ensure customerId is included
            List<OrderItem> orderItems = convertDtoToOrderItems(orderDTO.getOrderItems());

            Order createdOrder = orderService.createOrder(order, orderItems);
            logger.info("Created Order: {}", createdOrder);

            ctx.status(201).json(Map.of("orderId", createdOrder.getOrderId()));
        } catch (Exception e) {
            logger.error("Error creating order", e);
            ctx.status(500).json(Map.of("error", "Error creating order", "message", e.getMessage()));
        }
    }



    

        // ✅ Handler to get orders by customerId
        public void getOrdersByCustomerIdHandler(Context ctx) {
            try {
                int customerId = Integer.parseInt(ctx.pathParam("customerId"));
                List<Order> orders = orderService.getOrdersByCustomerId(customerId);
    
                if (orders.isEmpty()) {
                    ctx.status(404).json(Map.of("message", "No orders found for this customer"));
                } else {
                    ctx.json(orders);
                }
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("error", "Invalid customer ID format"));
            } catch (Exception e) {
                logger.error("Error fetching orders by customerId", e);
                ctx.status(500).json(Map.of("error", "Error retrieving orders", "message", e.getMessage()));
            }
        }
    
    
// Helper method to convert DTO to OrderItem objects
/*private List<OrderItem> convertDtoToOrderItems(List<OrderDTO.OrderItemDTO> orderItemDTOs) {
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
}*/

    // ✅ Helper method to convert DTO to OrderItem objects
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


        
    

        public void getAllOrdersHandler(Context ctx) {
           
            try {
                logger.info("Fetching all orders from the service...");
                List<Order> orders = orderService.getAllOrders();
                logger.info("Service returned {} orders.", orders.size());
        
                if (orders.isEmpty()) {
                    ctx.status(404).result("No orders found");
                    logger.info("No orders found.");
                } else {
                    ctx.status(200).json(orders);
                    logger.info("Successfully returned {} orders.", orders.size());
                }
            } catch (Exception e) {
                logger.error("Exception occurred while retrieving orders: ", e);
                ctx.status(500).json(Map.of("error", "Error retrieving orders", "message", e.getMessage()));
            }
        }

            // ✅ Handler to get all unread orders (including customerId)
    public void getAllUnreadOrdersHandler(Context ctx) {
        try {
            List<Order> unreadOrders = orderService.getAllUnreadOrders();
            if (unreadOrders.isEmpty()) {
                ctx.status(404).json(Map.of("message", "No unread orders found"));
            } else {
                ctx.json(unreadOrders);
            }
        } catch (Exception e) {
            logger.error("Error fetching unread orders", e);
            ctx.status(500).json(Map.of("error", "Error fetching unread orders", "message", e.getMessage()));
        }
    }

        



        /*public void updateOrderHandler(Context ctx) {
           
            try {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));
                OrderDTO orderDTO = ctx.bodyAsClass(OrderDTO.class);
        
                // Update the order using the service
                boolean updated = orderService.updateOrder(orderId, orderDTO);
        
                if (updated) {
                    ctx.status(200).result("Order updated successfully.");
                } else {
                    ctx.status(404).result("Order not found.");
                }
            } catch (Exception e) {
                logger.error("Error updating order", e);
                ctx.status(500).json(Map.of("error", "Error updating order", "message", e.getMessage()));
            }
        }*/

            // ✅ Handler to update an order (now includes customerId)
    public void updateOrderHandler(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            OrderDTO orderDTO = ctx.bodyAsClass(OrderDTO.class);

            // Ensure customerId is included
            if (orderDTO.getCustomerId() == null) {
                logger.warn("Updating order without customerId.");
            }

            boolean updated = orderService.updateOrder(orderId, orderDTO);
            if (updated) {
                ctx.status(200).result("Order updated successfully.");
            } else {
                ctx.status(404).result("Order not found.");
            }
        } catch (Exception e) {
            logger.error("Error updating order", e);
            ctx.status(500).json(Map.of("error", "Error updating order", "message", e.getMessage()));
        }
    }


        // ✅ Handler to update order action (automatically confirms order if needed)
        public void updateOrderActionHandler(Context ctx) {
            try {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));
                String action = ctx.bodyAsClass(Map.class).get("action").toString();
    
                boolean updated = orderService.updateOrderAction(orderId, action);
    
                if (updated) {
                    if ("Confirm".equalsIgnoreCase(action)) {
                        orderService.confirmOrderAndUpdateProductQuantity(orderId);
                    }
                    ctx.status(200).json(Map.of("message", "Order action updated successfully."));
                } else {
                    ctx.status(404).json(Map.of("error", "Order not found or action not updated"));
                }
            } catch (Exception e) {
                logger.error("Error updating order action", e);
                ctx.status(500).json(Map.of("error", "Error updating order action", "message", e.getMessage()));
            }
        }
    

        
        public void deleteOrderHandler(Context ctx) {
            
            try {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));
                boolean deleted = orderService.deleteOrder(orderId);
        
                if (deleted) {
                    ctx.status(200).result("Order deleted successfully.");
                } else {
                    ctx.status(404).result("Order not found.");
                }
            } catch (Exception e) {
                logger.error("Error deleting order", e);
                ctx.status(500).json(Map.of("error", "Error deleting order", "message", e.getMessage()));
            }
        }
        
        

        public void getSalesSummaryHandler(Context ctx) {
            
            try {
                String storeNumberParam = ctx.queryParam("storeNumber");
                Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;
        
                Map<String, Double> salesSummary = orderService.getSalesSummary(storeNumber);
                ctx.json(salesSummary);
            } catch (Exception e) {
                logger.error("Error getting sales summary", e);
                ctx.status(500).json(Map.of("error", "Error getting sales summary", "message", e.getMessage()));
            }
        }
        



        public void getTodayOrdersHandler(Context ctx) {
            
            try {
                String storeNumberParam = ctx.queryParam("storeNumber");
                Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;
        
                List<OrderSummaryDTO> todayOrders = orderService.getTodayOrders(storeNumber);
                ctx.json(todayOrders);
            } catch (Exception e) {
                logger.error("Error getting today's orders", e);
                ctx.status(500).json(Map.of("error", "Error getting today's orders", "message", e.getMessage()));
            }
        }
        
        

        

    // Retrieve order details by ID
    public void getOrderByIdHandler(Context ctx) {
        
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            OrderDetails orderDetails = orderService.getOrderDetailsById(orderId);

            if (orderDetails != null) {
                ctx.json(orderDetails);
            } else {
                ctx.status(404).result("Order not found");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("error", "Invalid order ID format. Must be a number."));
        } catch (Exception e) {
            logger.error("Error retrieving order", e);
            ctx.status(500).json(Map.of("error", "Error retrieving order", "message", e.getMessage()));
        }
    }


    // Handler to mark an order as read
    public void markOrderAsReadHandler(Context ctx) {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("orderId"));
            if (orderService.markOrderAsRead(orderId)) {
                ctx.result("Order marked as read");
            } else {
                ctx.status(404).result("Order not found or already marked as read");
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid order ID format");
        } catch (Exception e) {
            logger.error("Error updating order status", e);
            ctx.status(500).json(Map.of("error", "Error updating order status", "message", e.getMessage()));
        }
    }

    // Confirm an order and update product quantities
        public void confirmOrderHandler(Context ctx) {
           
            try {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));

                if (orderService.confirmOrderAndUpdateProductQuantity(orderId)) {
                    ctx.result("Order confirmed and product quantities updated");
                } else {
                    ctx.status(404).json(Map.of("error", "Order not found or insufficient stock"));
                }
            } catch (NumberFormatException e) {
                ctx.status(400).json(Map.of("error", "Invalid order ID format. Must be a number."));
            } catch (Exception e) {
                logger.error("Error confirming order", e);
                ctx.status(500).json(Map.of("error", "Error confirming order", "message", e.getMessage()));
            }
        }


    // Handler to retrieve total sales by date range
    public void getTotalSalesByDateHandler(Context ctx) {
        
        try {
            String start = ctx.queryParam("startDate");
            String end = ctx.queryParam("endDate");
    
            LocalDate startDate = LocalDate.parse(start);
            LocalDate endDate = LocalDate.parse(end);
    
            if (endDate.isBefore(startDate)) {
                ctx.status(400).json(Map.of("error", "End date cannot be before start date"));
                return;
            }
    
            List<Map<String, Object>> salesData = orderService.getTotalSalesByDate(startDate, endDate);
            ctx.json(Map.of("startDate", startDate, "endDate", endDate, "salesData", salesData));
        } catch (java.time.format.DateTimeParseException e) {
            ctx.status(400).json(Map.of("error", "Invalid date format. Please use YYYY-MM-DD."));
        } catch (Exception e) {
            logger.error("Error retrieving sales data", e);
            ctx.status(500).json(Map.of("error", "Error retrieving sales data", "message", e.getMessage()));
        }
    }
    

    
    
    

public void getSalesDataHandler(Context ctx) {
    
    try {
        String filter = ctx.queryParam("filter");
        String storeNumberParam = ctx.queryParam("storeNumber");
        Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;

        // Validate filter as before...

        // Fetch sales data based on the filter and store number
        Map<String, List<Object>> salesData = orderService.getSalesData(filter, storeNumber);
        ctx.json(salesData);
    } catch (Exception e) {
        logger.error("Error fetching sales data", e);
        ctx.status(500).json(Map.of("error", "Internal server error", "message", e.getMessage()));
    }
}





// In OrderController.java

public void getConfirmedOrdersByDateRangeHandler(Context ctx) {
    
    try {
        String startDateStr = ctx.queryParam("startDate");
        String endDateStr = ctx.queryParam("endDate");
        String storeNumberParam = ctx.queryParam("storeNumber");
        Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;

        if (startDateStr == null || endDateStr == null) {
            ctx.status(400).json(Map.of("error", "startDate and endDate query parameters are required"));
            return;
        }

        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        if (startDate.isAfter(endDate)) {
            ctx.status(400).json(Map.of("error", "startDate must be before or equal to endDate"));
            return;
        }

        List<OrderSummaryDTO> salesData = orderService.getConfirmedOrdersByDateRange(startDate, endDate, storeNumber);

        if (salesData.isEmpty()) {
            ctx.status(404).json(Map.of("message", "No confirmed orders found in the specified date range."));
        } else {
            ctx.json(salesData);
        }

    } catch (DateTimeParseException e) {
        ctx.status(400).json(Map.of("error", "Invalid date format. Please use YYYY-MM-DD."));
    } catch (Exception e) {
        logger.error("Error retrieving sales data", e);
        ctx.status(500).json(Map.of("error", "Error retrieving sales data", "message", e.getMessage()));
    }
}



// In OrderController.java
public void getOrdersWithMinimalDetailsHandler(Context ctx) {
    try {
        String storeNumberParam = ctx.queryParam("storeNumber");
        Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;

        List<OrderMinimalSummaryDTO> orders = orderService.getOrdersWithMinimalItemDetails(storeNumber);
        ctx.json(orders);
    } catch (Exception e) {
        logger.error("Error fetching orders with minimal details", e);
        ctx.status(500).json(Map.of("error", "Error fetching orders", "message", e.getMessage()));
    }
}


public void getProfitByDateRangeHandler(Context ctx) {
    try {
        String startDateStr = ctx.queryParam("startDate");
        String endDateStr = ctx.queryParam("endDate");
        String storeNumberParam = ctx.queryParam("storeNumber");
        String category = ctx.queryParam("category");
        String productName = ctx.queryParam("productName");

        Integer storeNumber = (storeNumberParam != null && !storeNumberParam.isEmpty()) ? Integer.parseInt(storeNumberParam) : null;
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        if (startDate.isAfter(endDate)) {
            ctx.status(400).json(Map.of("error", "startDate must be before or equal to endDate"));
            return;
        }

        List<Map<String, Object>> profitData = orderService.getProfitByDateRange(startDate, endDate, storeNumber, category, productName);
        ctx.json(profitData);
    } catch (DateTimeParseException e) {
        ctx.status(400).json(Map.of("error", "Invalid date format. Please use YYYY-MM-DD."));
    } catch (Exception e) {
        logger.error("Error retrieving profit data", e);
        ctx.status(500).json(Map.of("error", "Error retrieving profit data", "message", e.getMessage()));
    }
}


}
