package com.walid.backend.Service;

import com.walid.backend.AI.SalesForecastAI;
import com.walid.backend.DAO.ForecastDAO;
import com.walid.backend.Model.AISalesForecast;
import com.walid.backend.Model.SalesHistory;

import ai.onnxruntime.OrtException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ForecastService {

    private final SalesForecastAI aiModel;
    private final AISalesForecastDAO forecastDAO;  // 🔹 Create an instance of DAO

    public AISalesForecastService() throws OrtException {
        this.aiModel = new SalesForecastAI();
        this.forecastDAO = new AISalesForecastDAO(); // 🔹 Initialize DAO instance
    }

    // Method to predict sales and save to database
    public AISalesForecast predictAndSaveSales(int productId, int daysAhead) throws OrtException {
        double predictedSales = aiModel.predictSales(productId, daysAhead);

        // Create forecast object
        AISalesForecast forecast = new AISalesForecast(
            0, // Auto-increment ID
            productId,
            LocalDate.now().plusDays(daysAhead), // Future date
            predictedSales,
            95.0, // Placeholder confidence level
            LocalDateTime.now()
        );

        // Save forecast to DB
        boolean saved = AISalesForecastDAO.saveForecast(forecast);
        if (!saved) {
            throw new RuntimeException("Failed to save AI forecast to database.");
        }

        return forecast;
    }

    // Method to fetch past forecasts
    public List<AISalesForecast> getPastForecasts(int productId) {
        return AISalesForecastDAO.getForecastByProduct(productId);
    }

    // 🔹 FIXED: Use instance method instead of static reference
    @SuppressWarnings("static-access")
    public List<Map<String, Object>> validateForecasts(int productId) {
        return forecastDAO.compareForecastsWithActualSales(productId); // 🔹 Use instance method
    }

    // 🔹 FIXED: Use instance method instead of static reference
    public List<Map<String, Object>> identifyStockRisks() {
        return forecastDAO.identifyStockRisks(); // 🔹 Use instance method
    }

    // ✅ AI-Powered Dynamic Reordering System
public List<Map<String, Object>> getReorderRecommendations() {
    return forecastDAO.getReorderRecommendations();
}

// ✅ AI-Based Demand Trends Analysis
public List<Map<String, Object>> getDemandTrends() {
    return forecastDAO.getDemandTrends();
}

// ✅ AI-Powered Auto-Pricing & Discounts
public List<Map<String, Object>> getPriceSuggestions() {
    return forecastDAO.getPriceSuggestions();
}
// ✅ AI-Generated Sales Forecast Reports
public List<Map<String, Object>> getSalesForecastReport(String period) {
    return forecastDAO.getSalesForecastReport(period);
}



//*************************************************//
// ✅ AI-Based Customer Purchase Behavior         //
//*************************************************//
/*public List<Map<String, Object>> getCustomerTrends() {
    return forecastDAO.getCustomerTrends();
}*/
public List<Map<String, Object>> getCustomerTrends(String filter) {
    return forecastDAO.getCustomerTrends(filter);
}

//*************************************************//
// ✅ AI-Driven Stock Optimization & Waste Reduction //
//*************************************************//
public List<Map<String, Object>> getStockOptimization() {
    return forecastDAO.getStockOptimization();
}

public List<Map<String, Object>> getAutoReplenishment() {
    return forecastDAO.getAutoReplenishment();
}
public List<Map<String, Object>> getVendorRecommendations() {
    return forecastDAO.getVendorRecommendations();
}


public List<Map<String, Object>> getPersonalizedPromotions() {
    return forecastDAO.getPersonalizedPromotions();
}

public void updatePromotionsDaily() {
    forecastDAO.updatePromotionsDaily();
}

public void generateDailyForecasts() throws OrtException {
    List<SalesHistory> salesData = AISalesForecastDAO.getSalesHistory(); // Get past sales

    for (SalesHistory sale : salesData) {
        int productId = sale.getProductId();
        LocalDate forecastDate = LocalDate.now().plusDays(1); // Forecast for tomorrow

        // ✅ Check if forecast already exists for this product and date
        if (AISalesForecastDAO.forecastExists(productId, forecastDate)) {
            System.out.println("⚠️ Forecast already exists for Product ID: " + productId + " on " + forecastDate);
            continue; // Skip if forecast already exists
        }

        double predictedSales = aiModel.predictSales(productId, 1); // Predict for tomorrow

        // Save forecast
        AISalesForecast forecast = new AISalesForecast(
            0, // Auto-increment ID
            productId,
            forecastDate,
            predictedSales,
            95.0, // Confidence level
            LocalDateTime.now()
        );

        AISalesForecastDAO.saveForecast(forecast);
        System.out.println("✅ Forecast saved for Product ID: " + productId + " on " + forecastDate);
    }
}


    // ✅ Handle Multiple AI Reorder Requests
public boolean createReorders(List<Map<String, Object>> reorderList) {
    return forecastDAO.saveReorders(reorderList);
}

// ✅ Get all past reorders
public List<Map<String, Object>> getPastReorders() {
    return forecastDAO.getPastReorders();
}

// ✅ Get details of a specific reorder by date
public List<Map<String, Object>> getReorderDetails(String reorderDate) {
    return forecastDAO.getReorderDetails(reorderDate);
}


public boolean updateMultipleReorderStatuses(List<Map<String, Object>> updates) {
    return forecastDAO.updateMultipleReorderStatuses(updates);
}


public List<Map<String, Object>> getCustomerRecommendations(int customerId) {
    return forecastDAO.getCustomerRecommendations(customerId);
}
public Map<String, Object> getTopSellingProductToday() {
    Map<String, Object> result = forecastDAO.getTopSellingProductToday();
    if (result == null) {
        return Map.of("message", "No sales data available for today.");
    }
    return result;
}

public List<Map<String, Object>> detectFraudulentOrders() {
    List<Map<String, Object>> fraudOrders = forecastDAO.detectFraudulentOrders();
    if (fraudOrders.isEmpty()) {
        return List.of(Map.of("message", "No fraudulent activity detected in the past 7 days."));
    }
    return fraudOrders;
}



}
