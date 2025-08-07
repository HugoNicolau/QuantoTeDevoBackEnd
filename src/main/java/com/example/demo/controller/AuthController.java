package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.model.Usuario;
import com.example.demo.service.JwtService;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            Usuario usuario = usuarioService.registrarUsuario(request, passwordEncoder);
            String token = jwtService.generateToken(usuario);
            
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
            
            LoginResponse loginResponse = new LoginResponse(token, usuarioDTO);
            
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Usuário registrado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Usuario usuario = usuarioService.autenticarUsuario(request, passwordEncoder);
            String token = jwtService.generateToken(usuario);
            
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
            
            LoginResponse loginResponse = new LoginResponse(token, usuarioDTO);
            
            return ResponseEntity.ok(ApiResponse.success(loginResponse, "Login realizado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Em JWT stateless, o logout é feito no frontend removendo o token
        return ResponseEntity.ok(ApiResponse.success(null, "Logout realizado com sucesso"));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(@RequestHeader("Authorization") String tokenHeader) {
        try {
            String oldToken = tokenHeader.replace("Bearer ", "");
            String newToken = jwtService.refreshToken(oldToken);
            
            Map<String, String> response = Map.of("token", newToken);
            return ResponseEntity.ok(ApiResponse.success(response, "Token renovado com sucesso"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
