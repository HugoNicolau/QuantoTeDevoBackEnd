package com.example.demo.dto;

import com.example.demo.model.StatusConta;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContaDTO {
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    private LocalDate vencimento;
    private Boolean paga;
    private StatusConta status;
    private LocalDateTime dataCriacao;
    
    @NotNull(message = "ID do criador é obrigatório")
    private Long criadorId;
    private UsuarioDTO criador;
}
