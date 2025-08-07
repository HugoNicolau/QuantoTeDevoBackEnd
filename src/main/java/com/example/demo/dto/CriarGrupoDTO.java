package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CriarGrupoDTO {
    
    @NotBlank(message = "Nome do grupo é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;
    
    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
    private String descricao;
    
    // Lista inicial de IDs de usuários para adicionar como membros
    private List<Long> membrosIniciais;
}
