package com.example.demo.dto;

import com.example.demo.model.PrioridadeNotificacao;
import com.example.demo.model.TipoNotificacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoDTO {
    
    private Long id;
    private String titulo;
    private String mensagem;
    private TipoNotificacao tipo;
    private String tipoDescricao;
    private PrioridadeNotificacao prioridade;
    private String prioridadeDescricao;
    private String prioridadeCor;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataLeitura;
    private Boolean lida;
    private Long referenciaId;
    private String referenciaTipo;
    private LocalDateTime dataExpiracao;
    private Boolean expirada;
    private Long diasAteExpiracao;
    
    // Método para converter do model
    public static NotificacaoDTO fromModel(com.example.demo.model.Notificacao notificacao) {
        return NotificacaoDTO.builder()
            .id(notificacao.getId())
            .titulo(notificacao.getTitulo())
            .mensagem(notificacao.getMensagem())
            .tipo(notificacao.getTipo())
            .tipoDescricao(notificacao.getTipo().getDescricao())
            .prioridade(notificacao.getPrioridade())
            .prioridadeDescricao(notificacao.getPrioridade().getDescricao())
            .prioridadeCor(notificacao.getPrioridade().getCor())
            .dataCriacao(notificacao.getDataCriacao())
            .dataLeitura(notificacao.getDataLeitura())
            .lida(notificacao.getLida())
            .referenciaId(notificacao.getReferenciaId())
            .referenciaTipo(notificacao.getReferenciaTipo())
            .dataExpiracao(notificacao.getDataExpiracao())
            .expirada(notificacao.isExpirada())
            .diasAteExpiracao(calcularDiasAteExpiracao(notificacao.getDataExpiracao()))
            .build();
    }
    
    private static Long calcularDiasAteExpiracao(LocalDateTime dataExpiracao) {
        if (dataExpiracao == null) return null;
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isAfter(dataExpiracao)) return 0L;
        return java.time.Duration.between(agora, dataExpiracao).toDays();
    }
}
