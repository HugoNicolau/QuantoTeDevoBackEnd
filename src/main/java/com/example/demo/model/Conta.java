package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String descricao;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
    
    private LocalDate vencimento;
    
    @Builder.Default
    private Boolean paga = false;
    
    @Column(name = "data_criacao")
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;
    
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Divisao> divisoes;
}
