package com.example.demo.service;

import com.example.demo.dto.AmizadeDTO;
import com.example.demo.dto.SolicitarAmizadeDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Amizade;
import com.example.demo.model.StatusAmizade;
import com.example.demo.model.Usuario;
import com.example.demo.repository.AmizadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmizadeService {
    
    private final AmizadeRepository amizadeRepository;
    private final UsuarioService usuarioService;
    
    @Transactional
    public AmizadeDTO solicitarAmizade(Long solicitanteId, SolicitarAmizadeDTO solicitacao) {
        // Validações básicas
        if (solicitanteId.equals(solicitacao.getConvidadoId())) {
            throw new NegocioException("Não é possível enviar solicitação de amizade para si mesmo");
        }
        
        Usuario solicitante = usuarioService.encontrarUsuarioPorId(solicitanteId);
        Usuario convidado = usuarioService.encontrarUsuarioPorId(solicitacao.getConvidadoId());
        
        // Verificar se já existe amizade entre os usuários
        Optional<Amizade> amizadeExistente = amizadeRepository
            .findAmizadeEntreUsuarios(solicitanteId, solicitacao.getConvidadoId());
            
        if (amizadeExistente.isPresent()) {
            Amizade amizade = amizadeExistente.get();
            switch (amizade.getStatus()) {
                case ACEITA:
                    throw new NegocioException("Vocês já são amigos");
                case PENDENTE:
                    throw new NegocioException("Já existe uma solicitação pendente entre vocês");
                case BLOQUEADA:
                    throw new NegocioException("Não é possível enviar solicitação para este usuário");
                case REJEITADA:
                    // Permitir nova solicitação se foi rejeitada há mais de um tempo
                    // Por enquanto, permitir sempre
                    break;
            }
        }
        
        // Criar nova solicitação
        Amizade novaAmizade = Amizade.builder()
            .solicitante(solicitante)
            .convidado(convidado)
            .status(StatusAmizade.PENDENTE)
            .dataSolicitacao(LocalDateTime.now())
            .build();
            
        Amizade amizadeSalva = amizadeRepository.save(novaAmizade);
        return converterParaDTO(amizadeSalva, solicitanteId);
    }
    
    @Transactional(readOnly = true)
    public List<AmizadeDTO> listarConvitesPendentes(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        List<Amizade> convitesRecebidos = amizadeRepository
            .findByConvidadoAndStatus(usuario, StatusAmizade.PENDENTE);
            
        return convitesRecebidos.stream()
            .map(amizade -> converterParaDTO(amizade, usuarioId))
            .collect(Collectors.toList());
    }
    
    @Transactional
    public AmizadeDTO responderConvite(Long amizadeId, Long usuarioId, boolean aceitar) {
        Amizade amizade = amizadeRepository.findById(amizadeId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Amizade", amizadeId));
            
        // Verificar se o usuário é o convidado
        if (!amizade.getConvidado().getId().equals(usuarioId)) {
            throw new NegocioException("Apenas o usuário convidado pode responder ao convite");
        }
        
        if (amizade.getStatus() != StatusAmizade.PENDENTE) {
            throw new NegocioException("Este convite já foi respondido");
        }
        
        // Atualizar status
        amizade.setStatus(aceitar ? StatusAmizade.ACEITA : StatusAmizade.REJEITADA);
        amizade.setDataResposta(LocalDateTime.now());
        
        Amizade amizadeAtualizada = amizadeRepository.save(amizade);
        return converterParaDTO(amizadeAtualizada, usuarioId);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarAmigos(Long usuarioId) {
        usuarioService.encontrarUsuarioPorId(usuarioId); // Validar se usuário existe
        
        // Buscar amizades onde o usuário é solicitante
        List<Amizade> amizadesComoPrimeiro = amizadeRepository.findAmizadesComoPrimeiro(usuarioId);
        // Buscar amizades onde o usuário é convidado  
        List<Amizade> amizadesComoSegundo = amizadeRepository.findAmizadesComoSegundo(usuarioId);
        
        List<Usuario> amigos = new ArrayList<>();
        
        // Adicionar convidados das amizades onde o usuário é solicitante
        amizadesComoPrimeiro.forEach(a -> amigos.add(a.getConvidado()));
        
        // Adicionar solicitantes das amizades onde o usuário é convidado
        amizadesComoSegundo.forEach(a -> amigos.add(a.getSolicitante()));
        
        return amigos.stream()
            .map(this::converterUsuarioParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public boolean saoAmigos(Long usuario1Id, Long usuario2Id) {
        return amizadeRepository.saoAmigos(usuario1Id, usuario2Id);
    }
    
    @Transactional
    public void removerAmizade(Long usuario1Id, Long usuario2Id) {
        Optional<Amizade> amizade = amizadeRepository
            .findAmizadeEntreUsuarios(usuario1Id, usuario2Id);
            
        if (amizade.isPresent()) {
            amizadeRepository.delete(amizade.get());
        } else {
            throw new RecursoNaoEncontradoException("Amizade não encontrada entre os usuários");
        }
    }
    
    @Transactional
    public void bloquearUsuario(Long bloqueadorId, Long bloqueadoId) {
        if (bloqueadorId.equals(bloqueadoId)) {
            throw new NegocioException("Não é possível bloquear a si mesmo");
        }
        
        Usuario bloqueador = usuarioService.encontrarUsuarioPorId(bloqueadorId);
        Usuario bloqueado = usuarioService.encontrarUsuarioPorId(bloqueadoId);
        
        Optional<Amizade> amizadeExistente = amizadeRepository
            .findAmizadeEntreUsuarios(bloqueadorId, bloqueadoId);
            
        if (amizadeExistente.isPresent()) {
            // Atualizar amizade existente para bloqueada
            Amizade amizade = amizadeExistente.get();
            amizade.setStatus(StatusAmizade.BLOQUEADA);
            amizade.setDataResposta(LocalDateTime.now());
            amizadeRepository.save(amizade);
        } else {
            // Criar novo registro de bloqueio
            Amizade bloqueio = Amizade.builder()
                .solicitante(bloqueador)
                .convidado(bloqueado)
                .status(StatusAmizade.BLOQUEADA)
                .dataSolicitacao(LocalDateTime.now())
                .dataResposta(LocalDateTime.now())
                .build();
            amizadeRepository.save(bloqueio);
        }
    }
    
    private AmizadeDTO converterParaDTO(Amizade amizade, Long usuarioPerspectiva) {
        boolean isSolicitacao = amizade.getSolicitante().getId().equals(usuarioPerspectiva);
        
        return AmizadeDTO.builder()
            .id(amizade.getId())
            .solicitanteId(amizade.getSolicitante().getId())
            .solicitante(converterUsuarioParaDTO(amizade.getSolicitante()))
            .convidadoId(amizade.getConvidado().getId())
            .convidado(converterUsuarioParaDTO(amizade.getConvidado()))
            .status(amizade.getStatus().name())
            .dataSolicitacao(amizade.getDataSolicitacao())
            .dataResposta(amizade.getDataResposta())
            .isSolicitacao(isSolicitacao)
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
