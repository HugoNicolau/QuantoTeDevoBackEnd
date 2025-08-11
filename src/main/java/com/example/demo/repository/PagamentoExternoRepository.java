package com.example.demo.repository;

import com.example.demo.model.PagamentoExterno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoExternoRepository extends JpaRepository<PagamentoExterno, String> {
    
    /**
     * Busca pagamentos por conta e usuário criador
     */
    List<PagamentoExterno> findByContaIdAndCriadoPorId(Long contaId, Long criadoPorId);
    
    /**
     * Busca pagamentos ativos (não expirados)
     */
    @Query("SELECT p FROM PagamentoExterno p WHERE p.dataExpiracao > :agora")
    List<PagamentoExterno> findAtivos(@Param("agora") LocalDateTime agora);
    
    /**
     * Busca pagamentos pendentes de uma conta
     */
    @Query("SELECT p FROM PagamentoExterno p WHERE p.contaId = :contaId AND p.pago = false AND p.dataExpiracao > :agora")
    List<PagamentoExterno> findPendentesPorConta(@Param("contaId") Long contaId, @Param("agora") LocalDateTime agora);
    
    /**
     * Busca por ID apenas se não expirado
     */
    @Query("SELECT p FROM PagamentoExterno p WHERE p.id = :id AND p.dataExpiracao > :agora")
    Optional<PagamentoExterno> findByIdAndAtivo(@Param("id") String id, @Param("agora") LocalDateTime agora);
    
    /**
     * Conta pagamentos por usuário criador
     */
    @Query("SELECT COUNT(p) FROM PagamentoExterno p WHERE p.criadoPorId = :usuarioId")
    Long countByCreator(@Param("usuarioId") Long usuarioId);
    
    /**
     * Busca pagamentos por usuário criador (todos os status)
     */
    List<PagamentoExterno> findByCriadoPorIdOrderByDataCriacaoDesc(Long criadoPorId);
    
    /**
     * Busca pagamentos expirados para limpeza
     */
    @Query("SELECT p FROM PagamentoExterno p WHERE p.dataExpiracao < :agora")
    List<PagamentoExterno> findExpirados(@Param("agora") LocalDateTime agora);
    
    /**
     * Busca pagamentos por status
     */
    List<PagamentoExterno> findByPagoAndDataExpiracaoAfter(Boolean pago, LocalDateTime dataExpiracao);
}
