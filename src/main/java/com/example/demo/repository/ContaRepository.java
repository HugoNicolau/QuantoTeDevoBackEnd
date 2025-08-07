package com.example.demo.repository;

import com.example.demo.model.Conta;
import com.example.demo.model.Grupo;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContaRepository extends JpaRepository<Conta, Long> {
    
    List<Conta> findByCriador(Usuario criador);
    
    List<Conta> findByPaga(Boolean paga);
    
    List<Conta> findByVencimentoBefore(LocalDate data);
    
    // RF05: Métodos para filtros por período
    List<Conta> findByVencimentoBetween(LocalDate inicio, LocalDate fim);
    
    List<Conta> findByPagaAndVencimentoBetween(Boolean paga, LocalDate inicio, LocalDate fim);
    
    @Query("SELECT c FROM Conta c JOIN c.divisoes d WHERE d.usuario = :usuario")
    List<Conta> findContasDoUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT c FROM Conta c WHERE c.criador = :usuario OR c.id IN " +
           "(SELECT d.conta.id FROM Divisao d WHERE d.usuario = :usuario)")
    List<Conta> findContasRelacionadasAoUsuario(@Param("usuario") Usuario usuario);
    
    // Métodos para grupos
    List<Conta> findByGrupo(Grupo grupo);
    
    List<Conta> findByGrupoAndPaga(Grupo grupo, Boolean paga);
    
    @Query("SELECT c FROM Conta c WHERE c.grupo = :grupo AND c.vencimento BETWEEN :inicio AND :fim")
    List<Conta> findByGrupoAndVencimentoBetween(@Param("grupo") Grupo grupo, 
                                               @Param("inicio") LocalDate inicio, 
                                               @Param("fim") LocalDate fim);
    
    // Métodos para notificações automáticas
    List<Conta> findByVencimentoAndPagaFalse(LocalDate vencimento);
    
    List<Conta> findByVencimentoBeforeAndPagaFalse(LocalDate data);
}
