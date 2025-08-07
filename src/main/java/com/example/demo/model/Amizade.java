package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "amizades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"solicitante_id", "convidado_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amizade {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "convidado_id", nullable = false)
    private Usuario convidado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusAmizade status = StatusAmizade.PENDENTE;
    
    @Column(name = "data_solicitacao", nullable = false)
    @Builder.Default
    private LocalDateTime dataSolicitacao = LocalDateTime.now();
    
    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;
    
    // Constraint único definido na anotação @Table da classe
    
    // Método helper para verificar se a amizade está ativa
    public boolean isAtiva() {
        return this.status == StatusAmizade.ACEITA;
    }
    
    // Método helper para verificar se está pendente
    public boolean isPendente() {
        return this.status == StatusAmizade.PENDENTE;
    }
}
