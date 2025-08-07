package com.example.demo.dto;

import com.example.demo.model.StatusConvite;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConviteContaDTO {
    
    private Long id;
    private String token;
    private String emailConvidado;
    private String nomeConvidado;
    private BigDecimal valorSugerido;
    private String mensagem;
    private StatusConvite status;
    private LocalDateTime dataConvite;
    private LocalDateTime dataExpiracao;
    private LocalDateTime dataAceite;
    private LocalDateTime dataRejeicao;
    
    // Dados da conta
    private Long contaId;
    private String contaDescricao;
    private BigDecimal contaValor;
    private LocalDateTime contaDataCriacao;
    
    // Dados do usuário convidante
    private Long usuarioConvidanteId;
    private String usuarioConvidanteNome;
    private String usuarioConvidanteEmail;
    private String usuarioConvidanteChavePix;
    
    // Dados do usuário convidado (se já aceitou)
    private Long usuarioConvidadoId;
    private String usuarioConvidadoNome;
    
    // Campos calculados
    private boolean expirado;
    private long diasParaVencer;
}
