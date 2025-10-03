package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.AuthDTOs.LoginRequest;
import com.projetocefoods.cefoods.dto.AuthDTOs.LoginResponse;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest req) {
    return usuarioService.buscarPorLogin(req.login())
        .map(u -> {
            if (passwordEncoder.matches(req.senha(), u.getSenha())) {
                        LoginResponse response = new LoginResponse(
                                u.getId(),
                                u.getNome(),
                                null, // sobrenome (se existir)
                                u.getLogin(),
                                u.getEmail(),
                                u.getTelefone(),
                                u.getCpf(),
                                u.getData_nascimento() != null ? u.getData_nascimento().toString() : null,
                                u.getTipo_usuario(),
                                u.getTipo_perfil(),
                                null, // possui_loja (se implementar)
                                u.getChave_pix(),
                                u.getFoto_perfil(),
                                u.getData_cadastro() != null ? u.getData_cadastro().toString() : null,
                                u.getAtivo(),
                                u.getUltimo_acesso() != null ? u.getUltimo_acesso().toString() : null,
                                u.getEmail_verificado(),
                                u.getToken_recuperacao());
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(401).body("Login ou senha inválidos");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Login ou senha inválidos"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            usuarioService.cadastrarNovoUsuario(usuario);
            return ResponseEntity.ok("Usuário cadastrado. Verifique seu e-mail para confirmar (se fornecido).");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // Endpoints legacy (/verify, /resend-verification) removidos. Agora usamos somente código de 6 dígitos.

    // Reenviar somente o código de 6 dígitos
    @PostMapping("/resend-code")
    public ResponseEntity<?> reenviarCodigo(@RequestParam("email") String email) {
        try {
            usuarioService.reenviarCodigo(email);
            return ResponseEntity.ok("Código reenviado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Confirmar código de 6 dígitos
    @PostMapping("/confirm-code")
    public ResponseEntity<?> confirmarCodigo(@RequestParam("email") String email, @RequestParam("code") String code) {
        try {
            boolean ok = usuarioService.confirmarCodigo(email, code);
            if (ok) return ResponseEntity.ok("E-mail verificado com sucesso");
            return ResponseEntity.badRequest().body("Código inválido");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==================== RECUPERAÇÃO DE SENHA ====================
    @PostMapping("/password/forgot")
    public ResponseEntity<?> solicitarRecuperacao(@RequestParam("email") String email) {
        try {
            usuarioService.solicitarRecuperacaoSenha(email);
            return ResponseEntity.ok("Código de recuperação enviado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/resend")
    public ResponseEntity<?> reenviarRecuperacao(@RequestParam("email") String email) {
        try {
            usuarioService.reenviarRecuperacaoSenha(email);
            return ResponseEntity.ok("Novo código de recuperação enviado");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/validate-code")
    public ResponseEntity<?> validarCodigo(@RequestParam("email") String email, @RequestParam("code") String code) {
        try {
            usuarioService.validarCodigoRecuperacao(email, code);
            return ResponseEntity.ok("Código válido");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> redefinirSenha(@RequestParam("email") String email,
                                            @RequestParam("code") String code,
                                            @RequestParam("newPassword") String novaSenha) {
        try {
            usuarioService.redefinirSenha(email, code, novaSenha);
            return ResponseEntity.ok("Senha alterada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
