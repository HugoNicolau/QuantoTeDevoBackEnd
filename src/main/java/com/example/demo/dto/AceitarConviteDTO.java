package com.example.demo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AceitarConviteDTO {
    
    private String nome; // caso o usuário ainda não esteja cadastrado
    private String chavePix; // caso o usuário ainda não esteja cadastrado
    private boolean aceitarDivisao; // se quer participar da divisão automaticamente
}
