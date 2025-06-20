package com.walid.backend.Model;

import java.time.LocalDate;

public class SalesHistory {
    private LocalDate orderDate;
    private int productId;
    private int totalSold;

    public SalesHistory(LocalDate orderDate, int productId, int totalSold) {
        this.orderDate = orderDate;
        this.productId = productId;
        this.totalSold = totalSold;
    }

    public LocalDate getOrderDate() { return orderDate; }
    public int getProductId() { return productId; }
    public int getTotalSold() { return totalSold; }
}
