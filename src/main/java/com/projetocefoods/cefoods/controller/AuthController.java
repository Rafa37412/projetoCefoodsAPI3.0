package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.AuthDTOs.LoginRequest;
import com.projetocefoods.cefoods.dto.AuthDTOs.LoginResponse;
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
        return usuarioService.buscarPorLogin(req.login())
                .map(u -> {
                    if (u.getSenha().equals(req.senha())) {
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

}
