package com.example.demo.controller;

import com.example.demo.dto.AceitarConviteDTO;
import com.example.demo.dto.ConviteContaDTO;
import com.example.demo.dto.CriarConviteDTO;
import com.example.demo.service.ConviteContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/convites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class ConviteContaController {
    
    private final ConviteContaService conviteService;
    
    @PostMapping("/conta/{contaId}/convidar/{usuarioConvidanteId}")
    public ResponseEntity<ConviteContaDTO> criarConvite(
            @PathVariable Long contaId,
            @PathVariable Long usuarioConvidanteId,
            @Valid @RequestBody CriarConviteDTO criarConviteDTO) {
        ConviteContaDTO convite = conviteService.criarConvite(contaId, usuarioConvidanteId, criarConviteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(convite);
    }
    
    @GetMapping("/token/{token}")
    public ResponseEntity<ConviteContaDTO> buscarConvitePorToken(@PathVariable String token) {
        ConviteContaDTO convite = conviteService.buscarConvitePorToken(token);
        return ResponseEntity.ok(convite);
    }
    
    @PostMapping("/token/{token}/aceitar")
    public ResponseEntity<ConviteContaDTO> aceitarConvite(
            @PathVariable String token,
            @Valid @RequestBody AceitarConviteDTO aceitarConviteDTO) {
        ConviteContaDTO convite = conviteService.aceitarConvite(token, aceitarConviteDTO);
        return ResponseEntity.ok(convite);
    }
    
    @PostMapping("/token/{token}/rejeitar")
    public ResponseEntity<String> rejeitarConvite(@PathVariable String token) {
        conviteService.rejeitarConvite(token);
        return ResponseEntity.ok("Convite rejeitado com sucesso");
    }
    
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<ConviteContaDTO>> listarConvitesDaConta(@PathVariable Long contaId) {
        List<ConviteContaDTO> convites = conviteService.listarConvitesDaConta(contaId);
        return ResponseEntity.ok(convites);
    }
    
    @GetMapping("/usuario/{usuarioId}/enviados")
    public ResponseEntity<List<ConviteContaDTO>> listarConvitesEnviados(@PathVariable Long usuarioId) {
        List<ConviteContaDTO> convites = conviteService.listarConvitesEnviados(usuarioId);
        return ResponseEntity.ok(convites);
    }
    
    @GetMapping("/email/{email}/pendentes")
    public ResponseEntity<List<ConviteContaDTO>> listarConvitesPendentesParaEmail(@PathVariable String email) {
        List<ConviteContaDTO> convites = conviteService.listarConvitesPendentesParaEmail(email);
        return ResponseEntity.ok(convites);
    }
    
    @PostMapping("/expirar-automaticamente")
    public ResponseEntity<String> marcarConvitesExpirados() {
        conviteService.marcarConvitesExpirados();
        return ResponseEntity.ok("Convites expirados marcados com sucesso");
    }
}
