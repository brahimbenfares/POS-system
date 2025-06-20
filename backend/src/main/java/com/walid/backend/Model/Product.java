package com.walid.backend.Model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Product {
    private int id;
    private String barcode; // New field for Barcode
    private String name;
    private String description;
    private BigDecimal salesPrice;
    private BigDecimal purchasePrice;
    private int quantity;
    private String category;
    private String vendor;
    private Timestamp purchaseDate;
    private Integer storeNumber; // Changed from Timestamp to Integer
    private Timestamp createdAt;
    private boolean active;

    // Full Constructor including Barcode
    public Product(int id, String barcode, String name, String description, BigDecimal salesPrice,
                  BigDecimal purchasePrice, int quantity, String category, String vendor,
                  Timestamp purchaseDate, int storeNumber, Timestamp createdAt, boolean active) {
        this.id = id;
        this.barcode = barcode;
        this.name = name;
        this.description = description;
        this.salesPrice = salesPrice;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.category = category;
        this.vendor = vendor;
        this.purchaseDate = purchaseDate;
        this.storeNumber = storeNumber;
        this.createdAt = createdAt;
        this.active = active;
    }

    // Constructor without ID and Barcode (for new entries where ID is auto-generated and Barcode is optional)
    public Product(String name, String description, BigDecimal salesPrice,
                  BigDecimal purchasePrice, int quantity, String category, String vendor,
                  Timestamp purchaseDate, int storeNumber, Timestamp createdAt, boolean active) {
        this.name = name;
        this.description = description;
        this.salesPrice = salesPrice;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
        this.category = category;
        this.vendor = vendor;
        this.purchaseDate = purchaseDate;
        this.storeNumber = storeNumber;
        this.createdAt = createdAt;
        this.active = active;
    }

    // No-Arg Constructor
    public Product() {
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ... [Other existing getters and setters remain unchanged] ...

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSalesPrice() {
        return salesPrice;
    }

    public void setSalesPrice(BigDecimal salesPrice) {
        this.salesPrice = salesPrice;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

    public Timestamp getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Timestamp purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getStoreNumber() {
        return storeNumber;
    }
    
    public void setStoreNumber(Integer storeNumber) {
        this.storeNumber = storeNumber;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
