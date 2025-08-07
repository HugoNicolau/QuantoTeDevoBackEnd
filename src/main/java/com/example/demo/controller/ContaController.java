package com.example.demo.controller;

import com.example.demo.dto.ContaDTO;
import com.example.demo.service.ContaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class ContaController {
    
    private final ContaService contaService;
    
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
    
    @GetMapping
    @Deprecated // Esta rota deve ser usada apenas para fins administrativos
    public ResponseEntity<List<ContaDTO>> listarTodasContas(
            @RequestParam(required = false) Boolean paga,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoInicial,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate vencimentoFinal) {
        List<ContaDTO> contas;
        
        // ATENÇÃO: Esta rota retorna TODAS as contas do sistema (uso administrativo)
        if (vencimentoInicial != null && vencimentoFinal != null) {
            if (paga != null) {
                contas = contaService.listarContasPorStatusEPeriodo(paga, vencimentoInicial, vencimentoFinal);
            } else {
                contas = contaService.listarContasPorPeriodo(vencimentoInicial, vencimentoFinal);
            }
        } else if (paga != null) {
            contas = contaService.listarContasPorStatus(paga);
        } else {
            // Se não especificado, listar todas as contas não pagas por padrão
            contas = contaService.listarContasPorStatus(false);
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
    
    @GetMapping("/vencidas")
    @Deprecated // Esta rota deve ser usada apenas para fins administrativos
    public ResponseEntity<List<ContaDTO>> listarContasVencidas() {
        List<ContaDTO> contasVencidas = contaService.listarContasVencidas();
        return ResponseEntity.ok(contasVencidas);
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) {
        contaService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}
