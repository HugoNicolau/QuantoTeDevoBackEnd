package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "itens_compra")
public class ItemCompra {
    
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
    
    @Column(nullable = false)
    @Builder.Default
    private Integer quantidade = 1;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id", nullable = false)
    @NotNull
    @JsonBackReference("compra-itens")
    private Compra compra;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id", nullable = false)
    @NotNull
    private Usuario usuarioResponsavel; // Quem deve pagar este item
    
    @Column(name = "observacoes")
    private String observacoes;
    
    // Método auxiliar para calcular o valor total do item
    public BigDecimal getValorTotal() {
        return valor.multiply(BigDecimal.valueOf(quantidade));
    }
}
