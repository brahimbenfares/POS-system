package com.walid.backend.Model;

public class OrderSummaryDTO {
    private int orderId;
    private String orderDateTime; // Combined date and time
    private double totalPrice;
    private String status;

    // Constructors
    public OrderSummaryDTO() {}

    public OrderSummaryDTO(int orderId, String orderDateTime, double totalPrice, String status) {
        this.orderId = orderId;
        this.orderDateTime = orderDateTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
