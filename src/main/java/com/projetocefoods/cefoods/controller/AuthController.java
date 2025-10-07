package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.AuthDTOs.LoginRequest;
import com.projetocefoods.cefoods.dto.AuthDTOs.LoginResponse;
import com.projetocefoods.cefoods.dto.AuthDTOs.ResetPasswordRequest;
import com.projetocefoods.cefoods.dto.MessageResponse;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated LoginRequest req) {
        try {
            return usuarioService.autenticar(req.login(), req.senha())
                    .<ResponseEntity<?>>map(u -> {
                        LoginResponse response = new LoginResponse(
                                u.getId(),
                                u.getNome(),
                                null, // sobrenome
                                u.getLogin(),
                                u.getEmail(),
                                u.getTelefone(),
                                u.getCpf(),
                                u.getData_nascimento() != null ? u.getData_nascimento().toString() : null,
                                u.getTipo_usuario(),
                                u.getTipo_perfil(),
                                null, // possuiLoja - ajustar se houver lógica
                                u.getChave_pix(),
                                u.getFoto_perfil(),
                                u.getData_cadastro() != null ? u.getData_cadastro().toString() : null,
                                u.getAtivo(),
                                u.getUltimo_acesso() != null ? u.getUltimo_acesso().toString() : null,
                                u.getEmail_verificado(),
                                u.getToken_recuperacao()
                        );
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.status(401).body(new MessageResponse("Login ou senha inválidos")));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Erro interno durante autenticação"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            usuarioService.cadastrarNovoUsuario(usuario);
            return ResponseEntity.ok(new MessageResponse("Usuário cadastrado. Verifique seu e-mail para confirmar (se fornecido)."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    // ==================== VERIFICAÇÃO DE E-MAIL ====================
    @PostMapping("/verification/resend-code")
    public ResponseEntity<?> reenviarCodigoVerificacao(@RequestParam("email") String email) {
        try {
            usuarioService.reenviarCodigo(email);
            return ResponseEntity.ok(new MessageResponse("Código de verificação reenviado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/verification/confirm-code")
    public ResponseEntity<?> confirmarCodigoVerificacao(@RequestParam("email") String email, @RequestParam("code") String code) {
        try {
            boolean ok = usuarioService.confirmarCodigo(email, code);
            if (ok) return ResponseEntity.ok(new MessageResponse("E-mail verificado com sucesso"));
            return ResponseEntity.badRequest().body(new MessageResponse("Código inválido"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // ==================== RECUPERAÇÃO DE SENHA ====================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            usuarioService.solicitarRecuperacaoSenha(email);
            return ResponseEntity.ok(new MessageResponse("Código de recuperação enviado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/resend-code")
    public ResponseEntity<?> resendRecoveryCode(@RequestParam("email") String email) {
        try {
            usuarioService.reenviarRecuperacaoSenha(email);
            return ResponseEntity.ok(new MessageResponse("Novo código de recuperação enviado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/confirm-code")
    public ResponseEntity<?> confirmRecoveryCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        try {
            usuarioService.validarCodigoRecuperacao(email, code);
            return ResponseEntity.ok(new MessageResponse("Código válido"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Código inválido"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Código expirado"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody @Validated ResetPasswordRequest request) {
        try {
            usuarioService.redefinirSenha(request.email(), request.code(), request.novaSenha());
            return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Código inválido"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

}
