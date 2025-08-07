package com.example.demo.controller;

import com.example.demo.dto.DivisaoContaDTO;
import com.example.demo.model.Divisao;
import com.example.demo.service.DivisaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/divisoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class DivisaoController {
    
    private final DivisaoService divisaoService;
    
    @PostMapping("/dividir")
    public ResponseEntity<String> dividirConta(@Valid @RequestBody DivisaoContaDTO divisaoContaDTO) {
        divisaoService.dividirConta(divisaoContaDTO);
        return ResponseEntity.ok("Conta dividida com sucesso!");
    }
    
    @PostMapping("/dividir-igualmente")
    public ResponseEntity<String> dividirContaIgualmente(
            @RequestParam Long contaId,
            @RequestBody List<Long> usuarioIds) {
        divisaoService.dividirContaIgualmente(contaId, usuarioIds);
        return ResponseEntity.ok("Conta dividida igualmente com sucesso!");
    }
    
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<Divisao>> listarDivisoesDaConta(@PathVariable Long contaId) {
        List<Divisao> divisoes = divisaoService.listarDivisoesDaConta(contaId);
        return ResponseEntity.ok(divisoes);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Divisao>> listarDivisoesDoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean pago) {
        List<Divisao> divisoes = divisaoService.listarDivisoesDoUsuario(usuarioId, pago);
        return ResponseEntity.ok(divisoes);
    }
    
    @GetMapping("/usuario/{usuarioId}/total-devido")
    public ResponseEntity<BigDecimal> calcularTotalDevido(@PathVariable Long usuarioId) {
        BigDecimal totalDevido = divisaoService.calcularTotalDevido(usuarioId);
        return ResponseEntity.ok(totalDevido);
    }
    
    @GetMapping("/usuario/{usuarioId}/pendentes-receber")
    public ResponseEntity<List<Divisao>> listarDivisoesPendentesParaReceber(@PathVariable Long usuarioId) {
        List<Divisao> divisoes = divisaoService.listarDivisoesPendentesParaReceber(usuarioId);
        return ResponseEntity.ok(divisoes);
    }
    
    @PatchMapping("/{divisaoId}/marcar-paga")
    public ResponseEntity<String> marcarDivisaoComoPaga(@PathVariable Long divisaoId) {
        divisaoService.marcarDivisaoComoPaga(divisaoId);
        return ResponseEntity.ok("Divisão marcada como paga!");
    }
}
