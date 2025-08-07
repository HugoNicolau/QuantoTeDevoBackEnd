package com.example.demo.service;

import com.example.demo.dto.SaldoContatoDTO;
import com.example.demo.dto.SaldoUsuarioDTO;
import com.example.demo.dto.UsuarioDTO;
// import com.example.demo.dto.RegisterRequest;
// import com.example.demo.dto.LoginRequest;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Divisao;
import com.example.demo.model.Usuario;
import com.example.demo.repository.DivisaoRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final DivisaoRepository divisaoRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UsuarioDTO criarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new NegocioException("Email já cadastrado");
        }
        
        Usuario usuario = Usuario.builder()
            .nome(usuarioDTO.getNome())
            .email(usuarioDTO.getEmail())
            .chavePix(usuarioDTO.getChavePix())
            .build();
        
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioSalvo);
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
        return converterParaDTO(usuario);
    }
    
    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com email " + email + " não encontrado"));
        return converterParaDTO(usuario);
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public UsuarioDTO atualizarUsuario(Long id, UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
        
        // Verifica se o email mudou e se já existe outro usuário com esse email
        if (!usuario.getEmail().equals(usuarioDTO.getEmail()) && 
            usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new NegocioException("Email já cadastrado");
        }
        
        usuario.setNome(usuarioDTO.getNome());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setChavePix(usuarioDTO.getChavePix());
        
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return converterParaDTO(usuarioAtualizado);
    }
    
    @Transactional
    public void deletarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Usuário", id);
        }
        usuarioRepository.deleteById(id);
    }
    
    // Método auxiliar para encontrar usuário por ID (para uso interno)
    public Usuario encontrarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
    
    // RF03: Calcular saldos consolidados do usuário
    @Transactional(readOnly = true)
    public SaldoUsuarioDTO calcularSaldosUsuario(Long usuarioId) {
        Usuario usuario = encontrarUsuarioPorId(usuarioId);
        
        // Buscar todas as divisões relacionadas ao usuário
        List<Divisao> divisoesComoDividente = divisaoRepository.findByUsuarioIdAndPago(usuarioId, false);
        List<Divisao> divisoesComoCriador = divisaoRepository.findByContaCriadorIdAndPago(usuarioId, false);
        
        BigDecimal totalDevendo = calcularTotalDevendo(divisoesComoDividente);
        BigDecimal totalRecebendo = calcularTotalRecebendo(divisoesComoCriador, usuarioId);
        BigDecimal saldoLiquido = totalRecebendo.subtract(totalDevendo);
        
        List<SaldoContatoDTO> saldosContatos = calcularSaldosContatos(usuarioId);
        
        return new SaldoUsuarioDTO(
            usuarioId,
            usuario.getNome(),
            totalDevendo,
            totalRecebendo,
            saldoLiquido,
            saldosContatos
        );
    }
    
    // RF09: Calcular saldo entre dois usuários específicos
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoComUsuario(Long usuarioId, Long outroUsuarioId) {
        // Verifica se os usuários existem
        encontrarUsuarioPorId(usuarioId);
        encontrarUsuarioPorId(outroUsuarioId);
        
        // Quanto o outroUsuario deve para mim (divisões das minhas contas)
        List<Divisao> outroDeveParaMim = divisaoRepository
            .findByContaCriadorIdAndUsuarioIdAndPago(usuarioId, outroUsuarioId, false);
        BigDecimal totalOutroDeveParaMim = outroDeveParaMim.stream()
            .map(Divisao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // Quanto eu devo para o outroUsuario (divisões das contas dele)
        List<Divisao> euDevoParaOutro = divisaoRepository
            .findByContaCriadorIdAndUsuarioIdAndPago(outroUsuarioId, usuarioId, false);
        BigDecimal totalEuDevoParaOutro = euDevoParaOutro.stream()
            .map(Divisao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // Saldo líquido: positivo = ele me deve, negativo = eu devo para ele
        return totalOutroDeveParaMim.subtract(totalEuDevoParaOutro);
    }
    
    private BigDecimal calcularTotalDevendo(List<Divisao> divisoes) {
        return divisoes.stream()
            .map(Divisao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calcularTotalRecebendo(List<Divisao> divisoesComoCriador, Long usuarioId) {
        // Somar valores das divisões de contas que eu criei (onde outros me devem)
        return divisoesComoCriador.stream()
            .filter(d -> !d.getUsuario().getId().equals(usuarioId)) // Excluir minhas próprias divisões
            .map(Divisao::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private List<SaldoContatoDTO> calcularSaldosContatos(Long usuarioId) {
        Map<Long, SaldoContatoDTO> saldosMap = new HashMap<>();
        
        // Processar divisões onde eu devo para outros
        List<Divisao> divisoesComoDividente = divisaoRepository.findByUsuarioIdAndPago(usuarioId, false);
        for (Divisao divisao : divisoesComoDividente) {
            Long criadorId = divisao.getConta().getCriador().getId();
            if (!criadorId.equals(usuarioId)) {
                saldosMap.computeIfAbsent(criadorId, id -> {
                    Usuario criador = divisao.getConta().getCriador();
                    return new SaldoContatoDTO(id, criador.getNome(), criador.getEmail(), BigDecimal.ZERO, 0);
                });
                SaldoContatoDTO saldo = saldosMap.get(criadorId);
                saldo.setSaldoComEsteUsuario(saldo.getSaldoComEsteUsuario().subtract(divisao.getValor())); // Negativo = eu devo
                saldo.setQuantidadeContasAbertas(saldo.getQuantidadeContasAbertas() + 1);
            }
        }
        
        // Processar divisões onde outros me devem
        List<Divisao> divisoesComoCriador = divisaoRepository.findByContaCriadorIdAndPago(usuarioId, false);
        for (Divisao divisao : divisoesComoCriador) {
            Long dividenteId = divisao.getUsuario().getId();
            if (!dividenteId.equals(usuarioId)) {
                saldosMap.computeIfAbsent(dividenteId, id -> {
                    Usuario dividende = divisao.getUsuario();
                    return new SaldoContatoDTO(id, dividende.getNome(), dividende.getEmail(), BigDecimal.ZERO, 0);
                });
                SaldoContatoDTO saldo = saldosMap.get(dividenteId);
                saldo.setSaldoComEsteUsuario(saldo.getSaldoComEsteUsuario().add(divisao.getValor())); // Positivo = ele me deve
                saldo.setQuantidadeContasAbertas(saldo.getQuantidadeContasAbertas() + 1);
            }
        }
        
        return new ArrayList<>(saldosMap.values());
    }
    
    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .chavePix(usuario.getChavePix())
            .build();
    }
    
    // Métodos de autenticação serão implementados separadamente
}
