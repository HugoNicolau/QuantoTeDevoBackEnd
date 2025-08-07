package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarConviteDTO {
    
    @NotBlank(message = "Email do convidado é obrigatório")
    @Email(message = "Email deve ter formato válido")
    private String emailConvidado;
    
    private String nomeConvidado; // opcional
    
    @DecimalMin(value = "0.01", message = "Valor sugerido deve ser maior que zero")
    private BigDecimal valorSugerido;
    
    private String mensagem; // mensagem personalizada
    
    private Integer diasValidadeConvite; // padrão será 7 dias
}
