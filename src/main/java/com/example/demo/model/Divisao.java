package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "divisoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Divisao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    @JsonBackReference("conta-divisoes")
    private Conta conta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference("usuario-divisoes")
    private Usuario usuario;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @Builder.Default
    private Boolean pago = false;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    // RF10: Adicionar campo para forma de pagamento
    @Column(name = "forma_pagamento")
    private String formaPagamento;
}
