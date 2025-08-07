package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.exception.NegocioException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Verificar se o email já existe
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new NegocioException("Email já está em uso");
        }
        
        // Criar novo usuário
        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .chavePix(request.getChavePix())
                .build();
        
        usuario = usuarioRepository.save(usuario);
        
        // Gerar token
        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail());
        
        // Converter para DTO
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
        
        return new AuthResponse(token, usuarioDTO);
    }
    
    public AuthResponse login(LoginRequest request) {
        // Buscar usuário por email
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NegocioException("Credenciais inválidas"));
        
        // Verificar se a senha foi definida
        if (usuario.getSenha() == null) {
            throw new NegocioException("Usuário não possui senha definida. Use o endpoint de registro.");
        }
        
        // Verificar senha
        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new NegocioException("Credenciais inválidas");
        }
        
        // Gerar token
        String token = jwtService.generateToken(usuario.getId(), usuario.getEmail());
        
        // Converter para DTO
        UsuarioDTO usuarioDTO = UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
        
        return new AuthResponse(token, usuarioDTO);
    }
    
    public String refreshToken(String currentToken) {
        if (!jwtService.isTokenValid(currentToken)) {
            throw new NegocioException("Token inválido");
        }
        
        Long userId = jwtService.getUserIdFromToken(currentToken);
        String email = jwtService.getEmailFromToken(currentToken);
        
        return jwtService.generateToken(userId, email);
    }
}
