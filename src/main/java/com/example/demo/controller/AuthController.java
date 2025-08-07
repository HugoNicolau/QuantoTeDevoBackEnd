package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "data", Map.of(
                "token", response.getToken(),
                "usuario", response.getUsuario()
            ),
            "message", "Usuário registrado com sucesso"
        ));
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        
        return ResponseEntity.ok(Map.of(
            "data", Map.of(
                "token", response.getToken(),
                "usuario", response.getUsuario()
            ),
            "message", "Login realizado com sucesso"
        ));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(
            @RequestHeader("Authorization") String authHeader) {
        
        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Token inválido"
            ));
        }
        
        String token = authHeader.substring(7);
        String newToken = authService.refreshToken(token);
        
        return ResponseEntity.ok(Map.of(
            "data", Map.of(
                "token", newToken
            ),
            "message", "Token renovado com sucesso"
        ));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // O logout é tratado no frontend removendo o token do localStorage
        return ResponseEntity.ok(Map.of(
            "data", (Object) null,
            "message", "Logout realizado com sucesso"
        ));
    }
}
