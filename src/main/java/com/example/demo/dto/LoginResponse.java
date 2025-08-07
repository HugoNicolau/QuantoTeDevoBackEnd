package com.example.demo.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private UsuarioDTO usuario; // Informações do usuário logado
    
    public LoginResponse(String token) {
        this.token = token;
    }
}
