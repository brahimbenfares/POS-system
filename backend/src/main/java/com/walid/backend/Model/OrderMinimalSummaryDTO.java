package com.walid.backend.Model;

public class OrderMinimalSummaryDTO  {
    private int orderId;
    private String orderDateTime; // Combined date and time
    private double totalPrice;
    private String status;
    private String saleType;      // Wholesale or each
    private Integer storeNumber;  // Store number

    // Constructor for OrderMinimalSummaryDTO
    public OrderMinimalSummaryDTO(int orderId, String orderDateTime, double totalPrice, String status, String saleType, Integer storeNumber) {
        this.orderId = orderId;
        this.orderDateTime = orderDateTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.saleType = saleType;
        this.storeNumber = storeNumber;
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

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public Integer getStoreNumber() {
        return storeNumber;
    }

    public void setStoreNumber(Integer storeNumber) {
        this.storeNumber = storeNumber;
    }
}
