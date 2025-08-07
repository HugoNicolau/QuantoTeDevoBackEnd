package com.example.demo.repository;

import com.example.demo.model.Compra;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    List<Compra> findByUsuarioCriador(Usuario usuarioCriador);
    
    List<Compra> findByUsuarioCriadorId(Long usuarioCriadorId);
    
    List<Compra> findByFinalizada(Boolean finalizada);
    
    List<Compra> findByUsuarioCriadorIdAndFinalizada(Long usuarioCriadorId, Boolean finalizada);
    
    List<Compra> findByDataCompraBetween(LocalDate dataInicial, LocalDate dataFinal);
    
    @Query("SELECT c FROM Compra c WHERE c.usuarioCriador.id = :usuarioId " +
           "AND c.dataCompra BETWEEN :dataInicial AND :dataFinal")
    List<Compra> findByUsuarioCriadorIdAndDataCompraBetween(
        @Param("usuarioId") Long usuarioId,
        @Param("dataInicial") LocalDate dataInicial,
        @Param("dataFinal") LocalDate dataFinal);
    
    // Buscar compras onde o usuário tem itens para pagar
    @Query("SELECT DISTINCT c FROM Compra c JOIN c.itens i WHERE i.usuarioResponsavel.id = :usuarioId")
    List<Compra> findComprasComItensDoUsuario(@Param("usuarioId") Long usuarioId);
    
    // Buscar compras não finalizadas onde o usuário tem itens para pagar
    @Query("SELECT DISTINCT c FROM Compra c JOIN c.itens i WHERE i.usuarioResponsavel.id = :usuarioId AND c.finalizada = false")
    List<Compra> findComprasAtivasComItensDoUsuario(@Param("usuarioId") Long usuarioId);
}
