
package com.projetocefoods.cefoods.controller;

import com.projetocefoods.cefoods.dto.HorarioFuncionamentoDTO;
import com.projetocefoods.cefoods.dto.LojaDTO.CreateLoja;
import com.projetocefoods.cefoods.dto.LojaDTO.UpdateLoja;
import com.projetocefoods.cefoods.dto.LojaDTO.UpdateLojaStatusReq;
import com.projetocefoods.cefoods.dto.LojaResponse;
import com.projetocefoods.cefoods.service.LojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lojas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LojaController {
    private final LojaService service;

    @PostMapping
    public ResponseEntity<LojaResponse> criar(@RequestBody CreateLoja dto) {
        System.out.println("Recebido CreateLoja no backend: idUsuario=" + dto.idUsuario() + ", status=" + dto.status());
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<LojaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<LojaResponse>> porUsuario(@PathVariable("idUsuario") Long idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaResponse> atualizar(@PathVariable Long id, @RequestBody UpdateLoja dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.desativarLoja(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{idLoja}/horarios")
    public ResponseEntity<List<HorarioFuncionamentoDTO>> listarHorarios(@PathVariable Long idLoja) {
        return ResponseEntity.ok(service.listarHorarios(idLoja));
    }

    @PutMapping("/{idLoja}/horarios")
    public ResponseEntity<Void> atualizarHorarios(
            @PathVariable Long idLoja,
            @RequestBody List<HorarioFuncionamentoDTO> novosHorarios) {
        service.atualizarHorarios(idLoja, novosHorarios);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idLoja}/status")
    public ResponseEntity<LojaResponse> atualizarStatus(
            @PathVariable Long idLoja,
            @RequestBody UpdateLojaStatusReq dto) {
        return ResponseEntity.ok(service.atualizarStatus(idLoja, dto));
    }
}
