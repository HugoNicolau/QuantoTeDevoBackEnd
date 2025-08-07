package com.example.demo.controller;

import com.example.demo.dto.CriarGrupoDTO;
import com.example.demo.dto.GerenciarMembrosDTO;
import com.example.demo.dto.GrupoDTO;
import com.example.demo.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grupos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GrupoController {
    
    private final GrupoService grupoService;
    
    @PostMapping("/criar/{criadorId}")
    public ResponseEntity<GrupoDTO> criarGrupo(
            @PathVariable Long criadorId,
            @Valid @RequestBody CriarGrupoDTO criarGrupoDTO) {
        GrupoDTO grupo = grupoService.criarGrupo(criadorId, criarGrupoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(grupo);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<GrupoDTO>> listarGruposDoUsuario(@PathVariable Long usuarioId) {
        List<GrupoDTO> grupos = grupoService.listarGruposDoUsuario(usuarioId);
        return ResponseEntity.ok(grupos);
    }
    
    @GetMapping("/{grupoId}/usuario/{usuarioId}")
    public ResponseEntity<GrupoDTO> buscarGrupoPorId(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId) {
        GrupoDTO grupo = grupoService.buscarGrupoPorId(grupoId, usuarioId);
        return ResponseEntity.ok(grupo);
    }
    
    @PutMapping("/{grupoId}/membros/{usuarioId}")
    public ResponseEntity<GrupoDTO> gerenciarMembros(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId,
            @Valid @RequestBody GerenciarMembrosDTO gerenciarDTO) {
        GrupoDTO grupo = grupoService.gerenciarMembros(grupoId, usuarioId, gerenciarDTO);
        return ResponseEntity.ok(grupo);
    }
    
    @DeleteMapping("/{grupoId}/desativar/{usuarioId}")
    public ResponseEntity<Void> desativarGrupo(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId) {
        grupoService.desativarGrupo(grupoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{grupoId}/sair/{usuarioId}")
    public ResponseEntity<Void> sairDoGrupo(
            @PathVariable Long grupoId,
            @PathVariable Long usuarioId) {
        grupoService.sairDoGrupo(grupoId, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
