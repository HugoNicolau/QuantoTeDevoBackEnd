package com.example.demo.repository;

import com.example.demo.model.Grupo;
import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    
    // Buscar grupos criados por um usuário
    List<Grupo> findByCriadorAndAtivoTrue(Usuario criador);
    
    // Buscar grupos onde o usuário é membro (incluindo os que criou)
    @Query("SELECT DISTINCT g FROM Grupo g " +
           "LEFT JOIN g.membros m " +
           "WHERE (g.criador = :usuario OR m = :usuario) AND g.ativo = true")
    List<Grupo> findGruposByUsuario(@Param("usuario") Usuario usuario);
    
    // Buscar grupos ativos onde o usuário é membro
    @Query("SELECT g FROM Grupo g " +
           "JOIN g.membros m " +
           "WHERE m = :usuario AND g.ativo = true")
    List<Grupo> findGruposByMembro(@Param("usuario") Usuario usuario);
    
    // Verificar se usuário é membro de um grupo específico
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END " +
           "FROM Grupo g " +
           "LEFT JOIN g.membros m " +
           "WHERE g.id = :grupoId AND (g.criador = :usuario OR m = :usuario) AND g.ativo = true")
    boolean isUsuarioMembroDoGrupo(@Param("grupoId") Long grupoId, @Param("usuario") Usuario usuario);
    
    // Buscar grupos por nome (busca parcial)
    @Query("SELECT g FROM Grupo g WHERE LOWER(g.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND g.ativo = true")
    List<Grupo> findByNomeContainingIgnoreCase(@Param("nome") String nome);
    
    // Contar membros de um grupo
    @Query("SELECT SIZE(g.membros) FROM Grupo g WHERE g.id = :grupoId")
    int countMembrosDoGrupo(@Param("grupoId") Long grupoId);
    
    // Buscar grupos com menos de X membros
    @Query("SELECT g FROM Grupo g WHERE SIZE(g.membros) < :maxMembros AND g.ativo = true")
    List<Grupo> findGruposComMenosMembros(@Param("maxMembros") int maxMembros);
}
