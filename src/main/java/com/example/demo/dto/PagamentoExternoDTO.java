package com.example.demo.dto;

import com.example.demo.model.PagamentoExterno;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoExternoDTO {
    
    private String id;
    private String nomeParticipante;
    private BigDecimal valor;
    private String descricaoDespesa;
    private String criadoPor;
    private Long criadoPorId;
    private Long contaId;
    private LocalDateTime dataCriacao;
    private LocalDate dataVencimento;
    private Boolean pago;
    private LocalDateTime dataPagamento;
    private String formaPagamento;
    private String observacoes;
    private LocalDateTime dataExpiracao;
    
    // Campos calculados
    private boolean expirado;
    private boolean vencido;
    private long diasParaVencer;
    private long horasParaExpirar;
    
    // Dados da conta (quando necessário)
    private String contaDescricao;
    private BigDecimal contaValor;
    
    /**
     * Construtor a partir da entidade
     */
    public PagamentoExternoDTO(PagamentoExterno entity) {
        this.id = entity.getId();
        this.nomeParticipante = entity.getNomeParticipante();
        this.valor = entity.getValor();
        this.descricaoDespesa = entity.getDescricaoDespesa();
        this.criadoPor = entity.getCriadoPor();
        this.criadoPorId = entity.getCriadoPorId();
        this.contaId = entity.getContaId();
        this.dataCriacao = entity.getDataCriacao();
        this.dataVencimento = entity.getDataVencimento();
        this.pago = entity.getPago();
        this.dataPagamento = entity.getDataPagamento();
        this.formaPagamento = entity.getFormaPagamento();
        this.observacoes = entity.getObservacoes();
        this.dataExpiracao = entity.getDataExpiracao();
        
        // Calcular campos derivados
        LocalDateTime agora = LocalDateTime.now();
        LocalDate hoje = LocalDate.now();
        
        this.expirado = entity.isExpirado();
        this.vencido = entity.isVencido();
        this.diasParaVencer = hoje.until(entity.getDataVencimento()).getDays();
        
        // Calcular horas até expirar usando Duration
        this.horasParaExpirar = java.time.Duration.between(agora, entity.getDataExpiracao()).toHours();
        
        // Dados da conta se disponível (será preenchido separadamente quando necessário)
        this.contaDescricao = null;
        this.contaValor = null;
    }
}
