package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Quanto Te Devo API");
        response.put("version", "1.0.0");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "Quanto Te Devo API");
        response.put("description", "API para gerenciamento de contas compartilhadas");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "usuarios", "/api/usuarios",
            "contas", "/api/contas", 
            "divisoes", "/api/divisoes",
            "dividas", "/api/dividas"
        ));
        
        return ResponseEntity.ok(response);
    }
}
