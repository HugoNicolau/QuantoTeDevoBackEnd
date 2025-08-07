package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DividaDTO {
    
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @Positive(message = "Valor deve ser positivo")
    private BigDecimal valor;
    
    @NotNull(message = "ID do usuário devedor é obrigatório")
    private Long usuarioDevedorId;
    private String nomeUsuarioDevedor;
    
    @NotNull(message = "ID do usuário credor é obrigatório")
    private Long usuarioCredorId;
    private String nomeUsuarioCredor;
    
    private LocalDateTime dataCriacao;
    private LocalDate dataVencimento;
    private Boolean paga;
    private LocalDateTime dataPagamento;
    private String formaPagamento;
}
