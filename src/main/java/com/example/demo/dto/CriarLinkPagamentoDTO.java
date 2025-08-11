package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarLinkPagamentoDTO {
    
    @NotBlank(message = "Nome do participante é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nomeParticipante;
    
    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;
    
    @NotBlank(message = "Descrição da despesa é obrigatória")
    @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
    private String descricaoDespesa;
    
    @NotNull(message = "ID da conta é obrigatório")
    private Long contaId;
    
    @NotNull(message = "ID do criador é obrigatório")
    private Long criadoPorId;
    
    @NotBlank(message = "Nome do criador é obrigatório")
    @Size(max = 100, message = "Nome do criador deve ter no máximo 100 caracteres")
    private String criadoPor;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Future(message = "Data de vencimento deve ser no futuro")
    private LocalDate dataVencimento;
    
    // Opcional - se não informado, usa 30 dias
    private Integer diasValidade;
    
    @Size(max = 500, message = "Observações devem ter no máximo 500 caracteres")
    private String observacoes;
}
