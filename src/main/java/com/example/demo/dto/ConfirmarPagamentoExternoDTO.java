package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmarPagamentoExternoDTO {
    
    @NotBlank(message = "Forma de pagamento é obrigatória")
    @Size(max = 100, message = "Forma de pagamento deve ter no máximo 100 caracteres")
    private String formaPagamento;
    
    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;
}
