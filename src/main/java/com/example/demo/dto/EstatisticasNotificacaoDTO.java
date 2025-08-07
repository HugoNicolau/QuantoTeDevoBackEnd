package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasNotificacaoDTO {
    
    private Long totalNotificacoes;
    private Long notificacoesNaoLidas;
    private Long notificacoesLidas;
    private Long notificacoesExpiradas;
    
    private Long contaVencendo;
    private Long contaVencida;
    private Long dividaPendente;
    private Long divisaoPendente;
    private Long conviteRecebido;
    
    private Double percentualLidas;
    
    public static EstatisticasNotificacaoDTO criar(
            Long total, Long naoLidas, Long lidas, Long expiradas,
            Long contaVencendo, Long contaVencida, Long dividaPendente, 
            Long divisaoPendente, Long conviteRecebido) {
        
        double percentual = total > 0 ? (lidas.doubleValue() / total.doubleValue()) * 100 : 0.0;
        
        return EstatisticasNotificacaoDTO.builder()
            .totalNotificacoes(total)
            .notificacoesNaoLidas(naoLidas)
            .notificacoesLidas(lidas)
            .notificacoesExpiradas(expiradas)
            .contaVencendo(contaVencendo)
            .contaVencida(contaVencida)
            .dividaPendente(dividaPendente)
            .divisaoPendente(divisaoPendente)
            .conviteRecebido(conviteRecebido)
            .percentualLidas(Math.round(percentual * 100.0) / 100.0)
            .build();
    }
}
