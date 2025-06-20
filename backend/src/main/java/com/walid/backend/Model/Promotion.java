package com.walid.backend.Model;

import java.time.LocalDateTime;
import java.util.ArrayList; // Add this import
import java.util.List;      // Add this import
import java.util.Objects;

public class Promotion {
    private int promoId;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double discountPercentage;
    private boolean active;

    private List<Integer> productIds; // Add this field

    // Default constructor
    public Promotion() {
        this.productIds = new ArrayList<>(); // Initialize the list
    }

    // Parameterized constructor
    public Promotion(int promoId, String description, LocalDateTime startDate, LocalDateTime endDate, double discountPercentage, boolean active) {
        this.promoId = promoId;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountPercentage = discountPercentage;
        this.active = active;
        this.productIds = new ArrayList<>(); // Initialize the list
    }

    // Getter and Setter for productIds
    public List<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Integer> productIds) {
        this.productIds = productIds != null ? productIds : new ArrayList<>();
    }


    // Getters and Setters
    public int getPromoId() {
        return promoId;
    }

    public void setPromoId(int promoId) {
        this.promoId = promoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

       // toString, equals, and hashCode methods
       @Override
       public String toString() {
           return "Promotion{" +
                   "promoId=" + promoId +
                   ", description='" + description + '\'' +
                   ", startDate=" + startDate +
                   ", endDate=" + endDate +
                   ", discountPercentage=" + discountPercentage +
                   ", active=" + active +
                   ", productIds=" + productIds +
                   '}';
       }
   
       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
   
           Promotion promotion = (Promotion) o;
           return promoId == promotion.promoId &&
                   Double.compare(promotion.discountPercentage, discountPercentage) == 0 &&
                   active == promotion.active &&
                   Objects.equals(description, promotion.description) &&
                   Objects.equals(startDate, promotion.startDate) &&
                   Objects.equals(endDate, promotion.endDate) &&
                   Objects.equals(productIds, promotion.productIds);
       }
   
       @Override
       public int hashCode() {
           return Objects.hash(promoId, description, startDate, endDate, discountPercentage, active, productIds);
       }
   }
   
