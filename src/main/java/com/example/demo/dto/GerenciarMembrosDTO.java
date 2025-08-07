package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GerenciarMembrosDTO {
    
    @NotNull(message = "Lista de IDs de usuários é obrigatória")
    @NotEmpty(message = "Lista de IDs de usuários não pode estar vazia")
    private List<Long> usuarioIds;
    
    @NotNull(message = "Ação é obrigatória")
    private AcaoMembro acao;
    
    public enum AcaoMembro {
        ADICIONAR,
        REMOVER
    }
}
