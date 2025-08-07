package com.example.demo.service;

import com.example.demo.dto.ContaDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Conta;
import com.example.demo.model.StatusConta;
import com.example.demo.model.Usuario;
import com.example.demo.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContaService {
    
    private final ContaRepository contaRepository;
    private final UsuarioService usuarioService;
    
    @Transactional
    public ContaDTO criarConta(ContaDTO contaDTO) {
        Usuario criador = usuarioService.encontrarUsuarioPorId(contaDTO.getCriadorId());
        
        Conta conta = Conta.builder()
            .descricao(contaDTO.getDescricao())
            .valor(contaDTO.getValor())
            .vencimento(contaDTO.getVencimento())
            .paga(false)
            .status(StatusConta.PENDENTE)
            .criador(criador)
            .build();
        
        Conta contaSalva = contaRepository.save(conta);
        return converterParaDTO(contaSalva);
    }
    
    @Transactional(readOnly = true)
    public ContaDTO buscarPorId(Long id) {
        Conta conta = contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta", id));
        return converterParaDTO(conta);
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasDoUsuario(Long usuarioId) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        return contaRepository.findContasRelacionadasAoUsuario(usuario).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasPorStatus(boolean paga) {
        return contaRepository.findByPaga(paga).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasVencidas() {
        return contaRepository.findByVencimentoBefore(LocalDate.now()).stream()
            .filter(conta -> !conta.getPaga())
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasVencidasDoUsuario(Long usuarioId) {
        return contaRepository.findByVencimentoBefore(LocalDate.now()).stream()
            .filter(conta -> !conta.getPaga())
            .filter(conta -> conta.getCriador().getId().equals(usuarioId))
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    // RF05: Filtros por período
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasPorPeriodo(LocalDate inicio, LocalDate fim) {
        return contaRepository.findByVencimentoBetween(inicio, fim).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasPorStatusEPeriodo(boolean paga, LocalDate inicio, LocalDate fim) {
        return contaRepository.findByPagaAndVencimentoBetween(paga, inicio, fim).stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    // RF05: Método para filtrar contas específicas de um usuário
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasDoUsuarioComFiltros(Long usuarioId, Boolean paga, LocalDate inicio, LocalDate fim) {
        Usuario usuario = usuarioService.encontrarUsuarioPorId(usuarioId);
        
        // Buscar contas relacionadas ao usuário (criadas por ele ou onde ele participa)
        List<Conta> contasRelacionadas = contaRepository.findContasRelacionadasAoUsuario(usuario);
        
        return contasRelacionadas.stream()
            .filter(conta -> {
                // Filtrar por status se especificado
                if (paga != null && !conta.getPaga().equals(paga)) {
                    return false;
                }
                
                // Filtrar por período se especificado
                if (inicio != null && fim != null && conta.getVencimento() != null) {
                    return !conta.getVencimento().isBefore(inicio) && !conta.getVencimento().isAfter(fim);
                }
                
                return true;
            })
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public ContaDTO atualizarConta(Long id, ContaDTO contaDTO) {
        Conta conta = contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta", id));
        
        conta.setDescricao(contaDTO.getDescricao());
        conta.setValor(contaDTO.getValor());
        conta.setVencimento(contaDTO.getVencimento());
        
        if (contaDTO.getPaga() != null) {
            conta.setPaga(contaDTO.getPaga());
        }
        
        Conta contaAtualizada = contaRepository.save(conta);
        return converterParaDTO(contaAtualizada);
    }
    
    @Transactional
    public ContaDTO marcarComoPaga(Long id) {
        Conta conta = contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta", id));
        
        conta.setPaga(true);
        conta.setStatus(StatusConta.PAGA);
        Conta contaAtualizada = contaRepository.save(conta);
        return converterParaDTO(contaAtualizada);
    }
    
    @Transactional
    public ContaDTO marcarComoVencida(Long id) {
        Conta conta = contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta", id));
        
        // Só marca como vencida se não estiver paga
        if (!conta.getPaga()) {
            conta.setStatus(StatusConta.VENCIDA);
            Conta contaAtualizada = contaRepository.save(conta);
            return converterParaDTO(contaAtualizada);
        } else {
            throw new IllegalStateException("Não é possível marcar uma conta paga como vencida");
        }
    }
    
    @Transactional
    public void deletarConta(Long id) {
        if (!contaRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Conta", id);
        }
        contaRepository.deleteById(id);
    }
    
    // Método auxiliar para encontrar conta por ID (para uso interno)
    public Conta encontrarContaPorId(Long id) {
        return contaRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Conta", id));
    }
    
    @Transactional(readOnly = true)
    public List<ContaDTO> listarContasDoGrupo(Long grupoId, Boolean paga) {
        // Primeiro, vamos usar uma consulta simples sem injetar GrupoService
        // para evitar dependência circular
        return contaRepository.findAll().stream()
                .filter(conta -> conta.getGrupo() != null && conta.getGrupo().getId().equals(grupoId))
                .filter(conta -> paga == null || conta.getPaga().equals(paga))
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    private ContaDTO converterParaDTO(Conta conta) {
        UsuarioDTO criadorDTO = UsuarioDTO.builder()
            .id(conta.getCriador().getId())
            .nome(conta.getCriador().getNome())
            .email(conta.getCriador().getEmail())
            .chavePix(conta.getCriador().getChavePix())
            .build();
        
        return ContaDTO.builder()
            .id(conta.getId())
            .descricao(conta.getDescricao())
            .valor(conta.getValor())
            .vencimento(conta.getVencimento())
            .paga(conta.getPaga())
            .status(conta.getStatus())
            .dataCriacao(conta.getDataCriacao())
            .criadorId(conta.getCriador().getId())
            .criador(criadorDTO)
            .build();
    }
}
