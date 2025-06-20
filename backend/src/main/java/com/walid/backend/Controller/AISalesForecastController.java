package com.walid.backend.Controller;

import com.walid.backend.Service.AISalesForecastService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walid.backend.Model.AISalesForecast;
import ai.onnxruntime.OrtException;
import io.javalin.http.Handler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.net.URL;


public class AISalesForecastController {
    
    private AISalesForecastService forecastService; // Removed final
    
    

    public AISalesForecastController() {
        try {
            System.out.println("Initializing AISalesForecastService...");
            this.forecastService = new AISalesForecastService();  
            System.out.println("✅ AISalesForecastService initialized successfully.");
        } catch (OrtException e) {
            System.err.println("❌ Error initializing AISalesForecastService: " + e.getMessage());
            e.printStackTrace();  // Log detailed error
            this.forecastService = null;
        }
    }
    

    // API to predict sales and save it to DB
    public final Handler predictSales = ctx -> { 
        int productId = Integer.parseInt(ctx.pathParam("productId"));
        int daysAhead = Integer.parseInt(ctx.pathParam("daysAhead"));

        if (forecastService == null) {  // Check if service is properly initialized
            ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
            return;
        }

        try {
            AISalesForecast forecast = forecastService.predictAndSaveSales(productId, daysAhead);
            ctx.json(Map.of(
                "product_id", productId,
                "days_ahead", daysAhead,
                "predicted_sales", forecast.getPredictedSales(),
                "saved_to_db", true
            ));
        } catch (OrtException e) {
            ctx.status(500).json(Map.of("error", "Prediction failed.", "message", e.getMessage()));
        }
    };

    // API to get past AI forecasts
    public final Handler getForecasts = ctx -> { 
        int productId = Integer.parseInt(ctx.pathParam("productId"));

        if (forecastService == null) { // Check if service is properly initialized
            ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
            return;
        }

        List<AISalesForecast> forecasts = forecastService.getPastForecasts(productId);
        ctx.json(forecasts);
    };

// API to validate AI forecasts
public final Handler validateForecasts = ctx -> {
    int productId = Integer.parseInt(ctx.pathParam("productId"));

    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> comparisons = forecastService.validateForecasts(productId);
    ctx.json(comparisons);
};

    
// API to check stock risks
public final Handler checkStockRisks = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> risks = forecastService.identifyStockRisks();
    ctx.json(risks);
};



// ✅ API for AI-Powered Dynamic Reordering System
public final Handler getReorderRecommendations = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> recommendations = forecastService.getReorderRecommendations();
    ctx.json(recommendations);
};

// ✅ API for AI-Based Demand Trends Analysis
public final Handler getDemandTrends = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> trends = forecastService.getDemandTrends();
    ctx.json(trends);
};

// ✅ API for AI-Powered Auto-Pricing & Discounts
public final Handler getPriceSuggestions = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> priceSuggestions = forecastService.getPriceSuggestions();
    ctx.json(priceSuggestions);
};

// ✅ API for AI-Generated Sales Forecast Reports
public final Handler getSalesForecastReport = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    String period = ctx.queryParam("period"); // Default to weekly if not specified
    List<Map<String, Object>> report = forecastService.getSalesForecastReport(period);
    ctx.json(report);
};


//*************************************************//
// ✅ API for AI-Based Customer Purchase Behavior //
//*************************************************//
/*public final Handler getCustomerTrends = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> trends = forecastService.getCustomerTrends();
    ctx.json(trends);
};*/
public final Handler getCustomerTrends = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    // Get filter from request, default to "all" if not provided
    String filter = ctx.queryParam("filter");
    if (filter == null || filter.isEmpty()) {
        filter = "all";
    }

    // Fetch filtered trends
    List<Map<String, Object>> trends = forecastService.getCustomerTrends(filter);
    ctx.json(trends);
};


//*************************************************//
// ✅ API for AI-Driven Stock Optimization        //
//*************************************************//
public final Handler getStockOptimization = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> optimizations = forecastService.getStockOptimization();
    ctx.json(optimizations);
};



// ✅ API for AI Auto-Replenishment System
public final Handler getAutoReplenishment = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> replenishments = forecastService.getAutoReplenishment();
    ctx.json(replenishments);
};



// ✅ API for AI Supplier Recommendations
public final Handler getVendorRecommendations = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    List<Map<String, Object>> recommendations = forecastService.getVendorRecommendations();
    ctx.json(recommendations);
};




public final Handler getPersonalizedPromotions = ctx ->{
    List<Map<String, Object>> promotions = forecastService.getPersonalizedPromotions();
    ctx.json(promotions);
};

public final Handler updatePromotionsDaily = ctx -> {
    forecastService.updatePromotionsDaily();
    ctx.json(Map.of("message", "Promotions updated successfully"));
};

