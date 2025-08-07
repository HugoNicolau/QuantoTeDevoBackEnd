package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmizadeDTO {
    
    private Long id;
    
    @NotNull(message = "ID do solicitante é obrigatório")
    private Long solicitanteId;
    private UsuarioDTO solicitante;
    
    @NotNull(message = "ID do convidado é obrigatório")  
    private Long convidadoId;
    private UsuarioDTO convidado;
    
    private String status; // PENDENTE, ACEITA, REJEITADA, BLOQUEADA
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataResposta;
    
    // Campo auxiliar para indicar a perspectiva do usuário atual
    private boolean isSolicitacao; // true = eu enviei, false = eu recebi
}
