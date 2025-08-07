package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.Valid;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DividirPorcentagemDTO {
    
    @NotNull(message = "ID da conta é obrigatório")
    private Long contaId;
    
    @NotNull(message = "Lista de divisões é obrigatória")
    @NotEmpty(message = "Lista de divisões não pode estar vazia")
    @Valid
    private List<DivisaoPorcentualDTO> divisoes;
}
