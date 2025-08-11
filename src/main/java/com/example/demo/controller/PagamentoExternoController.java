package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.service.PagamentoExternoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/pagamentos-externos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoExternoController {
    
    private final PagamentoExternoService pagamentoExternoService;
    
    /**
     * Criar link de pagamento (ROTA AUTENTICADA)
     */
    @PostMapping("/criar-link")
    public ResponseEntity<CriarLinkPagamentoResponseDTO> criarLink(
            @Valid @RequestBody CriarLinkPagamentoDTO request,
            HttpServletRequest httpRequest) {
        try {
            // Pegar usuário autenticado do contexto
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            
            // Garantir que o usuário autenticado seja o mesmo do request
            if (!usuarioId.equals(request.getCriadoPorId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            CriarLinkPagamentoResponseDTO response = pagamentoExternoService.criarLinkPagamento(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Erro ao criar link de pagamento: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Buscar informações do pagamento (ROTA PÚBLICA)
     */
    @GetMapping("/public/{id}")
    public ResponseEntity<PagamentoExternoDTO> buscarPagamento(@PathVariable String id) {
        try {
            PagamentoExternoDTO pagamento = pagamentoExternoService.buscarPorIdPublico(id);
            return ResponseEntity.ok(pagamento);
            
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Confirmar pagamento (ROTA PÚBLICA)
     */
    @PostMapping("/public/{id}/confirmar")
    public ResponseEntity<PagamentoExternoDTO> confirmarPagamento(
            @PathVariable String id,
            @Valid @RequestBody ConfirmarPagamentoExternoDTO request) {
        try {
            PagamentoExternoDTO pagamento = pagamentoExternoService.confirmarPagamento(id, request);
            return ResponseEntity.ok(pagamento);
            
        } catch (Exception e) {
            log.error("Erro ao confirmar pagamento {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Listar pagamentos de uma conta (ROTA AUTENTICADA)
     */
    @GetMapping("/conta/{contaId}")
    public ResponseEntity<List<PagamentoExternoDTO>> listarPorConta(
            @PathVariable Long contaId,
            HttpServletRequest httpRequest) {
        try {
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            List<PagamentoExternoDTO> pagamentos = pagamentoExternoService.listarPorConta(contaId, usuarioId);
            return ResponseEntity.ok(pagamentos);
            
        } catch (Exception e) {
            log.error("Erro ao listar pagamentos da conta {}: {}", contaId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Listar todos os pagamentos do usuário (ROTA AUTENTICADA)
     */
    @GetMapping("/usuario")
    public ResponseEntity<List<PagamentoExternoDTO>> listarPorUsuario(HttpServletRequest httpRequest) {
        try {
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            List<PagamentoExternoDTO> pagamentos = pagamentoExternoService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(pagamentos);
            
        } catch (Exception e) {
            log.error("Erro ao listar pagamentos do usuário: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Listar pagamentos pendentes de uma conta (ROTA AUTENTICADA)
     */
    @GetMapping("/conta/{contaId}/pendentes")
    public ResponseEntity<List<PagamentoExternoDTO>> listarPendentes(
            @PathVariable Long contaId,
            HttpServletRequest httpRequest) {
        try {
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            
            // Verificar permissão através do service
            List<PagamentoExternoDTO> pagamentos = pagamentoExternoService.listarPorConta(contaId, usuarioId);
            List<PagamentoExternoDTO> pendentes = pagamentos.stream()
                .filter(p -> !p.getPago() && !p.isExpirado())
                .toList();
            
            return ResponseEntity.ok(pendentes);
            
        } catch (Exception e) {
            log.error("Erro ao listar pagamentos pendentes da conta {}: {}", contaId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obter estatísticas dos pagamentos do usuário (ROTA AUTENTICADA)
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasPagamentoExternoDTO> obterEstatisticas(HttpServletRequest httpRequest) {
        try {
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            EstatisticasPagamentoExternoDTO estatisticas = pagamentoExternoService.obterEstatisticas(usuarioId);
            return ResponseEntity.ok(estatisticas);
            
        } catch (Exception e) {
            log.error("Erro ao obter estatísticas de pagamentos: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Buscar pagamento por ID (ROTA AUTENTICADA) - para o criador do link
     */
    @GetMapping("/{id}")
    public ResponseEntity<PagamentoExternoDTO> buscarPorId(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        try {
            Long usuarioId = (Long) httpRequest.getAttribute("userId");
            Optional<PagamentoExternoDTO> pagamento = pagamentoExternoService.buscarPorId(id);
            
            if (pagamento.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Verificar se o usuário tem permissão (é o criador)
            if (!pagamento.get().getCriadoPorId().equals(usuarioId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            return ResponseEntity.ok(pagamento.get());
            
        } catch (Exception e) {
            log.error("Erro ao buscar pagamento autenticado {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Rota administrativa para limpeza de links expirados (ROTA AUTENTICADA)
     */
    @PostMapping("/limpar-expirados")
    public ResponseEntity<String> limparExpirados() {
        try {
            pagamentoExternoService.limparPagamentosExpirados();
            return ResponseEntity.ok("Limpeza de pagamentos expirados executada com sucesso");
            
        } catch (Exception e) {
            log.error("Erro ao limpar pagamentos expirados: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Erro interno do servidor");
        }
    }
}
