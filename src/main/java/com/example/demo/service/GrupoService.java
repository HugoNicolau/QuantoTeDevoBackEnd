package com.example.demo.service;

import com.example.demo.dto.CriarGrupoDTO;
import com.example.demo.dto.GerenciarMembrosDTO;
import com.example.demo.dto.GrupoDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Grupo;
import com.example.demo.model.Usuario;
import com.example.demo.repository.GrupoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrupoService {
    
    private final GrupoRepository grupoRepository;
    private final UsuarioService usuarioService;
    private final AmizadeService amizadeService;
    
    @Transactional
    public GrupoDTO criarGrupo(Long criadorId, CriarGrupoDTO criarGrupoDTO) {
        Usuario criador = usuarioService.encontrarUsuarioPorId(criadorId);
        
        // Validar membros iniciais (devem ser amigos do criador)
        List<Usuario> membrosIniciais = new ArrayList<>();
        if (criarGrupoDTO.getMembrosIniciais() != null && !criarGrupoDTO.getMembrosIniciais().isEmpty()) {
            for (Long membroId : criarGrupoDTO.getMembrosIniciais()) {
                if (membroId.equals(criadorId)) {
                    continue; // Pular o próprio criador
                }
                
                Usuario membro = usuarioService.encontrarUsuarioPorId(membroId);
                
                // Verificar se são amigos
                if (!amizadeService.saoAmigos(criadorId, membroId)) {
                    throw new NegocioException("Só é possível adicionar amigos ao grupo. " + 
                        membro.getNome() + " não é seu amigo.");
                }
                
                membrosIniciais.add(membro);
            }
        }
        
        // Criar o grupo
        Grupo grupo = Grupo.builder()
                .nome(criarGrupoDTO.getNome())
                .descricao(criarGrupoDTO.getDescricao())
                .criador(criador)
                .membros(membrosIniciais)
                .dataCriacao(LocalDateTime.now())
                .ativo(true)
                .build();
        
        Grupo grupoSalvo = grupoRepository.save(grupo);
        return converterParaDTO(grupoSalvo, criadorId);
    }
    
    @Transactional(readOnly = true)
    public List<GrupoDTO> listarGruposDoUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Grupo> grupos = grupoRepository.findGruposByUsuario(usuario);
        
        return grupos.stream()
                .map(grupo -> converterParaDTO(grupo, usuarioId))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public GrupoDTO buscarGrupoPorId(Long grupoId, Long usuarioId) {
        Grupo grupo = encontrarGrupoPorId(grupoId);
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        // Verificar se usuário tem acesso ao grupo
        if (!grupo.isMembro(usuario) && !grupo.isCriador(usuario)) {
            throw new NegocioException("Você não tem acesso a este grupo");
        }
        
        return converterParaDTO(grupo, usuarioId);
    }
    
    @Transactional
    public GrupoDTO gerenciarMembros(Long grupoId, Long usuarioId, GerenciarMembrosDTO gerenciarDTO) {
        Grupo grupo = encontrarGrupoPorId(grupoId);
        Usuario usuarioLogado = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        // Verificar se usuário pode modificar (apenas criador por enquanto)
        if (!grupo.podeModificar(usuarioLogado)) {
            throw new NegocioException("Apenas o criador do grupo pode gerenciar membros");
        }
        
        switch (gerenciarDTO.getAcao()) {
            case ADICIONAR:
                adicionarMembros(grupo, usuarioId, gerenciarDTO.getUsuarioIds());
                break;
            case REMOVER:
                removerMembros(grupo, gerenciarDTO.getUsuarioIds());
                break;
        }
        
        Grupo grupoAtualizado = grupoRepository.save(grupo);
        return converterParaDTO(grupoAtualizado, usuarioId);
    }
    
    private void adicionarMembros(Grupo grupo, Long criadorId, List<Long> novosMembroIds) {
        for (Long membroId : novosMembroIds) {
            if (membroId.equals(criadorId)) {
                continue; // Criador já faz parte implicitamente
            }
            
            Usuario novoMembro = usuarioService.encontrarUsuarioPorId(membroId);
            
            // Verificar se já é membro
            if (grupo.isMembro(novoMembro)) {
                continue; // Já é membro, pular
            }
            
            // Verificar se são amigos
            if (!amizadeService.saoAmigos(criadorId, membroId)) {
                throw new NegocioException("Só é possível adicionar amigos ao grupo. " + 
                    novoMembro.getNome() + " não é seu amigo.");
            }
            
            grupo.getMembros().add(novoMembro);
        }
    }
    
    private void removerMembros(Grupo grupo, List<Long> removerMembroIds) {
        List<Usuario> membrosParaRemover = grupo.getMembros().stream()
                .filter(membro -> removerMembroIds.contains(membro.getId()))
                .collect(Collectors.toList());
        
        grupo.getMembros().removeAll(membrosParaRemover);
    }
    
    @Transactional
    public void desativarGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = encontrarGrupoPorId(grupoId);
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        if (!grupo.podeModificar(usuario)) {
            throw new NegocioException("Apenas o criador do grupo pode desativá-lo");
        }
        
        grupo.setAtivo(false);
        grupoRepository.save(grupo);
    }
    
    @Transactional
    public void sairDoGrupo(Long grupoId, Long usuarioId) {
        Grupo grupo = encontrarGrupoPorId(grupoId);
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        if (grupo.isCriador(usuario)) {
            throw new NegocioException("O criador do grupo não pode sair. Desative o grupo ou transfira a liderança.");
        }
        
        if (!grupo.isMembro(usuario)) {
            throw new NegocioException("Você não é membro deste grupo");
        }
        
        grupo.getMembros().remove(usuario);
        grupoRepository.save(grupo);
    }
    
    private Grupo encontrarGrupoPorId(Long grupoId) {
        return grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Grupo", grupoId));
    }
    
    private GrupoDTO converterParaDTO(Grupo grupo, Long usuarioIdPerspectiva) {
        List<UsuarioDTO> membrosDTO = grupo.getMembros() != null ? 
            grupo.getMembros().stream()
                .map(this::converterUsuarioParaDTO)
                .collect(Collectors.toList()) : new ArrayList<>();
        
        Usuario usuarioPerspectiva = usuarioService.encontrarUsuarioPorId(usuarioIdPerspectiva);
        
        return GrupoDTO.builder()
                .id(grupo.getId())
                .nome(grupo.getNome())
                .descricao(grupo.getDescricao())
                .criador(converterUsuarioParaDTO(grupo.getCriador()))
                .membros(membrosDTO)
                .dataCriacao(grupo.getDataCriacao())
                .ativo(grupo.getAtivo())
                .totalMembros(membrosDTO.size())
                .totalContas(grupo.getContas() != null ? grupo.getContas().size() : 0)
                .usuarioECriador(grupo.isCriador(usuarioPerspectiva))
                .usuarioEMembro(grupo.isMembro(usuarioPerspectiva))
                .build();
    }
    
    private UsuarioDTO converterUsuarioParaDTO(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .chavePix(usuario.getChavePix())
                .build();
    }
}
