package com.walid.backend.Model;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Order {
    private int orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date orderDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time orderTime;
    private String status;
    private String action;
    private double delivery;
    private Integer customerId; // ✅ Added field for customer tracking

    // Default constructor
    public Order() {}

    // Constructor used in DAO's find methods
    public Order(int orderId, Date orderDate, Time orderTime, String status, String action, double delivery, Integer customerId) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.status = status;
        this.action = action;
        this.delivery = delivery;
        this.customerId = customerId;
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public Time getOrderTime() { return orderTime; }
    public void setOrderTime(Time orderTime) { this.orderTime = orderTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public double getDelivery() { return delivery; }
    public void setDelivery(double delivery) { this.delivery = delivery; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", orderTime=" + orderTime +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                ", delivery=" + delivery +
                ", customerId=" + customerId + // ✅ Added toString update
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return orderId == order.orderId &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(orderTime, order.orderTime) &&
                Objects.equals(status, order.status) &&
                Objects.equals(action, order.action) &&
                Objects.equals(delivery, order.delivery) &&
                Objects.equals(customerId, order.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderDate, orderTime, status, action, delivery, customerId);
    }
}



/*package com.walid.backend.Model;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Order {
    private int orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date orderDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private Time orderTime;
    private String status;
    private String action;
    private double delivery;


    // Default constructor
    public Order() {
    }

    // Constructor used in DAO's find methods
    public Order(int orderId, Date orderDate, Time orderTime, String status, String action, double delivery) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
        this.status = status;
        this.action = action;
        this.delivery = delivery;
    }


    // Getters and setters for all fields
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Time getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Time orderTime) {
        this.orderTime = orderTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getDelivery() {
        return delivery;
    }

    public void setDelivery(double delivery) {
        this.delivery = delivery;
    }


    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderDate=" + orderDate +
                ", orderTime=" + orderTime +
                ", status='" + status + '\'' +
                ", action='" + action + '\'' +
                ", delivery='" + delivery + '\'' +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;
        return orderId == order.orderId &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(orderTime, order.orderTime) &&
                Objects.equals(status, order.status) &&
                Objects.equals(action, order.action) &&
                Objects.equals(delivery, order.delivery) 
                ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderDate, orderTime, status, action,delivery);
    }
}*/
