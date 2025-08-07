package com.example.demo.controller;

import com.example.demo.dto.CompraDTO;
import com.example.demo.dto.CriarCompraDTO;
import com.example.demo.dto.ItemCompraDTO;
import com.example.demo.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class CompraController {
    
    private final CompraService compraService;
    
    @PostMapping
    public ResponseEntity<CompraDTO> criarCompra(@Valid @RequestBody CriarCompraDTO criarCompraDTO) {
        CompraDTO compraCriada = compraService.criarCompra(criarCompraDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCriada);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> buscarCompra(@PathVariable Long id) {
        CompraDTO compra = compraService.buscarPorId(id);
        return ResponseEntity.ok(compra);
    }
    
    @GetMapping("/usuario/{usuarioId}/criadas")
    public ResponseEntity<List<CompraDTO>> listarComprasDoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean finalizada) {
        List<CompraDTO> compras = compraService.listarComprasDoUsuario(usuarioId, finalizada);
        return ResponseEntity.ok(compras);
    }
    
    @GetMapping("/usuario/{usuarioId}/participando")
    public ResponseEntity<List<CompraDTO>> listarComprasComItensDoUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) Boolean ativas) {
        List<CompraDTO> compras = compraService.listarComprasComItensDoUsuario(usuarioId, ativas);
        return ResponseEntity.ok(compras);
    }
    
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<CompraDTO> finalizarCompra(@PathVariable Long id) {
        CompraDTO compraFinalizada = compraService.finalizarCompra(id);
        return ResponseEntity.ok(compraFinalizada);
    }
    
    @PostMapping("/{id}/itens")
    public ResponseEntity<CompraDTO> adicionarItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemCompraDTO itemDTO) {
        CompraDTO compraAtualizada = compraService.adicionarItem(id, itemDTO);
        return ResponseEntity.ok(compraAtualizada);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCompra(@PathVariable Long id) {
        compraService.deletarCompra(id);
        return ResponseEntity.noContent().build();
    }
}
