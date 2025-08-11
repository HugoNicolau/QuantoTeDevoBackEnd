package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriarLinkPagamentoResponseDTO {
    
    private String linkId;
    private String url;
    private String urlCompleta;
    
    public CriarLinkPagamentoResponseDTO(String linkId, String baseUrl) {
        this.linkId = linkId;
        this.url = "/pagamento/" + linkId;
        this.urlCompleta = baseUrl + "/pagamento/" + linkId;
    }
}