@SuppressWarnings("unchecked")
public final Handler createReorders = ctx -> {
    if (forecastService == null) {
        ctx.status(500).json(Map.of("error", "AI Service initialization failed."));
        return;
    }

    try {
        List<Map<String, Object>> reorderList = ctx.bodyAsClass(List.class);

        for (Map<String, Object> reorder : reorderList) {
            reorder.put("productId", Integer.parseInt(reorder.get("productId").toString()));
            reorder.put("supplierId", Integer.parseInt(reorder.get("supplierId").toString()));
            reorder.put("reorderQty", Integer.parseInt(reorder.get("reorderQty").toString()));
        }

        boolean success = forecastService.createReorders(reorderList);

        if (success) {
            ctx.json(Map.of("message", "✅ Reorder requests submitted successfully!"));
        } else {
            ctx.status(500).json(Map.of("error", "Failed to submit reorder requests."));
        }
    } catch (Exception e) {
        e.printStackTrace();
        ctx.status(500).json(Map.of("error", "Internal Server Error: " + e.getMessage()));
    }
};
// ✅ API to fetch all past reorders
public final Handler getPastReorders = ctx -> {
    List<Map<String, Object>> pastReorders = forecastService.getPastReorders();
    ctx.json(pastReorders);
};

// ✅ API to fetch reorder details by date
public final Handler getReorderDetails = ctx -> {
    String reorderDate = ctx.pathParam("date"); // Fetch `date` from URL
    List<Map<String, Object>> reorderDetails = forecastService.getReorderDetails(reorderDate);
    ctx.json(reorderDetails);
};

public final Handler updateAllReorderStatuses = ctx -> {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, List<Map<String, Object>>> requestBody = mapper.readValue(ctx.body(), new TypeReference<>() {});
    List<Map<String, Object>> updates = requestBody.get("updates");

    boolean success = forecastService.updateMultipleReorderStatuses(updates);
    
    if (success) {
        ctx.json(Map.of("message", "تم تحديث جميع الطلبات بنجاح!"));
    } else {
        ctx.status(500).json(Map.of("message", "❌ فشل تحديث الطلبات."));
    }
};


public Handler getCustomerRecommendations = ctx -> {
    int customerId = Integer.parseInt(ctx.pathParam("customerId"));
    ctx.json(forecastService.getCustomerRecommendations(customerId));
};

public final Handler getChatbotResponse = ctx -> {
    String userMessage = ctx.queryParam("message");

    System.out.println("🔍 Received Chatbot Query: " + userMessage);

    if (userMessage == null || userMessage.trim().isEmpty()) {
        ctx.json(Map.of("message", "❌ Please enter a valid question."));
        return;
    }

    String message = userMessage.toLowerCase();
    String response = "🤖 Sorry, I didn't understand that. Try asking about sales, stock, orders, or trends.";

    if (message.contains("top selling") || message.contains("best product") || message.contains("selling the most") || message.contains("top product")) {
        response = fetchAIData("/api/ai/customer-trends");
    } else if (message.contains("sales forecast") || message.contains("predict sales") || message.contains("future sales") || message.contains("next week")) {
        response = fetchAIData("/api/ai/sales-report");
    } else if (message.contains("low stock") || message.contains("check stock") || message.contains("stock level") || message.contains("running low")) {
        response = fetchAIData("/api/ai/check-stock-risks");
    } else if (message.contains("reorder") || message.contains("order more stock") || message.contains("reorder recommendation") || message.contains("need to restock")) {
        response = fetchAIData("/api/ai/reorder-recommendations");
    } else if (message.contains("price suggestion") || message.contains("discount") || message.contains("promotions") || message.contains("price change")) {
        response = fetchAIData("/api/ai/price-suggestions");
    } else if (message.contains("customer trend") || message.contains("customer behavior") || message.contains("most customers") || message.contains("best-selling items")) {
        response = fetchAIData("/api/ai/customer-trends");
    } else if (message.contains("fraud") || message.contains("suspicious order") || message.contains("fraud detection") || message.contains("look fraudulent")) {
        response = fetchAIData("/api/ai/fraud-detection");
    }

    ctx.json(Map.of("message", response));
};


// Helper method to fetch data from AI endpoints
private String fetchAIData(String apiUrl) {
    try {
        @SuppressWarnings("deprecation")
        URL url = new URL("http://localhost:8080" + apiUrl); // Ensure full API URL
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            return "❌ API request failed: " + conn.getResponseCode();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect();

        return response.toString(); // Return the actual response from the API
    } catch (Exception e) {
        return "❌ Error fetching data: " + e.getMessage();
    }
}



public Handler detectFraud = ctx -> {
    List<Map<String, Object>> fraudResults = forecastService.detectFraudulentOrders();
    ctx.json(fraudResults);
};




}
