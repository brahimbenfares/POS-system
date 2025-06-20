package com.walid.backend.Model;

import java.math.BigDecimal;

public class ProductWithPromotion {
    private int productId;
    private String productName;
    private BigDecimal salesPrice;
    private int quantity;
    private boolean active;

    // Promotion-related fields
    private Integer promoId;
    private Double discountPercentage;
    private String promotionDescription;

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getPromotionDescription() {
        return promotionDescription;
    }

    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }

    @Override
    public String toString() {
        return "ProductWithPromotion{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", salesPrice=" + salesPrice +
                ", quantity=" + quantity +
                ", promoId=" + promoId +
                ", discountPercentage=" + discountPercentage +
                ", promotionDescription='" + promotionDescription + '\'' +
                '}';
    }
}
