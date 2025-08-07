package com.example.demo.repository;

import com.example.demo.model.Amizade;
import com.example.demo.model.StatusAmizade;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmizadeRepository extends JpaRepository<Amizade, Long> {
    
    // Verificar se já existe amizade entre dois usuários (qualquer direção)
    @Query("SELECT a FROM Amizade a WHERE " +
           "(a.solicitante.id = :usuario1Id AND a.convidado.id = :usuario2Id) OR " +
           "(a.solicitante.id = :usuario2Id AND a.convidado.id = :usuario1Id)")
    Optional<Amizade> findAmizadeEntreUsuarios(@Param("usuario1Id") Long usuario1Id, 
                                               @Param("usuario2Id") Long usuario2Id);
    
    // Listar amizades aceitas de um usuário
    @Query("SELECT a FROM Amizade a WHERE " +
           "(a.solicitante.id = :usuarioId OR a.convidado.id = :usuarioId) " +
           "AND a.status = :status")
    List<Amizade> findAmizadesPorUsuarioEStatus(@Param("usuarioId") Long usuarioId, 
                                                @Param("status") StatusAmizade status);
    
    // Listar convites pendentes RECEBIDOS por um usuário
    List<Amizade> findByConvidadoAndStatus(Usuario convidado, StatusAmizade status);
    
    // Listar convites pendentes ENVIADOS por um usuário
    List<Amizade> findBySolicitanteAndStatus(Usuario solicitante, StatusAmizade status);
    
    // Verificar se são amigos (amizade aceita)
    @Query("SELECT COUNT(a) > 0 FROM Amizade a WHERE " +
           "((a.solicitante.id = :usuario1Id AND a.convidado.id = :usuario2Id) OR " +
           "(a.solicitante.id = :usuario2Id AND a.convidado.id = :usuario1Id)) " +
           "AND a.status = 'ACEITA'")
    boolean saoAmigos(@Param("usuario1Id") Long usuario1Id, @Param("usuario2Id") Long usuario2Id);
    
    // Listar amizades aceitas onde o usuário é solicitante
    @Query("SELECT a FROM Amizade a WHERE a.solicitante.id = :usuarioId AND a.status = 'ACEITA'")
    List<Amizade> findAmizadesComoPrimeiro(@Param("usuarioId") Long usuarioId);
    
    // Listar amizades aceitas onde o usuário é convidado
    @Query("SELECT a FROM Amizade a WHERE a.convidado.id = :usuarioId AND a.status = 'ACEITA'")
    List<Amizade> findAmizadesComoSegundo(@Param("usuarioId") Long usuarioId);
}
