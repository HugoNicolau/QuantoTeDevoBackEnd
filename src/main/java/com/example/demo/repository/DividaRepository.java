package com.example.demo.repository;

import com.example.demo.model.Divida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DividaRepository extends JpaRepository<Divida, Long> {
    
    List<Divida> findByUsuarioDevedorId(Long usuarioDevedorId);
    
    List<Divida> findByUsuarioCredorId(Long usuarioCredorId);
    
    List<Divida> findByUsuarioDevedorIdAndPaga(Long usuarioDevedorId, Boolean paga);
    
    List<Divida> findByUsuarioCredorIdAndPaga(Long usuarioCredorId, Boolean paga);
    
    List<Divida> findByDataVencimentoBeforeAndPagaFalse(LocalDate data);
    
    @Query("SELECT d FROM Divida d WHERE (d.usuarioDevedor.id = :usuarioId OR d.usuarioCredor.id = :usuarioId)")
    List<Divida> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    @Query("SELECT d FROM Divida d WHERE (d.usuarioDevedor.id = :usuarioId OR d.usuarioCredor.id = :usuarioId) AND d.paga = :paga")
    List<Divida> findByUsuarioIdAndPaga(@Param("usuarioId") Long usuarioId, @Param("paga") Boolean paga);
}
