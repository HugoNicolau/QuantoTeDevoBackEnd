package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrupoDTO {
    
    private Long id;
    private String nome;
    private String descricao;
    private UsuarioDTO criador;
    private List<UsuarioDTO> membros;
    private LocalDateTime dataCriacao;
    private Boolean ativo;
    
    // Campos calculados
    private Integer totalMembros;
    private Integer totalContas;
    private Boolean usuarioECriador;
    private Boolean usuarioEMembro;
}
