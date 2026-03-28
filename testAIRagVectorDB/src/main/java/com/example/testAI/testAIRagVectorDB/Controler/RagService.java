package com.example.testAI.testAIRagVectorDB.Controler;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {

    private ChatClient chatClient;
    private VectorStore vectorStore;
    RagService(ChatClient.Builder builder,VectorStore vectorStore){
        this.chatClient=builder.build();
        this.vectorStore=vectorStore;
    }

    public String injectDocument(List<String> texts){
        List<Document> documents=texts.stream()
                .map(x->new Document(x, Map.of("source", "manual")))
                .collect(Collectors.toList());
        vectorStore.add(documents);
        return "data is added"+documents.size();
    }

    public String ask(String qus){
        List<Document> sameData=vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(qus)
                        .topK(3)
                        .build());
        String data=sameData.stream().map(Document::getText).collect(Collectors.joining("/n"));
        String prompt = """
                তুমি একজন সহায়ক AI assistant।
                নিচের context ব্যবহার করে প্রশ্নের উত্তর দাও।
                যদি context এ উত্তর না থাকে, সেটা বলো।
                
                Context:
                %s
                
                প্রশ্ন: %s
                """;
        return chatClient.prompt().user(prompt).call().content();

    }


}
