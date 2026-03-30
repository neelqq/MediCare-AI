package com.example.testAI.testAIRagVectorDB.code.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineRagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public MedicineRagService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    public String suggest(String problem) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(problem)
                        .topK(3)
                        .build()
        );

        String context = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        String prompt = """
                You are a helpful medical assistant.
                Based on the patient's problem, provide the following information only:
                
                Context:
                %s
                
                Patient's Problem: %s
                
                Reply in this exact format:
                Medicine Name: ...
                About: (what this medicine does in 1-2 lines)
                Who Can Use: (who should take this medicine)
                Who Cannot Use: (who should avoid this medicine)
                Doctor's Note: Always consult a doctor before taking any medicine.
                """.formatted(context, problem);

        return chatClient.prompt().user(prompt).call().content();
    }

    public Flux<String> suggestStream(String problem) {
        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(problem)
                        .topK(3)
                        .build()
        );

        String context = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        String prompt = """
                You are a helpful medical assistant.
                Based on the patient's problem, provide the following information only:
                
                Context:
                %s
                
                Patient's Problem: %s
                
                Reply in this exact format:
                Medicine Name: ...
                About: (what this medicine does in 1-2 lines)
                Who Can Use: (who should take this medicine)
                Who Cannot Use: (who should avoid this medicine)
                Doctor's Note: Always consult a doctor before taking any medicine.
                """.formatted(context, problem);

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content();
    }
}
