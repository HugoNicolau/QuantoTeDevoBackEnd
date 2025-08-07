package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grupos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(name = "descricao")
    private String descricao;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "grupo_membros",
        joinColumns = @JoinColumn(name = "grupo_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> membros;
    
    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference("grupo-contas")
    private List<Conta> contas;
    
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "ativo")
    private Boolean ativo = true;
    
    // Método para verificar se usuário é membro do grupo
    public boolean isMembro(Usuario usuario) {
        return membros != null && membros.contains(usuario);
    }
    
    // Método para verificar se usuário é criador do grupo
    public boolean isCriador(Usuario usuario) {
        return criador != null && criador.getId().equals(usuario.getId());
    }
    
    // Método para verificar se usuário pode modificar o grupo (criador ou admin futuro)
    public boolean podeModificar(Usuario usuario) {
        return isCriador(usuario);
    }
}
