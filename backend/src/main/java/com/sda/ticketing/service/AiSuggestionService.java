package com.sda.ticketing.service;

import com.sda.ticketing.domain.Priority;
import com.sda.ticketing.dto.AiSuggestionRequest;
import com.sda.ticketing.dto.AiSuggestionResponse;
import org.springframework.stereotype.Service;

@Service
public class AiSuggestionService {

    public AiSuggestionResponse suggest(AiSuggestionRequest request) {
        AiSuggestionResponse response = new AiSuggestionResponse();
        String text = (request.getTitle() + " " + request.getDescription()).toLowerCase();
        if (text.contains("urgent") || text.contains("down")) {
            response.setSuggestedPriority(Priority.URGENT);
            response.setRationale("Detected urgency keywords in the text.");
        } else if (text.contains("error") || text.contains("issue")) {
            response.setSuggestedPriority(Priority.HIGH);
            response.setRationale("Detected incident-related keywords in the text.");
        } else {
            response.setSuggestedPriority(Priority.MEDIUM);
            response.setRationale("Default medium priority suggestion.");
        }
        return response;
    }
}

