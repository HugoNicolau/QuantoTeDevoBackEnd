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
    
    @Query("SELECT SUM(d.valor) FROM Divisao d WHERE d.usuario = :usuario AND d.pago = false")
    BigDecimal calcularTotalDevidoPorUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT d FROM Divisao d WHERE d.conta.criador = :usuario AND d.pago = false")
    List<Divisao> findDivisoesPendentesParaReceber(@Param("usuario") Usuario usuario);
}
