package com.example.testAI.testAIRagVectorDB.Controler;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class Controler {
    private ChatClient chatClient;
    private RagService ragService;
    Controler(ChatClient.Builder builder,RagService ragService){

        this.chatClient=builder.build();
        this.ragService=ragService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        String data=this.chatClient.prompt("hii").call().content();
        return ResponseEntity.ok(data);
    }
    @PostMapping("/addData")
    public ResponseEntity<Map<String,String>> addData(@RequestBody Map<String, List<String>> req){
        List<String> texts=req.get("texts");
        String result=ragService.injectDocument(texts);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @GetMapping("/ask")
    public ResponseEntity<Map<String,String>> ask(@RequestParam String str){
        String ans=ragService.ask(str);
        return ResponseEntity.ok(Map.of("question", str, "answer", ans));
    }
}
