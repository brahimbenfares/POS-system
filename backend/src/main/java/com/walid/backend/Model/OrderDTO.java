package com.walid.backend.Model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)  // This ensures unknown fields are ignored to prevent errors
public class OrderDTO {
    private String orderType;
    private String date; 
    private String time; 
    private Double delivery;
    private Integer customerId; // ✅ Added field for tracking customer
    @JsonAlias({"items", "orderItems"})  // Allow flexible JSON mapping
    private List<OrderItemDTO> orderItems;

    // Getters and Setters
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getDelivery() {
        return delivery;
    }

    public void setDelivery(Double delivery) {
        this.delivery = delivery;
    }

    public Integer getCustomerId() { // ✅ Added getter and setter for customerId
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderType='" + orderType + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", delivery=" + delivery +
                ", customerId=" + customerId +  // ✅ Included in toString
                ", orderItems=" + orderItems +
                '}';
    }

    // Inner class for Order Items
    public static class OrderItemDTO {
        private int productId;
        private int quantity;
        private Integer promoId;  // Nullable
        private double discountPercentage;
        private double discountAmount;
        private double finalPrice;  // Add this to match frontend
        private Double additionalDiscountPercentage;  // Nullable
        private String additionalDiscountDescription; // Nullable

        // Getters and Setters
        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Integer getPromoId() {
            return promoId;
        }

        public void setPromoId(Integer promoId) {
            this.promoId = promoId;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(double discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
        }

        public double getFinalPrice() {
            return finalPrice;
        }

        public void setFinalPrice(double finalPrice) {
            this.finalPrice = finalPrice;
        }

        public Double getAdditionalDiscountPercentage() {
            return additionalDiscountPercentage;
        }

        public void setAdditionalDiscountPercentage(Double additionalDiscountPercentage) {
            this.additionalDiscountPercentage = additionalDiscountPercentage;
        }

        public String getAdditionalDiscountDescription() {
            return additionalDiscountDescription;
        }

        public void setAdditionalDiscountDescription(String additionalDiscountDescription) {
            this.additionalDiscountDescription = additionalDiscountDescription;
        }

        @Override
        public String toString() {
            return "OrderItemDTO{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    ", promoId=" + promoId +
                    ", discountPercentage=" + discountPercentage +
                    ", discountAmount=" + discountAmount +
                    ", finalPrice=" + finalPrice +
                    ", additionalDiscountPercentage=" + additionalDiscountPercentage +
                    ", additionalDiscountDescription='" + additionalDiscountDescription + '\'' +
                    '}';
        }
    }
}


/*package com.walid.backend.Model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)  // This ensures unknown fields are ignored to prevent errors
public class OrderDTO {
    private String orderType;
    private String date; // Added this line
    private String time; // Added this line
    private Double delivery;
    @JsonAlias({"items", "orderItems"})  // Allow flexible JSON mapping
    private List<OrderItemDTO> orderItems;

    // Getters and Setters
    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    // Added getters and setters for 'date'
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Added getters and setters for 'time'
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<OrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }
    public Double getDelivery(){
        return delivery;
    }
    public void setDelivery(Double delivery){
        this.delivery=delivery;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderType='" + orderType + '\'' +
                ", date='" + date + '\'' +             // Added 'date' to toString
                ", time='" + time + '\'' +    
                ", delivery='" + delivery + '\'' +  
                ", orderItems=" + orderItems +
                '}';
    }

    // Inner class for Order Items
    public static class OrderItemDTO {
        private int productId;
        private int quantity;
        private Integer promoId;  // Nullable
        private double discountPercentage;
        private double discountAmount;
        private double finalPrice;  // Add this to match frontend
        private Double additionalDiscountPercentage;  // Nullable
        private String additionalDiscountDescription; // Nullable

        // Getters and Setters
        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public Integer getPromoId() {
            return promoId;
        }

        public void setPromoId(Integer promoId) {
            this.promoId = promoId;
        }

        public double getDiscountPercentage() {
            return discountPercentage;
        }

        public void setDiscountPercentage(double discountPercentage) {
            this.discountPercentage = discountPercentage;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }

        public void setDiscountAmount(double discountAmount) {
            this.discountAmount = discountAmount;
        }

        public double getFinalPrice() {
            return finalPrice;
        }

        public void setFinalPrice(double finalPrice) {
            this.finalPrice = finalPrice;
        }

        public Double getAdditionalDiscountPercentage() {
            return additionalDiscountPercentage;
        }

        public void setAdditionalDiscountPercentage(Double additionalDiscountPercentage) {
            this.additionalDiscountPercentage = additionalDiscountPercentage;
        }

        public String getAdditionalDiscountDescription() {
            return additionalDiscountDescription;
        }

        public void setAdditionalDiscountDescription(String additionalDiscountDescription) {
            this.additionalDiscountDescription = additionalDiscountDescription;
        }

        @Override
        public String toString() {
            return "OrderItemDTO{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    ", promoId=" + promoId +
                    ", discountPercentage=" + discountPercentage +
                    ", discountAmount=" + discountAmount +
                    ", finalPrice=" + finalPrice +
                    ", additionalDiscountPercentage=" + additionalDiscountPercentage +
                    ", additionalDiscountDescription='" + additionalDiscountDescription + '\'' +
                    '}';
        }
    }
}*/
