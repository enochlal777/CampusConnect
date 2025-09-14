package com.campusconnect.CampusConnect.controller;

import com.campusconnect.CampusConnect.service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService service;

    public GeminiController(GeminiService service){
        this.service=service;
    }

    record PromptRequest(String prompt){}

    @PostMapping("/generate")
    public ResponseEntity<String> generate(@RequestBody PromptRequest request){
        String result = service.generateText(request.prompt());
        return ResponseEntity.ok(result);
    }
}
