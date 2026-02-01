package com.sda.ticketing.web;

import com.sda.ticketing.dto.AiSuggestionRequest;
import com.sda.ticketing.dto.AiSuggestionResponse;
import com.sda.ticketing.service.AiSuggestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiSuggestionController {

    private final AiSuggestionService aiSuggestionService;

    public AiSuggestionController(AiSuggestionService aiSuggestionService) {
        this.aiSuggestionService = aiSuggestionService;
    }

    @PostMapping("/suggest")
    public ResponseEntity<AiSuggestionResponse> suggest(@Valid @RequestBody AiSuggestionRequest request) {
        return ResponseEntity.ok(aiSuggestionService.suggest(request));
    }
}

