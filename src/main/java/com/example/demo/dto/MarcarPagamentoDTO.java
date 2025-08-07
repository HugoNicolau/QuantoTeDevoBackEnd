package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarPagamentoDTO {
    
    @NotNull(message = "Data de pagamento é obrigatória")
    private LocalDateTime dataPagamento;
    
    @NotBlank(message = "Forma de pagamento é obrigatória")
    private String formaPagamento; // Ex: "Pix", "Dinheiro", "Transferência", "Cartão"
}
