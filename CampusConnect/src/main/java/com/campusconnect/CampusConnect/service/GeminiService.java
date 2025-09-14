package com.campusconnect.CampusConnect.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    private final Client client;

    public GeminiService(Client client){
        this.client = client;
    }
    public String generateText(String prompt){
        GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash",prompt,null);
        return response.text();
    }
}
