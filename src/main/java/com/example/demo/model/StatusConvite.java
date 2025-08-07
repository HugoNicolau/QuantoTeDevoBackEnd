package com.example.demo.model;

public enum StatusConvite {
    PENDENTE("Pendente"),
    ACEITO("Aceito"),
    EXPIRADO("Expirado"),
    REJEITADO("Rejeitado");
    
    private final String descricao;
    
    StatusConvite(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
