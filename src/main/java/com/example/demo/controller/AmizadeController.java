package com.example.demo.controller;

import com.example.demo.dto.AmizadeDTO;
import com.example.demo.dto.SolicitarAmizadeDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.service.AmizadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/amizades")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AmizadeController {
    
    private final AmizadeService amizadeService;
    
    @PostMapping("/solicitar/{solicitanteId}")
    public ResponseEntity<AmizadeDTO> solicitarAmizade(
            @PathVariable Long solicitanteId,
            @Valid @RequestBody SolicitarAmizadeDTO solicitacao) {
        AmizadeDTO amizade = amizadeService.solicitarAmizade(solicitanteId, solicitacao);
        return ResponseEntity.status(HttpStatus.CREATED).body(amizade);
    }
    
    @GetMapping("/pendentes/{usuarioId}")
    public ResponseEntity<List<AmizadeDTO>> listarConvitesPendentes(@PathVariable Long usuarioId) {
        List<AmizadeDTO> convitesPendentes = amizadeService.listarConvitesPendentes(usuarioId);
        return ResponseEntity.ok(convitesPendentes);
    }
    
    @PostMapping("/{amizadeId}/aceitar/{usuarioId}")
    public ResponseEntity<AmizadeDTO> aceitarConvite(
            @PathVariable Long amizadeId,
            @PathVariable Long usuarioId) {
        AmizadeDTO amizade = amizadeService.responderConvite(amizadeId, usuarioId, true);
        return ResponseEntity.ok(amizade);
    }
    
    @PostMapping("/{amizadeId}/rejeitar/{usuarioId}")
    public ResponseEntity<AmizadeDTO> rejeitarConvite(
            @PathVariable Long amizadeId,
            @PathVariable Long usuarioId) {
        AmizadeDTO amizade = amizadeService.responderConvite(amizadeId, usuarioId, false);
        return ResponseEntity.ok(amizade);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<UsuarioDTO>> listarAmigos(@PathVariable Long usuarioId) {
        List<UsuarioDTO> amigos = amizadeService.listarAmigos(usuarioId);
        return ResponseEntity.ok(amigos);
    }
    
    @GetMapping("/verificar/{usuario1Id}/{usuario2Id}")
    public ResponseEntity<Boolean> verificarAmizade(
            @PathVariable Long usuario1Id,
            @PathVariable Long usuario2Id) {
        boolean saoAmigos = amizadeService.saoAmigos(usuario1Id, usuario2Id);
        return ResponseEntity.ok(saoAmigos);
    }
    
    @DeleteMapping("/remover/{usuario1Id}/{usuario2Id}")
    public ResponseEntity<Void> removerAmizade(
            @PathVariable Long usuario1Id,
            @PathVariable Long usuario2Id) {
        amizadeService.removerAmizade(usuario1Id, usuario2Id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/bloquear/{bloqueadorId}/{bloqueadoId}")
    public ResponseEntity<Void> bloquearUsuario(
            @PathVariable Long bloqueadorId,
            @PathVariable Long bloqueadoId) {
        amizadeService.bloquearUsuario(bloqueadorId, bloqueadoId);
        return ResponseEntity.noContent().build();
    }
}
