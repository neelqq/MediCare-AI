package com.example.testAI.testAIRagVectorDB.code.Service;

import com.example.testAI.testAIRagVectorDB.code.Entity.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final MongoTemplate mongoTemplate;

    public OrderService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String placeOrder(String medicineName, int quantity) {
        Order order = new Order();
        order.setMedicineName(medicineName);
        order.setQuantity(quantity);
        order.setStatus("PENDING");
        order.setOrderTime(LocalDateTime.now());
        mongoTemplate.save(order);
        return "Order placed for: " + medicineName + " | Quantity: " + quantity + " | Status: PENDING";
    }

    public List<Order> getAllOrders() {
        return mongoTemplate.findAll(Order.class);
    }
}