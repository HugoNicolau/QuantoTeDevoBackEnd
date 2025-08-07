package com.example.demo.controller;

import com.example.demo.dto.SaldoUsuarioDTO;
import com.example.demo.dto.UsuarioDTO;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Para permitir requisições do frontend
public class UsuarioController {
    
    private final UsuarioService usuarioService;
    
    @PostMapping
    public ResponseEntity<UsuarioDTO> criarUsuario(@Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioCriado = usuarioService.criarUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCriado);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarUsuario(@PathVariable Long id) {
        UsuarioDTO usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarUsuarioPorEmail(@PathVariable String email) {
        UsuarioDTO usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
    
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        List<UsuarioDTO> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizarUsuario(
            @PathVariable Long id, 
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDTO);
        return ResponseEntity.ok(usuarioAtualizado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
    
    // RF03: Endpoint para saldos consolidados do usuário
    @GetMapping("/{id}/saldos")
    public ResponseEntity<SaldoUsuarioDTO> obterSaldosUsuario(@PathVariable Long id) {
        SaldoUsuarioDTO saldos = usuarioService.calcularSaldosUsuario(id);
        return ResponseEntity.ok(saldos);
    }
    
    // RF09: Endpoint para saldo entre dois usuários específicos
    @GetMapping("/{id}/saldo-com/{outroUsuarioId}")
    public ResponseEntity<BigDecimal> obterSaldoComUsuario(
            @PathVariable Long id, 
            @PathVariable Long outroUsuarioId) {
        BigDecimal saldo = usuarioService.calcularSaldoComUsuario(id, outroUsuarioId);
        return ResponseEntity.ok(saldo);
    }
}
