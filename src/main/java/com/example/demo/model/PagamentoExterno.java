package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "pagamentos_externos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagamentoExterno {
    
    @Id
    @Column(length = 32)
    private String id;
    
    @Column(name = "nome_participante", nullable = false, length = 100)
    private String nomeParticipante;
    
    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "descricao_despesa", nullable = false)
    private String descricaoDespesa;
    
    @Column(name = "criado_por", nullable = false, length = 100)
    private String criadoPor;
    
    @Column(name = "criado_por_id", nullable = false)
    private Long criadoPorId;
    
    @Column(name = "conta_id", nullable = false)
    private Long contaId;
    
    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;
    
    @Column(name = "pago", nullable = false)
    @Builder.Default
    private Boolean pago = false;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    @Column(name = "forma_pagamento", length = 100)
    private String formaPagamento;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;
    
    // Relacionamentos para consultas mais eficientes (opcionais)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", insertable = false, updatable = false)
    private Conta conta;
    
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "criado_por_id", insertable = false, updatable = false)
    private Usuario criador;
    
    // Métodos de conveniência
    public boolean isExpirado() {
        return dataExpiracao.isBefore(LocalDateTime.now());
    }
    
    public boolean isAtivo() {
        return !isExpirado();
    }
    
    public boolean isVencido() {
        return dataVencimento.isBefore(LocalDate.now()) && !pago;
    }
}
