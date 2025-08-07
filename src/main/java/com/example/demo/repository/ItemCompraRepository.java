package com.example.demo.repository;

import com.example.demo.model.Compra;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {
    
    List<ItemCompra> findByCompra(Compra compra);
    
    List<ItemCompra> findByCompraId(Long compraId);
    
    List<ItemCompra> findByUsuarioResponsavel(Usuario usuarioResponsavel);
    
    List<ItemCompra> findByUsuarioResponsavelId(Long usuarioResponsavelId);
    
    // Calcular valor total dos itens de uma compra
    @Query("SELECT SUM(i.valor * i.quantidade) FROM ItemCompra i WHERE i.compra.id = :compraId")
    BigDecimal calcularValorTotalDaCompra(@Param("compraId") Long compraId);
    
    // Calcular valor total que um usuário deve pagar em uma compra
    @Query("SELECT SUM(i.valor * i.quantidade) FROM ItemCompra i WHERE i.compra.id = :compraId AND i.usuarioResponsavel.id = :usuarioId")
    BigDecimal calcularValorUsuarioNaCompra(@Param("compraId") Long compraId, @Param("usuarioId") Long usuarioId);
    
    // Calcular valor total que um usuário deve pagar em compras não finalizadas
    @Query("SELECT SUM(i.valor * i.quantidade) FROM ItemCompra i WHERE i.usuarioResponsavel.id = :usuarioId AND i.compra.finalizada = false")
    BigDecimal calcularValorTotalDevidoPeloUsuario(@Param("usuarioId") Long usuarioId);
    
    // Contar itens de uma compra
    @Query("SELECT COUNT(i) FROM ItemCompra i WHERE i.compra.id = :compraId")
    Long contarItensDaCompra(@Param("compraId") Long compraId);
}
