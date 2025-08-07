package com.example.demo.service;

import com.example.demo.dto.DividaDTO;
import com.example.demo.model.Divida;
import com.example.demo.model.Usuario;
import com.example.demo.repository.DividaRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DividaService {
    
    private final DividaRepository dividaRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public DividaDTO criarDivida(DividaDTO dividaDTO) {
        Usuario usuarioDevedor = usuarioRepository.findById(dividaDTO.getUsuarioDevedorId())
            .orElseThrow(() -> new RuntimeException("Usuário devedor não encontrado"));
        
        Usuario usuarioCredor = usuarioRepository.findById(dividaDTO.getUsuarioCredorId())
            .orElseThrow(() -> new RuntimeException("Usuário credor não encontrado"));
        
        Divida divida = new Divida();
        divida.setDescricao(dividaDTO.getDescricao());
        divida.setValor(dividaDTO.getValor());
        divida.setUsuarioDevedor(usuarioDevedor);
        divida.setUsuarioCredor(usuarioCredor);
        divida.setDataVencimento(dividaDTO.getDataVencimento());
        divida.setDataCriacao(LocalDateTime.now());
        divida.setPaga(false);
        
        Divida dividaSalva = dividaRepository.save(divida);
        return converterParaDTO(dividaSalva);
    }
    
    @Transactional(readOnly = true)
    public DividaDTO buscarPorId(Long id) {
        Divida divida = dividaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Dívida não encontrada"));
        return converterParaDTO(divida);
    }
    
    @Transactional(readOnly = true)
    public List<DividaDTO> listarDividasDoUsuario(Long usuarioId, Boolean paga) {
        List<Divida> dividas;
        
        if (paga != null) {
            dividas = dividaRepository.findByUsuarioIdAndPaga(usuarioId, paga);
        } else {
            dividas = dividaRepository.findByUsuarioId(usuarioId);
        }
        
        return dividas.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DividaDTO> listarDividasDevidas(Long usuarioId, Boolean paga) {
        List<Divida> dividas;
        
        if (paga != null) {
            dividas = dividaRepository.findByUsuarioDevedorIdAndPaga(usuarioId, paga);
        } else {
            dividas = dividaRepository.findByUsuarioDevedorId(usuarioId);
        }
        
        return dividas.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DividaDTO> listarDividasAReceber(Long usuarioId, Boolean paga) {
        List<Divida> dividas;
        
        if (paga != null) {
            dividas = dividaRepository.findByUsuarioCredorIdAndPaga(usuarioId, paga);
        } else {
            dividas = dividaRepository.findByUsuarioCredorId(usuarioId);
        }
        
        return dividas.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public DividaDTO marcarComoPaga(Long dividaId, String formaPagamento) {
        Divida divida = dividaRepository.findById(dividaId)
            .orElseThrow(() -> new RuntimeException("Dívida não encontrada"));
        
        divida.setPaga(true);
        divida.setDataPagamento(LocalDateTime.now());
        divida.setFormaPagamento(formaPagamento);
        
        Divida dividaAtualizada = dividaRepository.save(divida);
        return converterParaDTO(dividaAtualizada);
    }
    
    @Transactional
    public void deletarDivida(Long id) {
        if (!dividaRepository.existsById(id)) {
            throw new RuntimeException("Dívida não encontrada");
        }
        dividaRepository.deleteById(id);
    }
    
    private DividaDTO converterParaDTO(Divida divida) {
        DividaDTO dto = new DividaDTO();
        dto.setId(divida.getId());
        dto.setDescricao(divida.getDescricao());
        dto.setValor(divida.getValor());
        dto.setUsuarioDevedorId(divida.getUsuarioDevedor().getId());
        dto.setNomeUsuarioDevedor(divida.getUsuarioDevedor().getNome());
        dto.setUsuarioCredorId(divida.getUsuarioCredor().getId());
        dto.setNomeUsuarioCredor(divida.getUsuarioCredor().getNome());
        dto.setDataCriacao(divida.getDataCriacao());
        dto.setDataVencimento(divida.getDataVencimento());
        dto.setPaga(divida.getPaga());
        dto.setDataPagamento(divida.getDataPagamento());
        dto.setFormaPagamento(divida.getFormaPagamento());
        return dto;
    }
}
