package com.example.testAI.testAIRagVectorDB.code.Service;

import com.example.testAI.testAIRagVectorDB.code.Entity.Medicine;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.MedicineNotFoundException;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MedicineIngestionService {

    private final VectorStore vectorStore;
    private final MongoTemplate mongoTemplate;

    public MedicineIngestionService(VectorStore vectorStore, MongoTemplate mongoTemplate) {
        this.vectorStore = vectorStore;
        this.mongoTemplate = mongoTemplate;
    }
    public String deleteMedicine(String id) {
        Medicine medicine = mongoTemplate.findById(id, Medicine.class);
        if (medicine == null) {
            throw new MedicineNotFoundException("Medicine not found with id: " + id);
        }
        mongoTemplate.remove(medicine);
        return "Medicine deleted: " + medicine.getName();
    }
    public String addMedicine(Medicine medicine) {
        mongoTemplate.save(medicine);

        String content = buildMedicineText(medicine);
        Document doc = new Document(content, Map.of(
                "name", medicine.getName(),
                "stockQuantity", String.valueOf(medicine.getStockQuantity())
        ));
        vectorStore.add(List.of(doc));

        return "Medicine added: " + medicine.getName() + " | Stock: " + medicine.getStockQuantity();
    }

    private String buildMedicineText(Medicine medicine) {
        return String.format("""
                Medicine Name: %s
                Used For: %s
                Dosage: %s
                Side Effects: %s
                Warnings: %s
                Stock: %d
                """,
                medicine.getName(),
                medicine.getUsedFor(),
                medicine.getDosage(),
                medicine.getSideEffects(),
                medicine.getWarnings(),
                medicine.getStockQuantity()
        );
    }
}