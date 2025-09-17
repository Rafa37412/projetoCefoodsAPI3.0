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
                                u.getIdUsuario(),
                                u.getNome(),
                                null, // sobrenome (se existir)
                                u.getLogin(),
                                u.getEmail(),
                                u.getTelefone(),
                                u.getCpf(),
                                u.getDataNascimento() != null ? u.getDataNascimento().toString() : null,
                                u.getTipoUsuario(),
                                u.getTipoPerfil(),
                                null, // possuiLoja (se implementar)
                                u.getChavePix(),
                                u.getFotoPerfil(),
                                u.getDataCadastro() != null ? u.getDataCadastro().toString() : null,
                                u.getAtivo(),
                                u.getUltimoAcesso() != null ? u.getUltimoAcesso().toString() : null,
                                u.getEmailVerificado(),
                                u.getTokenRecuperacao());
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.status(401).body("Login ou senha inválidos");
                    }
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Login ou senha inválidos"));
    }

}
