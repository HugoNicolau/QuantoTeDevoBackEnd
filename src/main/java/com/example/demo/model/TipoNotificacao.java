package com.example.demo.model;

public enum TipoNotificacao {
    CONTA_VENCENDO("Conta próxima do vencimento"),
    CONTA_VENCIDA("Conta vencida"),
    DIVIDA_PENDENTE("Dívida pendente"),
    DIVISAO_PENDENTE("Divisão pendente de pagamento"),
    PAGAMENTO_RECEBIDO("Pagamento recebido"),
    CONVITE_RECEBIDO("Convite para conta"),
    CONTA_CRIADA("Nova conta criada"),
    LEMBRETE_PAGAMENTO("Lembrete de pagamento"),
    SISTEMA("Notificação do sistema");
    
    private final String descricao;
    
    TipoNotificacao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
