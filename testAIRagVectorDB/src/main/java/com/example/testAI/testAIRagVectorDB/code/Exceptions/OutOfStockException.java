package com.example.testAI.testAIRagVectorDB.code.Exceptions;

public class OutOfStockException extends RuntimeException {
    private final String medicineName;

    public OutOfStockException(String medicineName) {
        super("Medicine is out of stock: " + medicineName);
        this.medicineName = medicineName;
    }

    public String getMedicineName() {
        return medicineName;
    }
}