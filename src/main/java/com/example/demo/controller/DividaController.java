package com.example.demo.controller;

import com.example.demo.dto.DividaDTO;
import com.example.demo.service.DividaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dividas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class DividaController {
    
    private final DividaService dividaService;
    
    @PostMapping
    public ResponseEntity<DividaDTO> criarDivida(@Valid @RequestBody DividaDTO dividaDTO) {
        DividaDTO dividaCriada = dividaService.criarDivida(dividaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dividaCriada);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DividaDTO> buscarDivida(@PathVariable Long id) {
        DividaDTO divida = dividaService.buscarPorId(id);
        return ResponseEntity.ok(divida);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DividaDTO>> listarDividasDoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean paga) {
        List<DividaDTO> dividas = dividaService.listarDividasDoUsuario(usuarioId, paga);
        return ResponseEntity.ok(dividas);
    }
    
    @GetMapping("/usuario/{usuarioId}/devendo")
    public ResponseEntity<List<DividaDTO>> listarDividasDevidas(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean paga) {
        List<DividaDTO> dividas = dividaService.listarDividasDevidas(usuarioId, paga);
        return ResponseEntity.ok(dividas);
    }
    
    @GetMapping("/usuario/{usuarioId}/recebendo")
    public ResponseEntity<List<DividaDTO>> listarDividasAReceber(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean paga) {
        List<DividaDTO> dividas = dividaService.listarDividasAReceber(usuarioId, paga);
        return ResponseEntity.ok(dividas);
    }
    
    @PatchMapping("/{id}/marcar-paga")
    public ResponseEntity<DividaDTO> marcarComoPaga(
            @PathVariable Long id,
            @RequestParam(required = false) String formaPagamento) {
        DividaDTO dividaAtualizada = dividaService.marcarComoPaga(id, formaPagamento);
        return ResponseEntity.ok(dividaAtualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarDivida(@PathVariable Long id) {
        dividaService.deletarDivida(id);
        return ResponseEntity.noContent().build();
    }
}
