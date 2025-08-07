package com.example.demo.controller;

import com.example.demo.dto.EstatisticasNotificacaoDTO;
import com.example.demo.dto.NotificacaoDTO;
import com.example.demo.model.TipoNotificacao;
import com.example.demo.service.NotificacaoAutomaticaService;
import com.example.demo.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificacaoController {
    
    private final NotificacaoService notificacaoService;
    private final NotificacaoAutomaticaService notificacaoAutomaticaService;
    
    /**
     * Lista todas as notificações do usuário (não expiradas)
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoes(@PathVariable Long usuarioId) {
        List<NotificacaoDTO> notificacoes = notificacaoService.listarNotificacoesDoUsuario(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }
    
    /**
     * Lista apenas notificações não lidas do usuário
     */
    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoesNaoLidas(@PathVariable Long usuarioId) {
        List<NotificacaoDTO> notificacoes = notificacaoService.listarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(notificacoes);
    }
    
    /**
     * Lista notificações por tipo específico
     */
    @GetMapping("/usuario/{usuarioId}/tipo/{tipo}")
    public ResponseEntity<List<NotificacaoDTO>> listarNotificacoesPorTipo(
            @PathVariable Long usuarioId, 
            @PathVariable TipoNotificacao tipo) {
        List<NotificacaoDTO> notificacoes = notificacaoService.listarNotificacoesPorTipo(usuarioId, tipo);
        return ResponseEntity.ok(notificacoes);
    }
    
    /**
     * Conta quantas notificações não lidas o usuário tem
     */
    @GetMapping("/usuario/{usuarioId}/contador")
    public ResponseEntity<Map<String, Object>> contarNotificacoesNaoLidas(@PathVariable Long usuarioId) {
        Long contador = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        Map<String, Object> resultado = Map.of(
            "naoLidas", contador,
            "temNovas", contador > 0
        );
        return ResponseEntity.ok(resultado);
    }
    
    /**
     * Obtém estatísticas completas das notificações do usuário
     */
    @GetMapping("/usuario/{usuarioId}/estatisticas")
    public ResponseEntity<EstatisticasNotificacaoDTO> obterEstatisticas(@PathVariable Long usuarioId) {
        EstatisticasNotificacaoDTO estatisticas = notificacaoService.obterEstatisticas(usuarioId);
        return ResponseEntity.ok(estatisticas);
    }
    
    /**
     * Marca uma notificação específica como lida
     */
    @PatchMapping("/{notificacaoId}/marcar-lida")
    public ResponseEntity<NotificacaoDTO> marcarComoLida(@PathVariable Long notificacaoId) {
        NotificacaoDTO notificacao = notificacaoService.marcarComoLida(notificacaoId);
        return ResponseEntity.ok(notificacao);
    }
    
    /**
     * Marca todas as notificações do usuário como lidas
     */
    @PatchMapping("/usuario/{usuarioId}/marcar-todas-lidas")
    public ResponseEntity<String> marcarTodasComoLidas(@PathVariable Long usuarioId) {
        notificacaoService.marcarTodasComoLidas(usuarioId);
        return ResponseEntity.ok("Todas as notificações foram marcadas como lidas");
    }
    
    /**
     * Força a execução das verificações automáticas de notificações (útil para testes/admin)
     */
    @PostMapping("/verificar-automaticas")
    public ResponseEntity<String> executarVerificacaoManual() {
        notificacaoAutomaticaService.executarVerificacaoManual();
        return ResponseEntity.ok("Verificação automática de notificações executada com sucesso");
    }
    
    /**
     * Endpoint para obter todos os tipos de notificação disponíveis
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<Map<String, String>>> listarTiposNotificacao() {
        List<Map<String, String>> tipos = List.of(
            Map.of("codigo", "CONTA_VENCENDO", "descricao", "Conta próxima do vencimento", "icone", "⏰"),
            Map.of("codigo", "CONTA_VENCIDA", "descricao", "Conta vencida", "icone", "🚨"),
            Map.of("codigo", "DIVIDA_PENDENTE", "descricao", "Dívida pendente", "icone", "💰"),
            Map.of("codigo", "DIVISAO_PENDENTE", "descricao", "Divisão pendente", "icone", "💳"),
            Map.of("codigo", "PAGAMENTO_RECEBIDO", "descricao", "Pagamento recebido", "icone", "✅"),
            Map.of("codigo", "CONVITE_RECEBIDO", "descricao", "Convite recebido", "icone", "📧"),
            Map.of("codigo", "CONTA_CRIADA", "descricao", "Nova conta criada", "icone", "📋"),
            Map.of("codigo", "LEMBRETE_PAGAMENTO", "descricao", "Lembrete de pagamento", "icone", "🔔"),
            Map.of("codigo", "SISTEMA", "descricao", "Notificação do sistema", "icone", "⚙️")
        );
        
        return ResponseEntity.ok(tipos);
    }
}
