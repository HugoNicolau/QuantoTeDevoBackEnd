package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraDTO {
    
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;
    
    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantidade;
    
    @NotNull(message = "ID do usuário responsável é obrigatório")
    private Long usuarioResponsavelId;
    private UsuarioDTO usuarioResponsavel;
    
    private String observacoes;
    
    // Campo calculado
    private BigDecimal valorTotal; // valor * quantidade
}
