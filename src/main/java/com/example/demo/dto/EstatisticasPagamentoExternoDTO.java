package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasPagamentoExternoDTO {
    
    private long totalLinks;
    private long linksPagos;
    private long linksPendentes;
    private long linksExpirados;
    private long linksVencidos;
    private double percentualPagos;
}
