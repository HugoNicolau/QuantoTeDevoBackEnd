package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Compra;
import com.example.demo.model.ItemCompra;
import com.example.demo.model.Usuario;
import com.example.demo.repository.CompraRepository;
import com.example.demo.repository.ItemCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {
    
    private final CompraRepository compraRepository;
    private final ItemCompraRepository itemCompraRepository;
    private final UsuarioService usuarioService;
    private final DividaService dividaService;
    
    @Transactional
    public CompraDTO criarCompra(CriarCompraDTO criarCompraDTO) {
        Usuario usuarioCriador = usuarioService.encontrarUsuarioPorId(criarCompraDTO.getUsuarioCriadorId());
        
        // Validar se todos os usuários responsáveis existem
        for (CriarCompraDTO.CriarItemCompraDTO item : criarCompraDTO.getItens()) {
            usuarioService.encontrarUsuarioPorId(item.getUsuarioResponsavelId());
        }
        
        // Criar a compra
        LocalDate dataCompra = criarCompraDTO.getDataCompra() != null ? 
                               criarCompraDTO.getDataCompra() : LocalDate.now();
        
        Compra compra = Compra.builder()
            .descricao(criarCompraDTO.getDescricao())
            .dataCompra(dataCompra)
            .usuarioCriador(usuarioCriador)
            .observacoes(criarCompraDTO.getObservacoes())
            .finalizada(false)
            .build();
        
        Compra compraSalva = compraRepository.save(compra);
        
        // Criar os itens
        List<ItemCompra> itens = criarCompraDTO.getItens().stream()
            .map(itemDTO -> {
                Usuario usuarioResponsavel = usuarioService.encontrarUsuarioPorId(itemDTO.getUsuarioResponsavelId());
                
                return ItemCompra.builder()
                    .descricao(itemDTO.getDescricao())
                    .valor(itemDTO.getValor())
                    .quantidade(itemDTO.getQuantidade())
                    .compra(compraSalva)
                    .usuarioResponsavel(usuarioResponsavel)
                    .observacoes(itemDTO.getObservacoes())
                    .build();
            })
            .collect(Collectors.toList());
        
        itemCompraRepository.saveAll(itens);
        
        // Recarregar a compra com os itens
        compraSalva.setItens(itens);
        
        return converterParaDTO(compraSalva);
    }
    
    @Transactional(readOnly = true)
    public CompraDTO buscarPorId(Long id) {
        Compra compra = compraRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Compra não encontrada com id: " + id));
        return converterParaDTO(compra);
    }
    
    @Transactional(readOnly = true)
    public List<CompraDTO> listarComprasDoUsuario(Long usuarioId, Boolean finalizada) {
        List<Compra> compras;
        
        if (finalizada != null) {
            compras = compraRepository.findByUsuarioCriadorIdAndFinalizada(usuarioId, finalizada);
        } else {
            compras = compraRepository.findByUsuarioCriadorId(usuarioId);
        }
        
        return compras.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CompraDTO> listarComprasComItensDoUsuario(Long usuarioId, Boolean ativas) {
        List<Compra> compras;
        
        if (ativas != null && ativas) {
            compras = compraRepository.findComprasAtivasComItensDoUsuario(usuarioId);
        } else {
            compras = compraRepository.findComprasComItensDoUsuario(usuarioId);
        }
        
        return compras.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public CompraDTO finalizarCompra(Long id) {
        Compra compra = compraRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Compra", id));
        
        if (compra.getFinalizada()) {
            throw new NegocioException("Esta compra já foi finalizada");
        }
        
        // Gerar dívidas automaticamente baseadas nos itens
        gerarDividasAutomaticamente(compra);
        
        compra.setFinalizada(true);
        Compra compraAtualizada = compraRepository.save(compra);
        
        return converterParaDTO(compraAtualizada);
    }
    
    @Transactional
    public void deletarCompra(Long id) {
        Compra compra = compraRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Compra", id));
        
        if (compra.getFinalizada()) {
            throw new NegocioException("Não é possível deletar uma compra finalizada");
        }
        
        compraRepository.delete(compra);
    }
    
    @Transactional
    public CompraDTO adicionarItem(Long compraId, ItemCompraDTO itemDTO) {
        Compra compra = compraRepository.findById(compraId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Compra", compraId));
        
        if (compra.getFinalizada()) {
            throw new NegocioException("Não é possível adicionar itens a uma compra finalizada");
        }
        
        Usuario usuarioResponsavel = usuarioService.encontrarUsuarioPorId(itemDTO.getUsuarioResponsavelId());
        
        ItemCompra item = ItemCompra.builder()
            .descricao(itemDTO.getDescricao())
            .valor(itemDTO.getValor())
            .quantidade(itemDTO.getQuantidade())
            .compra(compra)
            .usuarioResponsavel(usuarioResponsavel)
            .observacoes(itemDTO.getObservacoes())
            .build();
        
        itemCompraRepository.save(item);
        
        return converterParaDTO(compra);
    }
    
    private void gerarDividasAutomaticamente(Compra compra) {
        // Agrupar itens por usuário responsável
        var itensPorUsuario = compra.getItens().stream()
            .collect(Collectors.groupingBy(ItemCompra::getUsuarioResponsavel));
        
        // Para cada usuário que não seja o criador da compra, gerar uma dívida
        for (var entrada : itensPorUsuario.entrySet()) {
            Usuario usuarioResponsavel = entrada.getKey();
            List<ItemCompra> itensDoUsuario = entrada.getValue();
            
            // Se não for o próprio criador da compra
            if (!usuarioResponsavel.getId().equals(compra.getUsuarioCriador().getId())) {
                BigDecimal valorTotal = itensDoUsuario.stream()
                    .map(ItemCompra::getValorTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Criar dívida
                DividaDTO dividaDTO = new DividaDTO();
                dividaDTO.setDescricao("Dívida da compra: " + compra.getDescricao());
                dividaDTO.setValor(valorTotal);
                dividaDTO.setUsuarioDevedorId(usuarioResponsavel.getId());
                dividaDTO.setUsuarioCredorId(compra.getUsuarioCriador().getId());
                dividaDTO.setDataVencimento(compra.getDataCompra().plusDays(7)); // Vence em 7 dias
                
                dividaService.criarDivida(dividaDTO);
            }
        }
    }
    
    private CompraDTO converterParaDTO(Compra compra) {
        CompraDTO dto = CompraDTO.builder()
            .id(compra.getId())
            .descricao(compra.getDescricao())
            .dataCompra(compra.getDataCompra())
            .dataCriacao(compra.getDataCriacao())
            .usuarioCriadorId(compra.getUsuarioCriador().getId())
            .finalizada(compra.getFinalizada())
            .observacoes(compra.getObservacoes())
            .build();
        
        // Converter usuário criador
        dto.setUsuarioCriador(UsuarioDTO.builder()
            .id(compra.getUsuarioCriador().getId())
            .nome(compra.getUsuarioCriador().getNome())
            .email(compra.getUsuarioCriador().getEmail())
            .chavePix(compra.getUsuarioCriador().getChavePix())
            .build());
        
        // Converter itens
        if (compra.getItens() != null) {
            List<ItemCompraDTO> itensDTO = compra.getItens().stream()
                .map(this::converterItemParaDTO)
                .collect(Collectors.toList());
            dto.setItens(itensDTO);
            
            // Calcular campos agregados
            dto.setTotalItens(itensDTO.size());
            dto.setValorTotal(itensDTO.stream()
                .map(ItemCompraDTO::getValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        
        return dto;
    }
    
    private ItemCompraDTO converterItemParaDTO(ItemCompra item) {
        ItemCompraDTO dto = ItemCompraDTO.builder()
            .id(item.getId())
            .descricao(item.getDescricao())
            .valor(item.getValor())
            .quantidade(item.getQuantidade())
            .usuarioResponsavelId(item.getUsuarioResponsavel().getId())
            .observacoes(item.getObservacoes())
            .valorTotal(item.getValorTotal())
            .build();
        
        // Converter usuário responsável
        dto.setUsuarioResponsavel(UsuarioDTO.builder()
            .id(item.getUsuarioResponsavel().getId())
            .nome(item.getUsuarioResponsavel().getNome())
            .email(item.getUsuarioResponsavel().getEmail())
            .chavePix(item.getUsuarioResponsavel().getChavePix())
            .build());
        
        return dto;
    }
}
