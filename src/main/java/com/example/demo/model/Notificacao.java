package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false)
    private String titulo;
    
    @Column(length = 500)
    private String mensagem;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeNotificacao prioridade;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_leitura")
    private LocalDateTime dataLeitura;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean lida = false;
    
    @Column(name = "referencia_id")
    private Long referenciaId;
    
    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;
    
    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;
    
    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (dataExpiracao == null) {
            // Notificações expiram em 30 dias por padrão
            dataExpiracao = dataCriacao.plusDays(30);
        }
    }
    
    public void marcarComoLida() {
        this.lida = true;
        this.dataLeitura = LocalDateTime.now();
    }
    
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }
}
