package com.example.testAI.testAIRagVectorDB.code.Exceptions;

public class MedicineNotFoundException extends RuntimeException {
    public MedicineNotFoundException(String message) {
        super(message);
    }
}