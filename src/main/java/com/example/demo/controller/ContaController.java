package com.example.demo.controller;

import com.example.demo.dto.ContaDTO;
import com.example.demo.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class ContaController {
    
    private final ContaService contaService;
    
    @GetMapping
    public ResponseEntity<List<ContaDTO>> listarContas(HttpServletRequest request) {
        // Pega o usuário autenticado do contexto de segurança
        Long usuarioId = (Long) request.getAttribute("userId");
        List<ContaDTO> contas = contaService.listarContasDoUsuario(usuarioId);
        return ResponseEntity.ok(contas);
    }
    
    @PostMapping
    public ResponseEntity<ContaDTO> criarConta(@Valid @RequestBody ContaDTO contaDTO) {
        ContaDTO contaCriada = contaService.criarConta(contaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(contaCriada);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscarConta(@PathVariable Long id) {
        ContaDTO conta = contaService.buscarPorId(id);
        return ResponseEntity.ok(conta);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ContaDTO>> listarContasDoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean paga) {
        // Rota simples sem filtros de data (mantida para compatibilidade)
        List<ContaDTO> contas;
        if (paga != null) {
            contas = contaService.listarContasDoUsuarioComFiltros(usuarioId, paga, null, null);
        } else {
            contas = contaService.listarContasDoUsuario(usuarioId);
        }
        return ResponseEntity.ok(contas);
    }
    
    // RF05: Nova rota específica para contas de um usuário com filtros
    @GetMapping("/usuario/{usuarioId}/filtros")
    public ResponseEntity<List<ContaDTO>> listarContasDoUsuarioComFiltros(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean paga,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoFinal) {
        
        List<ContaDTO> contas = contaService.listarContasDoUsuarioComFiltros(
            usuarioId, paga, vencimentoInicial, vencimentoFinal);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/usuario/{usuarioId}/vencidas")
    public ResponseEntity<List<ContaDTO>> listarContasVencidasDoUsuario(@PathVariable Long usuarioId) {
        List<ContaDTO> contasVencidas = contaService.listarContasVencidasDoUsuario(usuarioId);
        return ResponseEntity.ok(contasVencidas);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContaDTO> atualizarConta(
            @PathVariable Long id, 
            @Valid @RequestBody ContaDTO contaDTO) {
        ContaDTO contaAtualizada = contaService.atualizarConta(id, contaDTO);
        return ResponseEntity.ok(contaAtualizada);
    }
    
    @PatchMapping("/{id}/marcar-paga")
    public ResponseEntity<ContaDTO> marcarComoPaga(@PathVariable Long id) {
        ContaDTO contaAtualizada = contaService.marcarComoPaga(id);
        return ResponseEntity.ok(contaAtualizada);
    }
    
    @PatchMapping("/{id}/marcar-vencida")
    public ResponseEntity<ContaDTO> marcarComoVencida(@PathVariable Long id) {
        ContaDTO contaAtualizada = contaService.marcarComoVencida(id);
        return ResponseEntity.ok(contaAtualizada);
    }
    
    @PatchMapping("/{id}/marcar-pendente")
    public ResponseEntity<ContaDTO> marcarComoPendente(@PathVariable Long id) {
        ContaDTO contaAtualizada = contaService.marcarComoPendente(id);
        return ResponseEntity.ok(contaAtualizada);
    }
    
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<ContaDTO>> listarContasDoGrupo(
            @PathVariable Long grupoId,
            @RequestParam(required = false) Boolean paga) {
        List<ContaDTO> contas = contaService.listarContasDoGrupo(grupoId, paga);
        return ResponseEntity.ok(contas);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) {
        contaService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}
