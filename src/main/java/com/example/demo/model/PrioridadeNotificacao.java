package com.example.demo.model;

public enum PrioridadeNotificacao {
    BAIXA("Baixa", "#28a745"),
    MEDIA("Média", "#ffc107"), 
    ALTA("Alta", "#fd7e14"),
    URGENTE("Urgente", "#dc3545");
    
    private final String descricao;
    private final String cor;
    
    PrioridadeNotificacao(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public String getCor() {
        return cor;
    }
}
