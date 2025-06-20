package com.walid.backend.Model;

public class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private int quantity;
    private Integer promoId; // Nullable
    private double discountPercentage;
    private double discountAmount;
    private double finalPrice;

    // Additional fields
    private String productName;
    private double salesPrice;
    private String promotionDescription;
    private Double promoDiscountPercentage; // New field for promotion discount percentage

    // Additional discount fields
    private Double additionalDiscountPercentage; // Nullable
    private String additionalDiscountDescription; // Nullable
    private Double additionalDiscountAmount; // Nullable

    // Getters and Setters
    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(double finalPrice) {
        this.finalPrice = finalPrice;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(double salesPrice) {
        this.salesPrice = salesPrice;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }

    public Double getPromoDiscountPercentage() {
        return promoDiscountPercentage;
    }

    public void setPromoDiscountPercentage(Double promoDiscountPercentage) {
        this.promoDiscountPercentage = promoDiscountPercentage;
    }

    public Double getAdditionalDiscountPercentage() {
        return additionalDiscountPercentage;
    }

    public void setAdditionalDiscountPercentage(Double additionalDiscountPercentage) {
        this.additionalDiscountPercentage = additionalDiscountPercentage;
    }

    public String getAdditionalDiscountDescription() {
        return additionalDiscountDescription;
    }

    public void setAdditionalDiscountDescription(String additionalDiscountDescription) {
        this.additionalDiscountDescription = additionalDiscountDescription;
    }

    public Double getAdditionalDiscountAmount() {
        return additionalDiscountAmount;
    }

    public void setAdditionalDiscountAmount(Double additionalDiscountAmount) {
        this.additionalDiscountAmount = additionalDiscountAmount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", promoId=" + promoId +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", finalPrice=" + finalPrice +
                ", productName='" + productName + '\'' +
                ", salesPrice=" + salesPrice +
                ", promotionDescription='" + promotionDescription + '\'' +
                ", promoDiscountPercentage=" + promoDiscountPercentage +
                ", additionalDiscountPercentage=" + additionalDiscountPercentage +
                ", additionalDiscountDescription='" + additionalDiscountDescription + '\'' +
                ", additionalDiscountAmount=" + additionalDiscountAmount +
                '}';
    }
}
