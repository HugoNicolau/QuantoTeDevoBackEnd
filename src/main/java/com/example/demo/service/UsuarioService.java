package com.example.demo.service;

import com.example.demo.dto.UsuarioDTO;
import com.example.demo.exception.NegocioException;
import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
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
    
    private UsuarioDTO converterParaDTO(Usuario usuario) {
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .nome(usuario.getNome())
            .email(usuario.getEmail())
            .chavePix(usuario.getChavePix())
            .build();
    }
}
