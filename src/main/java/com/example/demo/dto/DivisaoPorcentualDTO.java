package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DivisaoPorcentualDTO {
    
    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;
    
    @NotNull(message = "Percentual é obrigatório")
    @DecimalMin(value = "0.01", message = "Percentual deve ser maior que 0")
    @DecimalMax(value = "1.0", message = "Percentual deve ser menor ou igual a 1 (100%)")
    @Digits(integer = 1, fraction = 4, message = "Percentual deve ter no máximo 4 casas decimais")
    private BigDecimal percentual; // 0.25 = 25%, 1.0 = 100%
}
