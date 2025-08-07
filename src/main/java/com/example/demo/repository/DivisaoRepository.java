package com.example.demo.repository;

import com.example.demo.model.Conta;
import com.example.demo.model.Divisao;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface DivisaoRepository extends JpaRepository<Divisao, Long> {
    
    List<Divisao> findByUsuario(Usuario usuario);
    
    List<Divisao> findByConta(Conta conta);
    
    List<Divisao> findByPago(Boolean pago);
    
    List<Divisao> findByUsuarioAndPago(Usuario usuario, Boolean pago);
    
    // Novos métodos para saldos
    List<Divisao> findByUsuarioIdAndPago(Long usuarioId, Boolean pago);
    
    @Query("SELECT d FROM Divisao d WHERE d.conta.criador.id = :criadorId AND d.pago = :pago")
    List<Divisao> findByContaCriadorIdAndPago(@Param("criadorId") Long criadorId, @Param("pago") Boolean pago);
    
    @Query("SELECT d FROM Divisao d WHERE d.conta.criador.id = :criadorId AND d.usuario.id = :usuarioId AND d.pago = :pago")
    List<Divisao> findByContaCriadorIdAndUsuarioIdAndPago(@Param("criadorId") Long criadorId, @Param("usuarioId") Long usuarioId, @Param("pago") Boolean pago);
    
    @Query("SELECT SUM(d.valor) FROM Divisao d WHERE d.usuario = :usuario AND d.pago = false")
    BigDecimal calcularTotalDevidoPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT d FROM Divisao d WHERE d.conta.criador = :usuario AND d.pago = false")
    List<Divisao> findDivisoesPendentesParaReceber(@Param("usuario") Usuario usuario);
}
