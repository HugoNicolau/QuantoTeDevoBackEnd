package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoUsuarioDTO {
    private Long usuarioId;
    private String nomeUsuario;
    private BigDecimal totalDevendo;      // Quanto este usuário deve para outros
    private BigDecimal totalRecebendo;    // Quanto outros devem para este usuário
    private BigDecimal saldoLiquido;      // totalRecebendo - totalDevendo
    private List<SaldoContatoDTO> saldosContatos; // Detalhamento por contato
}
