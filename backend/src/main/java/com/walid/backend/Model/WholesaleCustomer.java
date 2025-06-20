package com.walid.backend.Model;

public class WholesaleCustomer {
    private int customerId;
    private String customerName;
    private String phoneNumber;
    private double totalPurchases;
    private int loyaltyPoints;
    private String lastPurchaseDate;
    private String loyaltyStatus;
    private String notes;

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public double getTotalPurchases() { return totalPurchases; }
    public void setTotalPurchases(double totalPurchases) { this.totalPurchases = totalPurchases; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getLastPurchaseDate() { return lastPurchaseDate; }
    public void setLastPurchaseDate(String lastPurchaseDate) { this.lastPurchaseDate = lastPurchaseDate; }

    public String getLoyaltyStatus() { return loyaltyStatus; }
    public void setLoyaltyStatus(String loyaltyStatus) { this.loyaltyStatus = loyaltyStatus; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
