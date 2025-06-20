package com.walid.backend.Model;

import java.util.List;

public class OrderDetails {
    private int orderID;
    private String orderDate;
    private String orderTime;
    private String status;
    private String action;
    private Double delivery;
    private Integer customerId; // ✅ Added customer tracking
    private List<OrderItem> orderItems;

    // Constructors
    public OrderDetails() {}

    // Getters and Setters
    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    public String getOrderTime() { return orderTime; }
    public void setOrderTime(String orderTime) { this.orderTime = orderTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Double getDelivery() { return delivery; }
    public void setDelivery(Double delivery) { this.delivery = delivery; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderID=" + orderID +
                ", orderDate='" + orderDate + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                ", delivery=" + delivery +
                ", customerId=" + customerId + // ✅ Updated toString method
                ", orderItems=" + orderItems +
                '}';
    }
}


/*package com.walid.backend.Model;

import java.util.List;

public class OrderDetails {
    private int orderID;
    private String orderDate;
    private String orderTime;
    private String status;
    private String action;
    private Double delivery;
    private List<OrderItem> orderItems; // Added list of detailed order items

    // Constructors
    public OrderDetails() {
    }

    // Getters and Setters
    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
    public Double getDelivery(){
        return delivery;
    }
    public void setDelivery(Double delivery){
        this.delivery=delivery;
    }
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderDetails{" +
                "orderID=" + orderID +
                ", orderDate='" + orderDate + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                ", delivery='" + delivery + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
}*/
