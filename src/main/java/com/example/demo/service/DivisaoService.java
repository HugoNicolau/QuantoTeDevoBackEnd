package com.example.demo.service;

import com.example.demo.dto.DivisaoContaDTO;
import com.example.demo.dto.MarcarPagamentoDTO;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Conta;
import com.example.demo.model.Divisao;
import com.example.demo.model.Usuario;
import com.example.demo.repository.DivisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisaoService {
    
    private final DivisaoRepository divisaoRepository;
    private final ContaService contaService;
    private final UsuarioService usuarioService;
    
    @Transactional
    public void dividirConta(DivisaoContaDTO divisaoContaDTO) {
        Conta conta = contaService.encontrarContaPorId(divisaoContaDTO.getContaId());
        
        // Validar se a soma das divisões é igual ao valor da conta
        BigDecimal somaValores = divisaoContaDTO.getUsuarios().stream()
            .map(DivisaoContaDTO.DivisaoUsuarioDTO::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (somaValores.compareTo(conta.getValor()) != 0) {
            throw new NegocioException("A soma das divisões deve ser igual ao valor total da conta");
        }
        
        // Remover divisões existentes (se houver)
        List<Divisao> divisoesExistentes = divisaoRepository.findByConta(conta);
        divisaoRepository.deleteAll(divisoesExistentes);
        
        // Criar novas divisões
        for (DivisaoContaDTO.DivisaoUsuarioDTO divisaoUsuario : divisaoContaDTO.getUsuarios()) {
            Usuario usuario = usuarioService.encontrarUsuarioPorId(divisaoUsuario.getUsuarioId());
            
            Divisao divisao = Divisao.builder()
                .conta(conta)
                .usuario(usuario)
                .valor(divisaoUsuario.getValor())
                .pago(false)
                .build();
            
            divisaoRepository.save(divisao);
        }
    }
    
    @Transactional
    public void dividirContaIgualmente(Long contaId, List<Long> usuarioIds) {
        Conta conta = contaService.encontrarContaPorId(contaId);
        
        if (usuarioIds.isEmpty()) {
            throw new NegocioException("É necessário pelo menos um usuário para dividir a conta");
        }
        
        BigDecimal valorPorPessoa = conta.getValor().divide(
            BigDecimal.valueOf(usuarioIds.size()), 2, BigDecimal.ROUND_HALF_UP);
        
        // Remover divisões existentes (se houver)
        List<Divisao> divisoesExistentes = divisaoRepository.findByConta(conta);
        divisaoRepository.deleteAll(divisoesExistentes);
        
        // Criar divisões iguais
        for (Long usuarioId : usuarioIds) {
            Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
            
            Divisao divisao = Divisao.builder()
                .conta(conta)
                .usuario(usuario)
                .valor(valorPorPessoa)
                .pago(false)
                .build();
            
            divisaoRepository.save(divisao);
        }
    }
    
    @Transactional(readOnly = true)
    public List<Divisao> listarDivisoesDaConta(Long contaId) {
        Conta conta = contaService.encontrarContaPorId(contaId);
        return divisaoRepository.findByConta(conta);
    }
    
    @Transactional(readOnly = true)
    public List<Divisao> listarDivisoesDoUsuario(Long usuarioId, Boolean pago) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        if (pago != null) {
            return divisaoRepository.findByUsuarioAndPago(usuario, pago);
        }
        
        return divisaoRepository.findByUsuario(usuario);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalDevido(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        BigDecimal total = divisaoRepository.calcularTotalDevidoPorUsuario(usuario);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public List<Divisao> listarDivisoesPendentesParaReceber(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        return divisaoRepository.findDivisoesPendentesParaReceber(usuario);
    }
    
    @Transactional
    public void marcarDivisaoComoPaga(Long divisaoId) {
        Divisao divisao = divisaoRepository.findById(divisaoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Divisão", divisaoId));
        
        divisao.setPago(true);
        divisao.setDataPagamento(LocalDateTime.now());
        divisaoRepository.save(divisao);
        
        // Verificar se todas as divisões da conta foram pagas
        List<Divisao> divisoesDaConta = divisaoRepository.findByConta(divisao.getConta());
        boolean todasPagas = divisoesDaConta.stream().allMatch(Divisao::getPago);
        
        if (todasPagas) {
            // Marcar a conta como paga
            contaService.marcarComoPaga(divisao.getConta().getId());
        }
    }
    
    // RF10: Método aprimorado para marcar pagamento com detalhes
    @Transactional
    public void marcarDivisaoComoPaga(Long divisaoId, MarcarPagamentoDTO pagamentoDTO) {
        Divisao divisao = divisaoRepository.findById(divisaoId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Divisão", divisaoId));
        
        if (divisao.getPago()) {
            throw new NegocioException("Esta divisão já foi marcada como paga");
        }
        
        divisao.setPago(true);
        divisao.setDataPagamento(pagamentoDTO.getDataPagamento());
        divisao.setFormaPagamento(pagamentoDTO.getFormaPagamento());
        divisaoRepository.save(divisao);
        
        // Verificar se todas as divisões da conta foram pagas
        List<Divisao> divisoesDaConta = divisaoRepository.findByConta(divisao.getConta());
        boolean todasPagas = divisoesDaConta.stream().allMatch(Divisao::getPago);
        
        if (todasPagas) {
            // Marcar a conta como paga
            contaService.marcarComoPaga(divisao.getConta().getId());
        }
    }
}
