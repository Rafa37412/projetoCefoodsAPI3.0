package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.UsuarioUpdateDTO;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // GET - Listar todos os usuários
    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // GET - Buscar usuário por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        return usuario.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Criar novo usuário
    @PostMapping
    public Usuario criarUsuario(@RequestBody Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // PUT - Atualizar usuário existente
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable("id") Long id, @RequestBody UsuarioUpdateDTO dto) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Atualizar apenas campos não nulos
            if (dto.getNome() != null)
                usuario.setNome(dto.getNome());
            if (dto.getLogin() != null)
                usuario.setLogin(dto.getLogin());
            if (dto.getEmail() != null)
                usuario.setEmail(dto.getEmail());

            // Aqui está o cuidado com a senha
            if (dto.getSenha() != null && !dto.getSenha().isEmpty()) {
                usuario.setSenha(dto.getSenha());
            }

            if (dto.getTelefone() != null)
                usuario.setTelefone(dto.getTelefone());
            if (dto.getCpf() != null)
                usuario.setCpf(dto.getCpf());
            if (dto.getData_nascimento() != null)
                usuario.setData_nascimento(dto.getData_nascimento());
            if (dto.getTipo_usuario() != null)
                usuario.setTipo_usuario(dto.getTipo_usuario());
            if (dto.getTipo_perfil() != null)
                usuario.setTipo_perfil(dto.getTipo_perfil());
            if (dto.getChave_pix() != null)
                usuario.setChave_pix(dto.getChave_pix());
            if (dto.getFoto_perfil() != null)
                usuario.setFoto_perfil(dto.getFoto_perfil());
            if (dto.getAtivo() != null)
                usuario.setAtivo(dto.getAtivo());
            if (dto.getEmail_verificado() != null)
                usuario.setEmail_verificado(dto.getEmail_verificado());
            if (dto.getToken_recuperacao() != null)
                usuario.setToken_recuperacao(dto.getToken_recuperacao());
            if (dto.getUltimo_acesso() != null)
                usuario.setUltimo_acesso(dto.getUltimo_acesso());
            if (dto.getPossui_loja() != null)
                usuario.setPossui_loja(dto.getPossui_loja());

            Usuario atualizado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(atualizado);
        }

        return ResponseEntity.notFound().build();
    }

    // DELETE - Excluir usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
