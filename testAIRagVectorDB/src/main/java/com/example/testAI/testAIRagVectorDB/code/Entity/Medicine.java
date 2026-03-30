package com.example.testAI.testAIRagVectorDB.code.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "medicines")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medicine {
    @Id
    private String id;
    private String name;
    private String usedFor;
    private String dosage;
    private String sideEffects;
    private String warnings;
    private int stockQuantity;
}