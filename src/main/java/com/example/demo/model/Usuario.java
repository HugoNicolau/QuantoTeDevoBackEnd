package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "senha")
    private String senha; // Opcional por enquanto, para não quebrar dados existentes
    
    @Column(name = "chave_pix")
    private String chavePix;
    
    @OneToMany(mappedBy = "criador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-contas")
    private List<Conta> contasCriadas;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-divisoes")
    private List<Divisao> divisoes;
    
    @OneToMany(mappedBy = "usuarioConvidante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-convites-enviados")
    private List<ConviteConta> convitesEnviados;
    
    @OneToMany(mappedBy = "usuarioConvidado", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("usuario-convites-recebidos")
    private List<ConviteConta> convitesRecebidos;
}
