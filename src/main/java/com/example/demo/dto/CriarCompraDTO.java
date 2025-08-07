package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarCompraDTO {
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    private LocalDate dataCompra; // Opcional - se não fornecida, será hoje
    
    @NotNull(message = "ID do usuário criador é obrigatório")
    private Long usuarioCriadorId;
    
    @NotEmpty(message = "Lista de itens não pode estar vazia")
    @Valid
    private List<CriarItemCompraDTO> itens;
    
    private String observacoes;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CriarItemCompraDTO {
        
        @NotBlank(message = "Descrição do item é obrigatória")
        private String descricao;
        
        @NotNull(message = "Valor do item é obrigatório")
        private java.math.BigDecimal valor;
        
        @NotNull(message = "Quantidade é obrigatória")
        private Integer quantidade;
        
        @NotNull(message = "ID do usuário responsável é obrigatório")
        private Long usuarioResponsavelId;
        
        private String observacoes;
    }
}
