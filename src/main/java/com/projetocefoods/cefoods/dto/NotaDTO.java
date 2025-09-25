package com.projetocefoods.cefoods.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NotaDTO {

    public record CreateNota(
        String titulo,
        String texto,
        Long id_usuario,
        Long id_loja
    ) {}

    public record AnexoResponse(
        Long id_anexo,
        String nome_arquivo,
        String tipo,
        Long tamanho
    ) {}

    public record NotaResponse(
        Long id_nota,
        String titulo,
        String texto,
        LocalDateTime data_criacao,
        Long id_usuario,
        Long id_loja,
        List<AnexoResponse> anexos
    ) {}
}
