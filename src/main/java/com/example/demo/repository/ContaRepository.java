package com.example.demo.repository;

import com.example.demo.model.Conta;
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
    
    @Query("SELECT c FROM Conta c JOIN c.divisoes d WHERE d.usuario = :usuario")
    List<Conta> findContasDoUsuario(@Param("usuario") Usuario usuario);
    
    @Query("SELECT c FROM Conta c WHERE c.criador = :usuario OR c.id IN " +
           "(SELECT d.conta.id FROM Divisao d WHERE d.usuario = :usuario)")
    List<Conta> findContasRelacionadasAoUsuario(@Param("usuario") Usuario usuario);
}
