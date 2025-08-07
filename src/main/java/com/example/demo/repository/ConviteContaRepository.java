package com.example.demo.repository;

import com.example.demo.model.ConviteConta;
import com.example.demo.model.StatusConvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConviteContaRepository extends JpaRepository<ConviteConta, Long> {
    
    Optional<ConviteConta> findByToken(String token);
    
    List<ConviteConta> findByEmailConvidadoAndStatus(String emailConvidado, StatusConvite status);
    
    List<ConviteConta> findByUsuarioConvidadoIdAndStatus(Long usuarioId, StatusConvite status);
    
    List<ConviteConta> findByContaId(Long contaId);
    
    List<ConviteConta> findByUsuarioConvidanteId(Long usuarioId);
    
    @Query("SELECT c FROM ConviteConta c WHERE c.status = :status AND c.dataExpiracao < :agora")
    List<ConviteConta> findByStatusAndDataExpiracaoBefore(@Param("status") StatusConvite status, @Param("agora") LocalDateTime agora);
    
    @Query("SELECT c FROM ConviteConta c WHERE c.emailConvidado = :email AND c.conta.id = :contaId AND c.status = :status")
    Optional<ConviteConta> findByEmailConvidadoAndContaIdAndStatus(@Param("email") String email, @Param("contaId") Long contaId, @Param("status") StatusConvite status);
    
    boolean existsByEmailConvidadoAndContaIdAndStatus(String emailConvidado, Long contaId, StatusConvite status);
}
