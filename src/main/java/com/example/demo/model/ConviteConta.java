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
@Table(name = "convites_conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConviteConta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String token;
    
    @Column(nullable = false)
    private String emailConvidado;
    
    @Column
    private String nomeConvidado; // opcional, caso queira personalizar
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorSugerido; // valor sugerido para divisão
    
    @Column
    private String mensagem; // mensagem personalizada do convite
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    @JsonBackReference("conta-convites")
    private Conta conta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_convidante_id", nullable = false)
    @JsonBackReference("usuario-convites-enviados")
    private Usuario usuarioConvidante;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_convidado_id")
    @JsonBackReference("usuario-convites-recebidos")
    private Usuario usuarioConvidado; // null até aceitar e/ou se cadastrar
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatusConvite status = StatusConvite.PENDENTE;
    
    @Column(name = "data_convite")
    @Builder.Default
    private LocalDateTime dataConvite = LocalDateTime.now();
    
    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;
    
    @Column(name = "data_aceite")
    private LocalDateTime dataAceite;
    
    @Column(name = "data_rejeicao")
    private LocalDateTime dataRejeicao;
}
