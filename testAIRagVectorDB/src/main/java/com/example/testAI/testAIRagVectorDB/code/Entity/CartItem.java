package com.example.testAI.testAIRagVectorDB.code.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String medicineId;
    private String medicineName;
    private int quantity;
    private int stockQuantity;
}