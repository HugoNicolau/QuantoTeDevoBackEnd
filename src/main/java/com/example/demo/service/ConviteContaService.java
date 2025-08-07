package com.example.demo.service;

import com.example.demo.dto.AceitarConviteDTO;
import com.example.demo.dto.ConviteContaDTO;
import com.example.demo.dto.CriarConviteDTO;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.*;
import com.example.demo.repository.ConviteContaRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConviteContaService {
    
    private final ConviteContaRepository conviteRepository;
    private final ContaService contaService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final DivisaoService divisaoService;
    
    @Transactional
    public ConviteContaDTO criarConvite(Long contaId, Long usuarioConvidanteId, CriarConviteDTO criarConviteDTO) {
        // Validar se a conta existe e se o usuário tem permissão
        Conta conta = contaService.encontrarContaPorId(contaId);
        Usuario usuarioConvidante = usuarioService.encontrarUsuarioPorId(usuarioConvidanteId);
        
        // Verificar se o usuário tem permissão para convidar (criador da conta)
        if (!conta.getCriador().getId().equals(usuarioConvidanteId)) {
            throw new IllegalStateException("Apenas o criador da conta pode enviar convites");
        }
        
        // Verificar se já existe convite pendente para este email e conta
        if (conviteRepository.existsByEmailConvidadoAndContaIdAndStatus(
                criarConviteDTO.getEmailConvidado(), contaId, StatusConvite.PENDENTE)) {
            throw new IllegalStateException("Já existe um convite pendente para este email nesta conta");
        }
        
        // Verificar se o email não é do próprio usuário convidante
        if (criarConviteDTO.getEmailConvidado().equals(usuarioConvidante.getEmail())) {
            throw new IllegalStateException("Não é possível convidar a si mesmo");
        }
        
        // Gerar token único
        String token = UUID.randomUUID().toString();
        
        // Definir data de expiração (padrão 7 dias)
        int diasValidade = criarConviteDTO.getDiasValidadeConvite() != null ? 
            criarConviteDTO.getDiasValidadeConvite() : 7;
        LocalDateTime dataExpiracao = LocalDateTime.now().plusDays(diasValidade);
        
        // Criar convite
        ConviteConta convite = ConviteConta.builder()
            .token(token)
            .emailConvidado(criarConviteDTO.getEmailConvidado())
            .nomeConvidado(criarConviteDTO.getNomeConvidado())
            .valorSugerido(criarConviteDTO.getValorSugerido())
            .mensagem(criarConviteDTO.getMensagem())
            .conta(conta)
            .usuarioConvidante(usuarioConvidante)
            .status(StatusConvite.PENDENTE)
            .dataExpiracao(dataExpiracao)
            .build();
        
        ConviteConta conviteSalvo = conviteRepository.save(convite);
        return converterParaDTO(conviteSalvo);
    }
    
    @Transactional(readOnly = true)
    public ConviteContaDTO buscarConvitePorToken(String token) {
        ConviteConta convite = conviteRepository.findByToken(token)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Convite com token " + token + " não encontrado"));
        
        return converterParaDTO(convite);
    }
    
    @Transactional
    public ConviteContaDTO aceitarConvite(String token, AceitarConviteDTO aceitarConviteDTO) {
        ConviteConta convite = conviteRepository.findByToken(token)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Convite com token " + token + " não encontrado"));
        
        // Validar se convite está válido
        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new IllegalStateException("Convite não está mais disponível");
        }
        
        if (convite.getDataExpiracao().isBefore(LocalDateTime.now())) {
            convite.setStatus(StatusConvite.EXPIRADO);
            conviteRepository.save(convite);
            throw new IllegalStateException("Convite expirado");
        }
        
        // Verificar se usuário já existe pelo email
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(convite.getEmailConvidado());
        Usuario usuarioConvidado;
        
        if (usuarioExistente.isPresent()) {
            usuarioConvidado = usuarioExistente.get();
        } else {
            // Criar novo usuário se não existe
            if (aceitarConviteDTO.getNome() == null || aceitarConviteDTO.getNome().trim().isEmpty()) {
                throw new IllegalArgumentException("Nome é obrigatório para novos usuários");
            }
            
            usuarioConvidado = Usuario.builder()
                .nome(aceitarConviteDTO.getNome())
                .email(convite.getEmailConvidado())
                .chavePix(aceitarConviteDTO.getChavePix())
                .build();
            
            usuarioConvidado = usuarioRepository.save(usuarioConvidado);
        }
        
        // Atualizar convite
        convite.setUsuarioConvidado(usuarioConvidado);
        convite.setStatus(StatusConvite.ACEITO);
        convite.setDataAceite(LocalDateTime.now());
        
        // Se aceitar divisão e há valor sugerido, criar divisão automaticamente
        if (aceitarConviteDTO.isAceitarDivisao() && convite.getValorSugerido() != null) {
            Divisao divisao = Divisao.builder()
                .conta(convite.getConta())
                .usuario(usuarioConvidado)
                .valor(convite.getValorSugerido())
                .pago(false)
                .build();
            
            divisaoService.salvarDivisao(divisao);
        }
        
        ConviteConta conviteAtualizado = conviteRepository.save(convite);
        return converterParaDTO(conviteAtualizado);
    }
    
    @Transactional
    public void rejeitarConvite(String token) {
        ConviteConta convite = conviteRepository.findByToken(token)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Convite com token " + token + " não encontrado"));
        
        if (convite.getStatus() != StatusConvite.PENDENTE) {
            throw new IllegalStateException("Convite não pode mais ser rejeitado");
        }
        
        convite.setStatus(StatusConvite.REJEITADO);
        convite.setDataRejeicao(LocalDateTime.now());
        conviteRepository.save(convite);
    }
    
    @Transactional(readOnly = true)
    public List<ConviteContaDTO> listarConvitesDaConta(Long contaId) {
        List<ConviteConta> convites = conviteRepository.findByContaId(contaId);
        return convites.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConviteContaDTO> listarConvitesEnviados(Long usuarioId) {
        List<ConviteConta> convites = conviteRepository.findByUsuarioConvidanteId(usuarioId);
        return convites.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ConviteContaDTO> listarConvitesPendentesParaEmail(String email) {
        List<ConviteConta> convites = conviteRepository.findByEmailConvidadoAndStatus(email, StatusConvite.PENDENTE);
        return convites.stream()
            .filter(convite -> convite.getDataExpiracao().isAfter(LocalDateTime.now())) // apenas não expirados
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void marcarConvitesExpirados() {
        List<ConviteConta> convitesExpirados = conviteRepository
            .findByStatusAndDataExpiracaoBefore(StatusConvite.PENDENTE, LocalDateTime.now());
        
        convitesExpirados.forEach(convite -> {
            convite.setStatus(StatusConvite.EXPIRADO);
        });
        
        conviteRepository.saveAll(convitesExpirados);
    }
    
    private ConviteContaDTO converterParaDTO(ConviteConta convite) {
        long diasParaVencer = 0;
        boolean expirado = false;
        
        if (convite.getDataExpiracao() != null) {
            diasParaVencer = ChronoUnit.DAYS.between(LocalDateTime.now(), convite.getDataExpiracao());
            expirado = convite.getDataExpiracao().isBefore(LocalDateTime.now());
        }
        
        return ConviteContaDTO.builder()
            .id(convite.getId())
            .token(convite.getToken())
            .emailConvidado(convite.getEmailConvidado())
            .nomeConvidado(convite.getNomeConvidado())
            .valorSugerido(convite.getValorSugerido())
            .mensagem(convite.getMensagem())
            .status(convite.getStatus())
            .dataConvite(convite.getDataConvite())
            .dataExpiracao(convite.getDataExpiracao())
            .dataAceite(convite.getDataAceite())
            .dataRejeicao(convite.getDataRejeicao())
            .contaId(convite.getConta().getId())
            .contaDescricao(convite.getConta().getDescricao())
            .contaValor(convite.getConta().getValor())
            .contaDataCriacao(convite.getConta().getDataCriacao())
            .usuarioConvidanteId(convite.getUsuarioConvidante().getId())
            .usuarioConvidanteNome(convite.getUsuarioConvidante().getNome())
            .usuarioConvidanteEmail(convite.getUsuarioConvidante().getEmail())
            .usuarioConvidanteChavePix(convite.getUsuarioConvidante().getChavePix())
            .usuarioConvidadoId(convite.getUsuarioConvidado() != null ? convite.getUsuarioConvidado().getId() : null)
            .usuarioConvidadoNome(convite.getUsuarioConvidado() != null ? convite.getUsuarioConvidado().getNome() : null)
            .expirado(expirado)
            .diasParaVencer(diasParaVencer)
            .build();
    }
}
