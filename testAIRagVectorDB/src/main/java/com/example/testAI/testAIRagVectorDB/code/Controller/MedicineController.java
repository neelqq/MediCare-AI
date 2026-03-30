package com.example.testAI.testAIRagVectorDB.code.Controller;


import com.example.testAI.testAIRagVectorDB.code.Entity.Medicine;
import com.example.testAI.testAIRagVectorDB.code.Entity.Order;

import com.example.testAI.testAIRagVectorDB.code.Exceptions.InvalidRequestException;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.MedicineNotFoundException;
import com.example.testAI.testAIRagVectorDB.code.Exceptions.OutOfStockException;
import com.example.testAI.testAIRagVectorDB.code.Service.MedicineIngestionService;
import com.example.testAI.testAIRagVectorDB.code.Service.MedicineRagService;
import com.example.testAI.testAIRagVectorDB.code.Service.OrderService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/medicine")
public class MedicineController {

    private final MedicineIngestionService ingestionService;
    private final MedicineRagService ragService;
    private final MongoTemplate mongoTemplate;
    private final OrderService orderService;

    public MedicineController(MedicineIngestionService ingestionService,
                              MedicineRagService ragService,
                              MongoTemplate mongoTemplate,
                              OrderService orderService) {
        this.ingestionService = ingestionService;
        this.ragService = ragService;
        this.mongoTemplate = mongoTemplate;
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addMedicine(@RequestBody Medicine medicine) {
        if (medicine.getName() == null || medicine.getName().isEmpty()) {
            throw new InvalidRequestException("Medicine name cannot be empty");
        }
        if (medicine.getStockQuantity() < 0) {
            throw new InvalidRequestException("Stock quantity cannot be negative");
        }
        return ResponseEntity.ok(ingestionService.addMedicine(medicine));
    }

    @GetMapping("/suggest")
    public ResponseEntity<Map<String, String>> suggest(@RequestParam String problem) {
        if (problem == null || problem.isEmpty()) {
            throw new InvalidRequestException("Problem description cannot be empty");
        }
        String answer = ragService.suggest(problem);
        return ResponseEntity.ok(Map.of(
                "Medicine Name", extractField(answer, "Medicine Name"),
                "About", extractField(answer, "About"),
                "Who Can Use", extractField(answer, "Who Can Use"),
                "Who Cannot Use", extractField(answer, "Who Cannot Use"),
                "Doctor's Note", "Always consult a doctor before taking any medicine."
        ));
    }

    @GetMapping(value = "/suggest/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public Flux<String> suggestStream(@RequestParam String problem) {
        if (problem == null || problem.isEmpty()) {
            throw new InvalidRequestException("Problem description cannot be empty");
        }
        return ragService.suggestStream(problem);
    }

    @GetMapping("/suggest/check")
    public ResponseEntity<Map<String, String>> suggestWithStockCheck(@RequestParam String problem) {
        if (problem == null || problem.isEmpty()) {
            throw new InvalidRequestException("Problem description cannot be empty");
        }

        String answer = ragService.suggest(problem);
        String medicineName = extractField(answer, "Medicine Name");

        if (medicineName.equals("Not found")) {
            throw new MedicineNotFoundException("No medicine found for: " + problem);
        }

        Medicine medicine = mongoTemplate.findOne(
                Query.query(Criteria.where("name").is(medicineName)),
                Medicine.class
        );

        if (medicine == null || medicine.getStockQuantity() == 0) {
            throw new OutOfStockException(medicineName);
        }

        return ResponseEntity.ok(Map.of(
                "Medicine Name", medicineName,
                "About", extractField(answer, "About"),
                "Who Can Use", extractField(answer, "Who Can Use"),
                "Who Cannot Use", extractField(answer, "Who Cannot Use"),
                "Stock Available", String.valueOf(medicine.getStockQuantity()),
                "Doctor's Note", "Always consult a doctor before taking any medicine."
        ));
    }

    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(
            @RequestParam String medicineName,
            @RequestParam int quantity) {
        if (medicineName == null || medicineName.isEmpty()) {
            throw new InvalidRequestException("Medicine name cannot be empty");
        }
        if (quantity <= 0) {
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        return ResponseEntity.ok(orderService.placeOrder(medicineName, quantity));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Medicine>> findAll() {
        List<Medicine> allMedicines = mongoTemplate.findAll(Medicine.class);
        if (allMedicines.isEmpty()) {
            throw new MedicineNotFoundException("No medicines found in database");
        }
        return ResponseEntity.ok(allMedicines);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteMedicine(@PathVariable String id) {
        if (id == null || id.isEmpty()) {
            throw new InvalidRequestException("Medicine ID cannot be empty");
        }
        return ResponseEntity.ok(ingestionService.deleteMedicine(id));
    }

    private String extractField(String answer, String field) {
        try {
            String[] lines = answer.split("\n");
            for (String line : lines) {
                if (line.startsWith(field + ":")) {
                    return line.substring(field.length() + 1).trim();
                }
            }
        } catch (Exception e) {
            return "Not found";
        }
        return "Not found";
    }
}