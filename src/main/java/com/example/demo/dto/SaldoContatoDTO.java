package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaldoContatoDTO {
    private Long usuarioId;
    private String nomeUsuario;
    private String email;
    private BigDecimal saldoComEsteUsuario; // Positivo = este usuário me deve, Negativo = eu devo para este usuário
    private int quantidadeContasAbertas;
}
