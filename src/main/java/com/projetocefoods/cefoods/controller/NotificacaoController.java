package com.projetocefoods.cefoods.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projetocefoods.cefoods.model.Notificacao;
import com.projetocefoods.cefoods.service.NotificacaoService;

import java.util.List;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8100") // libera só o Ionic
public class NotificacaoController {

    private final NotificacaoService notifService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Notificacao>> listarPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notifService.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<Notificacao> marcarComoLida(@PathVariable Long id) {
        return ResponseEntity.ok(notifService.marcarComoLida(id));
    }
}
