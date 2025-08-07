package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.dto.ApiResponse;
import com.example.demo.model.Usuario;
import com.example.demo.service.JwtService;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            // Verificar se email já existe
            if (usuarioRepository.existsByEmail(request.getEmail())) {
                throw new NegocioException("Email já cadastrado no sistema");
            }
            
            Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .chavePix(request.getChavePix())
                .build();
            
            Usuario usuarioSalvo = usuarioRepository.save(usuario);
            String token = jwtService.generateToken(usuarioSalvo.getEmail());
            
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuarioSalvo.getId())
                .nome(usuarioSalvo.getNome())
                .email(usuarioSalvo.getEmail())
                .chavePix(usuarioSalvo.getChavePix())
                .build();
            
            Map<String, Object> data = new HashMap<>();
            data.put("usuario", usuarioDTO);
            data.put("token", token);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Usuário registrado com sucesso")
                .data(data)
                .build();
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Erro ao registrar usuário: " + e.getMessage())
                .build();
                
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest request) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Email não encontrado"));
            
            if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
                throw new NegocioException("Senha inválida");
            }
            
            String token = jwtService.generateToken(usuario.getEmail());
            
            UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
            
            Map<String, Object> data = new HashMap<>();
            data.put("usuario", usuarioDTO);
            data.put("token", token);
            
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Login realizado com sucesso")
                .data(data)
                .build();
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResponse<Map<String, Object>> response = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Erro no login: " + e.getMessage())
                .build();
                
            return ResponseEntity.badRequest().body(response);
        }
    }
}
