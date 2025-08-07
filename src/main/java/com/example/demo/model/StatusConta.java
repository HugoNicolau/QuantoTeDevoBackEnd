package com.example.demo.model;

public enum StatusConta {
    PENDENTE("Pendente"),
    PAGA("Paga"),
    VENCIDA("Vencida"),
    PARCIALMENTE_PAGA("Parcialmente Paga");
    
    private final String descricao;
    
    StatusConta(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
