package com.example.demo.service;

import com.example.demo.dto.EstatisticasNotificacaoDTO;
import com.example.demo.dto.NotificacaoDTO;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.*;
import com.example.demo.repository.NotificacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoService {
    
    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioService usuarioService;
    
    @Transactional
    public NotificacaoDTO criarNotificacao(Long usuarioId, TipoNotificacao tipo, String titulo, 
                                         String mensagem, PrioridadeNotificacao prioridade) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        Notificacao notificacao = Notificacao.builder()
            .usuario(usuario)
            .tipo(tipo)
            .titulo(titulo)
            .mensagem(mensagem)
            .prioridade(prioridade)
            .lida(false)
            .build();
        
        notificacao = notificacaoRepository.save(notificacao);
        log.info("Notificação criada: {} para usuário {}", tipo, usuarioId);
        
        return NotificacaoDTO.fromModel(notificacao);
    }
    
    @Transactional
    public NotificacaoDTO criarNotificacaoComReferencia(Long usuarioId, TipoNotificacao tipo, String titulo, 
                                                      String mensagem, PrioridadeNotificacao prioridade,
                                                      Long referenciaId, String referenciaTipo) {
        
        // Verificar se já existe notificação similar recente para evitar spam
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        LocalDateTime desde = LocalDateTime.now().minusHours(1); // Últimas 1 hora
        
        Long notificacoesSimilares = notificacaoRepository.countNotificacoesSimilares(
            usuario, tipo, referenciaId, desde);
        
        if (notificacoesSimilares > 0) {
            log.info("Notificação similar já existe para usuário {} - tipo {} - referência {}", 
                usuarioId, tipo, referenciaId);
            return null; // Não criar duplicata
        }
        
        Notificacao notificacao = Notificacao.builder()
            .usuario(usuario)
            .tipo(tipo)
            .titulo(titulo)
            .mensagem(mensagem)
            .prioridade(prioridade)
            .referenciaId(referenciaId)
            .referenciaTipo(referenciaTipo)
            .lida(false)
            .build();
        
        notificacao = notificacaoRepository.save(notificacao);
        log.info("Notificação com referência criada: {} para usuário {} - ref: {}", 
            tipo, usuarioId, referenciaId);
        
        return NotificacaoDTO.fromModel(notificacao);
    }
    
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesDoUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Notificacao> notificacoes = notificacaoRepository.findNotificacoesNaoExpiradas(
            usuario, LocalDateTime.now());
        
        return notificacoes.stream()
            .map(NotificacaoDTO::fromModel)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesNaoLidas(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndLidaOrderByDataCriacaoDesc(
            usuario, false);
        
        return notificacoes.stream()
            .filter(n -> !n.isExpirada())
            .map(NotificacaoDTO::fromModel)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<NotificacaoDTO> listarNotificacoesPorTipo(Long usuarioId, TipoNotificacao tipo) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioAndTipoOrderByDataCriacaoDesc(
            usuario, tipo);
        
        return notificacoes.stream()
            .filter(n -> !n.isExpirada())
            .map(NotificacaoDTO::fromModel)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public NotificacaoDTO marcarComoLida(Long notificacaoId) {
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", notificacaoId));
        
        notificacao.marcarComoLida();
        notificacao = notificacaoRepository.save(notificacao);
        
        return NotificacaoDTO.fromModel(notificacao);
    }
    
    @Transactional
    public void marcarTodasComoLidas(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Notificacao> notificacoesNaoLidas = notificacaoRepository.findByUsuarioAndLidaOrderByDataCriacaoDesc(
            usuario, false);
        
        notificacoesNaoLidas.forEach(Notificacao::marcarComoLida);
        notificacaoRepository.saveAll(notificacoesNaoLidas);
        
        log.info("Todas as notificações marcadas como lidas para usuário {}", usuarioId);
    }
    
    @Transactional(readOnly = true)
    public Long contarNotificacoesNaoLidas(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        return notificacaoRepository.countNotificacoesNaoLidas(usuario, LocalDateTime.now());
    }
    
    @Transactional(readOnly = true)
    public EstatisticasNotificacaoDTO obterEstatisticas(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        LocalDateTime agora = LocalDateTime.now();
        
        List<Notificacao> todasNotificacoes = notificacaoRepository.findNotificacoesNaoExpiradas(usuario, agora);
        
        Long total = (long) todasNotificacoes.size();
        Long naoLidas = todasNotificacoes.stream().mapToLong(n -> n.getLida() ? 0 : 1).sum();
        Long lidas = total - naoLidas;
        
        List<Notificacao> expiradas = notificacaoRepository.findNotificacoesExpiradas(agora);
        Long totalExpiradas = expiradas.stream()
            .filter(n -> n.getUsuario().equals(usuario))
            .mapToLong(n -> 1L).sum();
        
        // Contar por tipo
        Long contaVencendo = contarPorTipo(todasNotificacoes, TipoNotificacao.CONTA_VENCENDO);
        Long contaVencida = contarPorTipo(todasNotificacoes, TipoNotificacao.CONTA_VENCIDA);
        Long dividaPendente = contarPorTipo(todasNotificacoes, TipoNotificacao.DIVIDA_PENDENTE);
        Long divisaoPendente = contarPorTipo(todasNotificacoes, TipoNotificacao.DIVISAO_PENDENTE);
        Long conviteRecebido = contarPorTipo(todasNotificacoes, TipoNotificacao.CONVITE_RECEBIDO);
        
        return EstatisticasNotificacaoDTO.criar(
            total, naoLidas, lidas, totalExpiradas,
            contaVencendo, contaVencida, dividaPendente, 
            divisaoPendente, conviteRecebido
        );
    }
    
    private Long contarPorTipo(List<Notificacao> notificacoes, TipoNotificacao tipo) {
        return notificacoes.stream()
            .filter(n -> n.getTipo() == tipo)
            .mapToLong(n -> 1L).sum();
    }
    
    @Transactional
    public void limparNotificacoesExpiradas() {
        int removidas = notificacaoRepository.deleteNotificacoesExpiradas(LocalDateTime.now());
        if (removidas > 0) {
            log.info("Removidas {} notificações expiradas", removidas);
        }
    }
    
    // ==================== MÉTODOS ESPECÍFICOS DE NEGÓCIO ====================
    
    @Async
    @Transactional
    public void notificarContaVencendo(Long usuarioId, Long contaId, String descricaoConta, 
                                     BigDecimal valor, int diasParaVencer) {
        String titulo = "⏰ Conta vencendo em " + diasParaVencer + " dias";
        String mensagem = String.format("A conta '%s' no valor de R$ %.2f vence em %d dias.", 
            descricaoConta, valor, diasParaVencer);
        
        PrioridadeNotificacao prioridade = diasParaVencer <= 2 ? PrioridadeNotificacao.ALTA : PrioridadeNotificacao.MEDIA;
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.CONTA_VENCENDO, titulo, mensagem, 
            prioridade, contaId, "CONTA");
    }
    
    @Async
    @Transactional
    public void notificarContaVencida(Long usuarioId, Long contaId, String descricaoConta, BigDecimal valor) {
        String titulo = "🚨 Conta vencida";
        String mensagem = String.format("A conta '%s' no valor de R$ %.2f está vencida!", 
            descricaoConta, valor);
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.CONTA_VENCIDA, titulo, mensagem, 
            PrioridadeNotificacao.URGENTE, contaId, "CONTA");
    }
    
    @Async
    @Transactional
    public void notificarDividaPendente(Long usuarioId, Long dividaId, String descricao, 
                                      BigDecimal valor, String nomeCredor) {
        String titulo = "💰 Dívida pendente";
        String mensagem = String.format("Você deve R$ %.2f para %s: %s", 
            valor, nomeCredor, descricao);
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.DIVIDA_PENDENTE, titulo, mensagem, 
            PrioridadeNotificacao.MEDIA, dividaId, "DIVIDA");
    }
    
    @Async
    @Transactional
    public void notificarDivisaoPendente(Long usuarioId, Long divisaoId, String descricaoConta, 
                                       BigDecimal valor, String nomeCriador) {
        String titulo = "💳 Divisão pendente";
        String mensagem = String.format("Você deve R$ %.2f para %s pela conta: %s", 
            valor, nomeCriador, descricaoConta);
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.DIVISAO_PENDENTE, titulo, mensagem, 
            PrioridadeNotificacao.MEDIA, divisaoId, "DIVISAO");
    }
    
    @Async
    @Transactional
    public void notificarPagamentoRecebido(Long usuarioId, BigDecimal valor, String pagador, String descricao) {
        String titulo = "✅ Pagamento recebido";
        String mensagem = String.format("Você recebeu R$ %.2f de %s: %s", 
            valor, pagador, descricao);
        
        criarNotificacao(usuarioId, TipoNotificacao.PAGAMENTO_RECEBIDO, titulo, mensagem, 
            PrioridadeNotificacao.BAIXA);
    }
    
    @Async
    @Transactional
    public void notificarConviteRecebido(Long usuarioId, Long conviteId, String remetente, 
                                       String descricaoConta, BigDecimal valorSugerido) {
        String titulo = "📧 Convite recebido";
        String mensagem = String.format("%s te convidou para participar da conta '%s'%s", 
            remetente, descricaoConta, 
            valorSugerido != null ? String.format(" - Valor sugerido: R$ %.2f", valorSugerido) : "");
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.CONVITE_RECEBIDO, titulo, mensagem, 
            PrioridadeNotificacao.MEDIA, conviteId, "CONVITE");
    }
    
    @Async
    @Transactional
    public void notificarNovaContaCriada(Long usuarioId, Long contaId, String descricao, BigDecimal valor) {
        String titulo = "📋 Nova conta criada";
        String mensagem = String.format("Uma nova conta foi criada: '%s' - R$ %.2f", descricao, valor);
        
        criarNotificacaoComReferencia(usuarioId, TipoNotificacao.CONTA_CRIADA, titulo, mensagem, 
            PrioridadeNotificacao.BAIXA, contaId, "CONTA");
    }
}
