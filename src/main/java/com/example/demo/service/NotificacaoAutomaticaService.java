package com.example.demo.service;

import com.example.demo.model.TipoNotificacao;
import com.example.demo.repository.ContaRepository;
import com.example.demo.repository.DivisaoRepository;
import com.example.demo.repository.DividaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoAutomaticaService {
    
    private final NotificacaoService notificacaoService;
    private final ContaRepository contaRepository;
    private final DivisaoRepository divisaoRepository;
    private final DividaRepository dividaRepository;
    
    /**
     * Executa verificações automáticas de notificações todos os dias às 09:00
     */
    @Scheduled(cron = "0 0 9 * * *") // Executa diariamente às 09:00
    @Transactional
    public void verificarNotificacoesAutomaticas() {
        log.info("Iniciando verificação automática de notificações");
        
        verificarContasVencendo();
        verificarContasVencidas();
        verificarDividasPendentes();
        verificarDivisoesPendentes();
        limparNotificacoesExpiradas();
        
        log.info("Verificação automática de notificações concluída");
    }
    
    /**
     * Verifica contas que estão próximas do vencimento (3, 7 e 15 dias)
     */
    @Transactional
    public void verificarContasVencendo() {
        LocalDate hoje = LocalDate.now();
        
        // Verificar contas vencendo em 1, 3, 7 e 15 dias
        int[] diasParaVerificar = {1, 3, 7, 15};
        
        for (int dias : diasParaVerificar) {
            LocalDate dataVencimento = hoje.plusDays(dias);
            
            contaRepository.findByVencimentoAndPagaFalse(dataVencimento).forEach(conta -> {
                // Notificar o criador da conta
                notificacaoService.notificarContaVencendo(
                    conta.getCriador().getId(),
                    conta.getId(),
                    conta.getDescricao(),
                    conta.getValor(),
                    dias
                );
                
                // Notificar usuários que têm divisões pendentes nesta conta
                divisaoRepository.findByContaAndPagoFalse(conta).forEach(divisao -> {
                    // Não notificar o criador novamente
                    if (!divisao.getUsuario().getId().equals(conta.getCriador().getId())) {
                        notificacaoService.notificarContaVencendo(
                            divisao.getUsuario().getId(),
                            conta.getId(),
                            conta.getDescricao(),
                            divisao.getValor(),
                            dias
                        );
                    }
                });
            });
        }
        
        log.info("Verificação de contas vencendo concluída");
    }
    
    /**
     * Verifica contas que já venceram
     */
    @Transactional
    public void verificarContasVencidas() {
        LocalDate hoje = LocalDate.now();
        
        contaRepository.findByVencimentoBeforeAndPagaFalse(hoje).forEach(conta -> {
            // Calcular dias em atraso
            long diasAtraso = ChronoUnit.DAYS.between(conta.getVencimento(), hoje);
            
            // Notificar apenas se não foi notificado recentemente (evitar spam)
            // Notificar nos dias: 1, 3, 7, 15, 30 dias de atraso
            if (diasAtraso == 1 || diasAtraso == 3 || diasAtraso == 7 || 
                diasAtraso == 15 || diasAtraso == 30 || diasAtraso % 30 == 0) {
                
                // Notificar o criador da conta
                notificacaoService.notificarContaVencida(
                    conta.getCriador().getId(),
                    conta.getId(),
                    conta.getDescricao(),
                    conta.getValor()
                );
                
                // Notificar usuários que têm divisões pendentes nesta conta
                divisaoRepository.findByContaAndPagoFalse(conta).forEach(divisao -> {
                    if (!divisao.getUsuario().getId().equals(conta.getCriador().getId())) {
                        notificacaoService.notificarContaVencida(
                            divisao.getUsuario().getId(),
                            conta.getId(),
                            conta.getDescricao(),
                            divisao.getValor()
                        );
                    }
                });
            }
        });
        
        log.info("Verificação de contas vencidas concluída");
    }
    
    /**
     * Verifica dívidas pendentes (semanalmente)
     */
    @Transactional
    public void verificarDividasPendentes() {
        // Notificar sobre dívidas pendentes uma vez por semana (domingos)
        LocalDate hoje = LocalDate.now();
        
        if (hoje.getDayOfWeek().getValue() == 7) { // Domingo
            dividaRepository.findByPagaFalse().forEach(divida -> {
                notificacaoService.notificarDividaPendente(
                    divida.getUsuarioDevedor().getId(),
                    divida.getId(),
                    divida.getDescricao(),
                    divida.getValor(),
                    divida.getUsuarioCredor().getNome()
                );
            });
            
            log.info("Verificação de dívidas pendentes concluída");
        }
    }
    
    /**
     * Verifica divisões pendentes (semanalmente)
     */
    @Transactional
    public void verificarDivisoesPendentes() {
        // Notificar sobre divisões pendentes uma vez por semana (domingos)
        LocalDate hoje = LocalDate.now();
        
        if (hoje.getDayOfWeek().getValue() == 7) { // Domingo
            divisaoRepository.findByPagoFalse().forEach(divisao -> {
                notificacaoService.notificarDivisaoPendente(
                    divisao.getUsuario().getId(),
                    divisao.getId(),
                    divisao.getConta().getDescricao(),
                    divisao.getValor(),
                    divisao.getConta().getCriador().getNome()
                );
            });
            
            log.info("Verificação de divisões pendentes concluída");
        }
    }
    
    /**
     * Remove notificações expiradas (executa uma vez por semana)
     */
    @Scheduled(cron = "0 0 2 * * SUN") // Executa domingos às 02:00
    @Transactional
    public void limparNotificacoesExpiradas() {
        notificacaoService.limparNotificacoesExpiradas();
        log.info("Limpeza de notificações expiradas concluída");
    }
    
    /**
     * Método para executar verificações sob demanda (útil para testes)
     */
    @Transactional
    public void executarVerificacaoManual() {
        log.info("Iniciando verificação manual de notificações");
        verificarNotificacoesAutomaticas();
    }
}
