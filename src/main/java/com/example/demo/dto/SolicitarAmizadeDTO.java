package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitarAmizadeDTO {
    
    @NotNull(message = "ID do usuário convidado é obrigatório")
    private Long convidadoId;
    
    private String mensagem; // Mensagem opcional do convite
}
