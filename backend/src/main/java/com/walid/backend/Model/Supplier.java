package com.walid.backend.Model;

import java.time.LocalDate;
public class Supplier {
    private int supplierId;
    private String supplierName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private double totalAmountPaid;
    private LocalDate lastShipmentDate;
    private String status;
    private String notes;


    // Getters and Setters
    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    

    public String getEmail() { // Add this getter
        return email;
    }

    public void setEmail(String email) { // Add this setter
        this.email = email;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public LocalDate getLastShipmentDate() {
        return lastShipmentDate;
    }

    public void setLastShipmentDate(LocalDate lastShipmentDate) {
        this.lastShipmentDate = lastShipmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
