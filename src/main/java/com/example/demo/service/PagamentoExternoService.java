package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Conta;
import com.example.demo.model.PagamentoExterno;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PagamentoExternoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PagamentoExternoService {
    
    private final PagamentoExternoRepository pagamentoExternoRepository;
    private final ContaService contaService;
    private final UsuarioService usuarioService;
    private final NotificacaoService notificacaoService;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Transactional
    public CriarLinkPagamentoResponseDTO criarLinkPagamento(CriarLinkPagamentoDTO request) {
        log.info("Criando link de pagamento para: {}", request.getNomeParticipante());
        
        // Validar se a conta existe e se o usuário tem permissão
        Conta conta = contaService.encontrarContaPorId(request.getContaId());
        Usuario criador = usuarioService.encontrarUsuarioPorId(request.getCriadoPorId());
        
        // Verificar se o usuário é o criador da conta ou tem permissão
        if (!conta.getCriador().getId().equals(request.getCriadoPorId())) {
            throw new NegocioException("Apenas o criador da conta pode gerar links de pagamento");
        }
        
        // Gerar ID único para o link (32 caracteres)
        String linkId = UUID.randomUUID().toString().replace("-", "");
        
        // Definir validade do link (padrão 30 dias)
        int diasValidade = request.getDiasValidade() != null ? request.getDiasValidade() : 30;
        LocalDateTime dataExpiracao = LocalDateTime.now().plusDays(diasValidade);
        
        // Criar entidade
        PagamentoExterno pagamento = PagamentoExterno.builder()
            .id(linkId)
            .nomeParticipante(request.getNomeParticipante())
            .valor(request.getValor())
            .descricaoDespesa(request.getDescricaoDespesa())
            .criadoPor(request.getCriadoPor())
            .criadoPorId(request.getCriadoPorId())
            .contaId(request.getContaId())
            .dataVencimento(request.getDataVencimento())
            .observacoes(request.getObservacoes())
            .dataExpiracao(dataExpiracao)
            .build();
        
        pagamentoExternoRepository.save(pagamento);
        
        // Notificar o criador sobre o link gerado
        notificacaoService.notificarLinkPagamentoCriado(
            request.getCriadoPorId(),
            linkId,
            request.getNomeParticipante(),
            request.getValor()
        );
        
        log.info("Link de pagamento criado com ID: {}", linkId);
        return new CriarLinkPagamentoResponseDTO(linkId, baseUrl);
    }
    
    @Transactional(readOnly = true)
    public Optional<PagamentoExternoDTO> buscarPorId(String id) {
        Optional<PagamentoExterno> pagamento = pagamentoExternoRepository.findByIdAndAtivo(id, LocalDateTime.now());
        return pagamento.map(PagamentoExternoDTO::new);
    }
    
    @Transactional(readOnly = true)
    public PagamentoExternoDTO buscarPorIdPublico(String id) {
        PagamentoExterno pagamento = pagamentoExternoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Link de pagamento não encontrado"));
        
        if (pagamento.isExpirado()) {
            throw new NegocioException("Este link de pagamento expirou");
        }
        
        return new PagamentoExternoDTO(pagamento);
    }
    
    @Transactional
    public PagamentoExternoDTO confirmarPagamento(String id, ConfirmarPagamentoExternoDTO request) {
        log.info("Confirmando pagamento para link: {}", id);
        
        PagamentoExterno pagamento = pagamentoExternoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Link de pagamento não encontrado"));
        
        // Validações
        if (pagamento.getPago()) {
            throw new NegocioException("Este pagamento já foi confirmado");
        }
        
        if (pagamento.isExpirado()) {
            throw new NegocioException("Este link de pagamento expirou");
        }
        
        // Confirmar pagamento
        pagamento.setPago(true);
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setFormaPagamento(request.getFormaPagamento());
        
        // Combinar observações se existirem ambas
        String observacoesCombinadas = combinarObservacoes(
            pagamento.getObservacoes(), 
            request.getObservacoes()
        );
        pagamento.setObservacoes(observacoesCombinadas);
        
        PagamentoExterno pagamentoAtualizado = pagamentoExternoRepository.save(pagamento);
        
        // Notificar o criador sobre o pagamento confirmado
        notificacaoService.notificarPagamentoExternoConfirmado(
            pagamento.getCriadoPorId(),
            pagamento.getId(),
            pagamento.getNomeParticipante(),
            pagamento.getValor(),
            request.getFormaPagamento()
        );
        
        log.info("Pagamento confirmado para link: {} por {}", id, pagamento.getNomeParticipante());
        return new PagamentoExternoDTO(pagamentoAtualizado);
    }
    
    @Transactional(readOnly = true)
    public List<PagamentoExternoDTO> listarPorConta(Long contaId, Long usuarioId) {
        // Verificar se o usuário tem permissão para ver os pagamentos desta conta
        Conta conta = contaService.encontrarContaPorId(contaId);
        
        if (!conta.getCriador().getId().equals(usuarioId)) {
            throw new NegocioException("Você não tem permissão para ver os pagamentos desta conta");
        }
        
        List<PagamentoExterno> pagamentos = pagamentoExternoRepository.findByContaIdAndCriadoPorId(contaId, usuarioId);
        return pagamentos.stream()
            .map(PagamentoExternoDTO::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PagamentoExternoDTO> listarPorUsuario(Long usuarioId) {
        List<PagamentoExterno> pagamentos = pagamentoExternoRepository.findByCriadoPorIdOrderByDataCriacaoDesc(usuarioId);
        return pagamentos.stream()
            .map(PagamentoExternoDTO::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PagamentoExternoDTO> listarPendentes(Long contaId) {
        List<PagamentoExterno> pagamentos = pagamentoExternoRepository.findPendentesPorConta(contaId, LocalDateTime.now());
        return pagamentos.stream()
            .map(PagamentoExternoDTO::new)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public long contarPagamentosPorUsuario(Long usuarioId) {
        return pagamentoExternoRepository.countByCreator(usuarioId);
    }
    
    @Transactional
    public void limparPagamentosExpirados() {
        log.info("Iniciando limpeza de pagamentos expirados");
        
        List<PagamentoExterno> expirados = pagamentoExternoRepository.findExpirados(LocalDateTime.now());
        
        if (!expirados.isEmpty()) {
            log.info("Encontrados {} pagamentos expirados para limpeza", expirados.size());
            pagamentoExternoRepository.deleteAll(expirados);
        }
    }
    
    @Transactional(readOnly = true)
    public EstatisticasPagamentoExternoDTO obterEstatisticas(Long usuarioId) {
        List<PagamentoExterno> todosPagamentos = pagamentoExternoRepository.findByCriadoPorIdOrderByDataCriacaoDesc(usuarioId);
        
        long total = todosPagamentos.size();
        long pagos = todosPagamentos.stream().mapToLong(p -> p.getPago() ? 1 : 0).sum();
        long pendentes = total - pagos;
        long expirados = todosPagamentos.stream().mapToLong(p -> p.isExpirado() && !p.getPago() ? 1 : 0).sum();
        long vencidos = todosPagamentos.stream().mapToLong(p -> p.isVencido() ? 1 : 0).sum();
        
        return EstatisticasPagamentoExternoDTO.builder()
            .totalLinks(total)
            .linksPagos(pagos)
            .linksPendentes(pendentes)
            .linksExpirados(expirados)
            .linksVencidos(vencidos)
            .percentualPagos(total > 0 ? (pagos * 100.0 / total) : 0.0)
            .build();
    }
    
    // Métodos auxiliares
    private String combinarObservacoes(String observacoesOriginais, String observacoesPagamento) {
        if (observacoesOriginais == null || observacoesOriginais.trim().isEmpty()) {
            return observacoesPagamento;
        }
        
        if (observacoesPagamento == null || observacoesPagamento.trim().isEmpty()) {
            return observacoesOriginais;
        }
        
        return observacoesOriginais + "\n\nPagamento: " + observacoesPagamento;
    }
}
