package com.example.demo.repository;

import com.example.demo.model.Notificacao;
import com.example.demo.model.TipoNotificacao;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    List<Notificacao> findByUsuarioAndLidaOrderByDataCriacaoDesc(Usuario usuario, Boolean lida);
    
    List<Notificacao> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
    
    Long countByUsuarioAndLida(Usuario usuario, Boolean lida);
    
    List<Notificacao> findByUsuarioAndTipoOrderByDataCriacaoDesc(Usuario usuario, TipoNotificacao tipo);
    
    @Query("SELECT n FROM Notificacao n WHERE n.usuario = :usuario AND n.dataExpiracao > :agora ORDER BY n.dataCriacao DESC")
    List<Notificacao> findNotificacoesNaoExpiradas(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);
    
    @Query("SELECT n FROM Notificacao n WHERE n.dataExpiracao < :agora")
    List<Notificacao> findNotificacoesExpiradas(@Param("agora") LocalDateTime agora);
    
    @Modifying
    @Query("DELETE FROM Notificacao n WHERE n.dataExpiracao < :agora")
    int deleteNotificacoesExpiradas(@Param("agora") LocalDateTime agora);
    
    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.usuario = :usuario AND n.lida = false AND n.dataExpiracao > :agora")
    Long countNotificacoesNaoLidas(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);
    
    // Buscar notificações duplicadas para evitar spam
    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.usuario = :usuario AND n.tipo = :tipo AND n.referenciaId = :referenciaId AND n.dataCriacao > :desde")
    Long countNotificacoesSimilares(@Param("usuario") Usuario usuario, @Param("tipo") TipoNotificacao tipo, @Param("referenciaId") Long referenciaId, @Param("desde") LocalDateTime desde);
}
