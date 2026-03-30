package com.example.testAI.testAIRagVectorDB.code.Service;

import com.example.testAI.testAIRagVectorDB.code.Entity.CartItem;
import com.example.testAI.testAIRagVectorDB.code.Entity.Medicine;
import com.example.testAI.testAIRagVectorDB.code.Entity.Order;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.InvalidRequestException;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.MedicineNotFoundException;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.OutOfStockException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartService {

    private final MongoTemplate mongoTemplate;
    private final Map<String, List<CartItem>> carts = new ConcurrentHashMap<>();

    public CartService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<CartItem> getCart(String username) {
        return carts.getOrDefault(username, new ArrayList<>());
    }

    public String addToCart(String username, String medicineId) {
        Medicine medicine = mongoTemplate.findById(medicineId, Medicine.class);
        if (medicine == null) {
            throw new MedicineNotFoundException("Medicine not found with id: " + medicineId);
        }
        if (medicine.getStockQuantity() == 0) {
            throw new OutOfStockException(medicine.getName());
        }

        List<CartItem> cart = carts.getOrDefault(username, new ArrayList<>());

        for (CartItem item : cart) {
            if (item.getMedicineId().equals(medicineId)) {
                if (item.getQuantity() >= medicine.getStockQuantity()) {
                    throw new InvalidRequestException("Not enough stock available");
                }
                item.setQuantity(item.getQuantity() + 1);
                carts.put(username, cart);
                return "Quantity updated: " + medicine.getName();
            }
        }

        cart.add(new CartItem(
                medicineId,
                medicine.getName(),
                1,
                medicine.getStockQuantity()
        ));
        carts.put(username, cart);
        return "Added to cart: " + medicine.getName();
    }

    public String removeFromCart(String username, String medicineId) {
        List<CartItem> cart = carts.getOrDefault(username, new ArrayList<>());
        cart.removeIf(item -> item.getMedicineId().equals(medicineId));
        carts.put(username, cart);
        return "Removed from cart";
    }

    public String buyAll(String username) {
        List<CartItem> cart = carts.getOrDefault(username, new ArrayList<>());
        if (cart.isEmpty()) {
            throw new InvalidRequestException("Cart is empty");
        }

        for (CartItem item : cart) {
            Medicine medicine = mongoTemplate.findById(item.getMedicineId(), Medicine.class);
            if (medicine == null) {
                throw new MedicineNotFoundException("Medicine not found: " + item.getMedicineName());
            }
            if (medicine.getStockQuantity() < item.getQuantity()) {
                throw new OutOfStockException(medicine.getName());
            }

            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("id").is(item.getMedicineId())),
                    new Update().inc("stockQuantity", -item.getQuantity()),
                    Medicine.class
            );

            Order order = new Order();
            order.setMedicineName(item.getMedicineName());
            order.setQuantity(item.getQuantity());
            order.setStatus("CONFIRMED");
            order.setOrderTime(LocalDateTime.now());
            mongoTemplate.save(order);
        }

        carts.remove(username);
        return "Purchase successful! " + cart.size() + " item(s) ordered.";
    }

    public String clearCart(String username) {
        carts.remove(username);
        return "Cart cleared";
    }
}
