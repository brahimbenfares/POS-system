package com.walid.backend.Model;

import java.sql.Timestamp;

public class AdditionalDiscount {
    private int discountId;
    private int orderItemId;
    private String description;
    private double discountPercentage;
    private double discountAmount;
    private Timestamp appliedAt;

    // Getters and Setters
    public int getDiscountId() {
        return discountId;
    }

    public void setDiscountId(int discountId){
        this.discountId = discountId;
    }

    public int getOrderItemId(){
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId){
        this.orderItemId = orderItemId;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public double getDiscountPercentage(){
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage){
        this.discountPercentage = discountPercentage;
    }

    public double getDiscountAmount(){
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount){
        this.discountAmount = discountAmount;
    }

    public Timestamp getAppliedAt(){
        return appliedAt;
    }

    public void setAppliedAt(Timestamp appliedAt){
        this.appliedAt = appliedAt;
    }

    @Override
    public String toString(){
        return "AdditionalDiscount{" +
                "discountId=" + discountId +
                ", orderItemId=" + orderItemId +
                ", description='" + description + '\'' +
                ", discountPercentage=" + discountPercentage +
                ", discountAmount=" + discountAmount +
                ", appliedAt=" + appliedAt +
                '}';
    }
}
