package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.UsuarioDTO.CreateUsuario;
import com.projetocefoods.cefoods.dto.UsuarioUpdateDTO;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import com.projetocefoods.cefoods.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
// Suporta ambos os caminhos: /usuarios (original) e /users (frontend inglês)
@RequestMapping({"/usuarios","/users"})
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired(required = false)
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder; // fallback se configurado

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

    // POST - Criar novo usuário com DTO e service (validação + hash de senha + email)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> criarUsuario(@RequestBody @Validated CreateUsuario dto) {
        try {
            Usuario salvo = usuarioService.cadastrarNovoUsuario(montarUsuario(dto, dto.foto_perfil()));
            // senha já write-only; retornando salvo sem expor senha
            return ResponseEntity.ok(sanitizar(salvo));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> criarUsuarioComFoto(
            @RequestPart("dados") @Validated CreateUsuario dto,
            @RequestPart(value = "foto_perfil", required = false) MultipartFile fotoPerfil) {
        try {
            String fotoBase64 = null;
            if (fotoPerfil != null && !fotoPerfil.isEmpty()) {
                fotoBase64 = Base64.getEncoder().encodeToString(fotoPerfil.getBytes());
            }
            Usuario salvo = usuarioService.cadastrarNovoUsuario(montarUsuario(dto, fotoBase64));
            return ResponseEntity.ok(sanitizar(salvo));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erro ao processar imagem de perfil");
        }
    }

    // PUT - Atualizar usuário existente
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody UsuarioUpdateDTO dto) {
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
                if (passwordEncoder != null) {
                    usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
                } else {
                    usuario.setSenha(dto.getSenha()); // fallback sem encoder configurado
                }
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
            return ResponseEntity.ok(sanitizar(atualizado));
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

    // Remove a senha explicitamente (defensivo apesar de @JsonProperty WRITE_ONLY)
    private Usuario sanitizar(Usuario u) {
        u.setSenha(null);
        return u;
    }

    private Usuario montarUsuario(CreateUsuario dto, String fotoBase64Override) {
        String fotoDefinitiva = fotoBase64Override != null ? fotoBase64Override : dto.foto_perfil();
        return Usuario.builder()
                .nome(dto.nome())
                .login(dto.login())
                .email(dto.email())
                .senha(dto.senha()) // service irá codificar
                .telefone(dto.telefone())
                .cpf(dto.cpf())
                .data_nascimento(dto.data_nascimento())
                .tipo_usuario(dto.tipo_usuario())
                .tipo_perfil(dto.tipo_perfil())
                .chave_pix(dto.chave_pix())
                .foto_perfil(fotoDefinitiva)
                .ativo(true)
                .email_verificado(false)
                .possui_loja(false)
                .data_cadastro(java.time.LocalDateTime.now())
                .build();
    }
}
