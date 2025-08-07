package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "compras")
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @Column(nullable = false)
    private String descricao;
    
    @Column(name = "data_compra", nullable = false)
    @Builder.Default
    private LocalDate dataCompra = LocalDate.now();
    
    @Column(name = "data_criacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_criador_id", nullable = false)
    @NotNull
    private Usuario usuarioCriador;
    
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("compra-itens")
    private List<ItemCompra> itens;
    
    @Column(name = "finalizada", nullable = false)
    @Builder.Default
    private Boolean finalizada = false;
    
    @Column(name = "observacoes")
    private String observacoes;
}
