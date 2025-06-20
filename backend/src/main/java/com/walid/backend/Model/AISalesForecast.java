package com.walid.backend.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AISalesForecast {
    private int forecastId;
    private int productId;
    private LocalDate forecastDate; // Keep this as LocalDate
    private double predictedSales;
    private double confidenceLevel;
    private LocalDateTime createdAt;

    public AISalesForecast(int forecastId, int productId, LocalDate forecastDate, double predictedSales, double confidenceLevel, LocalDateTime createdAt) {
        this.forecastId = forecastId;
        this.productId = productId;
        this.forecastDate = forecastDate;
        this.predictedSales = predictedSales;
        this.confidenceLevel = confidenceLevel;
        this.createdAt = createdAt;
    }

    // Getters
    public int getForecastId() { return forecastId; }
    public int getProductId() { return productId; }
    
    // ✅ Return a formatted date string instead of LocalDate
    public String getForecastDate() {
        return forecastDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public double getPredictedSales() { return predictedSales; }
    public double getConfidenceLevel() { return confidenceLevel; }

    // ✅ Return formatted date-time string for createdAt
    public String getCreatedAt() {
        return createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Setters
    public void setForecastId(int forecastId) { this.forecastId = forecastId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    public void setPredictedSales(double predictedSales) { this.predictedSales = predictedSales; }
    public void setConfidenceLevel(double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
