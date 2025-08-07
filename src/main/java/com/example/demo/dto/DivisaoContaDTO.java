package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DivisaoContaDTO {
    @NotNull(message = "ID da conta é obrigatório")
    private Long contaId;
    
    @NotNull(message = "Lista de usuários é obrigatória")
    private List<DivisaoUsuarioDTO> usuarios;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DivisaoUsuarioDTO {
        @NotNull(message = "ID do usuário é obrigatório")
        private Long usuarioId;
        
        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
        private BigDecimal valor;
    }
}
