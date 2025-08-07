package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraDTO {
    
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Data da compra é obrigatória")
    private LocalDate dataCompra;
    
    private LocalDateTime dataCriacao;
    
    @NotNull(message = "ID do usuário criador é obrigatório")
    private Long usuarioCriadorId;
    private UsuarioDTO usuarioCriador;
    
    private List<ItemCompraDTO> itens;
    
    private Boolean finalizada;
    private String observacoes;
    
    // Campos calculados
    private BigDecimal valorTotal; // Soma de todos os itens
    private Integer totalItens;    // Quantidade total de itens
}
