package com.rag.controller;

import com.rag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final DocumentService documentService;

    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return documentService.askQuestion(question);
    }

}
