package com.walid.backend.AI;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class SalesForecastAI {
    private OrtEnvironment env;
    private OrtSession session;

    

    public SalesForecastAI() throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        Path modelPath = Paths.get("walid-backend/sales_forecast_model.onnx");
        
        System.out.println("🔍 Checking ONNX model at: " + modelPath.toAbsolutePath());
    
        if (!modelPath.toFile().exists()) {
            throw new RuntimeException("❌ Model file not found at: " + modelPath.toAbsolutePath());
        }
    
        this.session = env.createSession(modelPath.toString());
        System.out.println("✅ ONNX Model Loaded Successfully.");
    }
    

    public double predictSales(int productId, int futureDay) throws OrtException {
        // Create input data
        float[][] inputData = {{(float) futureDay, (float) productId}};

        // Create ONNX tensor
        try (OnnxTensor inputTensor = OnnxTensor.createTensor(env, inputData)) {
            // Run inference
            Map<String, OnnxTensor> inputs = Collections.singletonMap("input", inputTensor);
            OrtSession.Result result = session.run(inputs);
            
            // Extract prediction result
            float[][] prediction = (float[][]) result.get(0).getValue();
            
            return prediction[0][0];  // Return the forecasted sales value
        }
    }
}


/*public SalesForecastAI() throws OrtException {
        this.env = OrtEnvironment.getEnvironment();
        Path modelPath = Paths.get("walid-backend/sales_forecast_model.onnx");
        
        if (!modelPath.toFile().exists()) {
            throw new RuntimeException("Model file not found at: " + modelPath.toAbsolutePath());
        }

        this.session = env.createSession(modelPath.toString());
    }*/