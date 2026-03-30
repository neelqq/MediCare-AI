package com.example.testAI.testAIRagVectorDB.code.Controller;

import com.example.testAI.testAIRagVectorDB.code.Entity.CartItem;
import com.example.testAI.testAIRagVectorDB.code.Service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<List<CartItem>> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(auth.getName()));
    }

    @PostMapping("/add/{medicineId}")
    public ResponseEntity<String> addToCart(@PathVariable String medicineId, Authentication auth) {
        return ResponseEntity.ok(cartService.addToCart(auth.getName(), medicineId));
    }

    @DeleteMapping("/remove/{medicineId}")
    public ResponseEntity<String> removeFromCart(@PathVariable String medicineId, Authentication auth) {
        return ResponseEntity.ok(cartService.removeFromCart(auth.getName(), medicineId));
    }

    @PostMapping("/buy")
    public ResponseEntity<String> buyAll(Authentication auth) {
        return ResponseEntity.ok(cartService.buyAll(auth.getName()));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication auth) {
        return ResponseEntity.ok(cartService.clearCart(auth.getName()));
    }
}
