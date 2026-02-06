package com.rodrigobarbosa.order.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Document(collation = "orders")
public class Order {
    @Id
    private String id;
    private Customer customer;
    private List<OrderItem> orderItems;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Order() {
    }

    public Order(String id, Customer customer, List<OrderItem> orderItems, BigDecimal totalAmount, OrderStatus orderStatus, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.customer = customer;
        this.orderItems = orderItems;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
