package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "dividas")
public class Divida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private String descricao;
    
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    @ManyToOne
    @JoinColumn(name = "usuario_devedor_id", nullable = false)
    @NotNull
    private Usuario usuarioDevedor; // Quem deve
    
    @ManyToOne
    @JoinColumn(name = "usuario_credor_id", nullable = false)
    @NotNull
    private Usuario usuarioCredor; // Para quem deve
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    
    @Column(name = "paga", nullable = false)
    private Boolean paga = false;
    
    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;
    
    @Column(name = "forma_pagamento")
    private String formaPagamento;
}
