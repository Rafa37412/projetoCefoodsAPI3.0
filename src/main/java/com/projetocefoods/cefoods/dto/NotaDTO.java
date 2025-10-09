package com.projetocefoods.cefoods.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class NotaDTO {

    public record CreateNota(
            @NotBlank(message = "titulo é obrigatório")
            String titulo,

            String texto,

            @JsonAlias({"id_usuario", "idUsuario"})
            @NotNull(message = "id_usuario/idUsuario é obrigatório")
            Long idUsuario,

            @JsonAlias({"id_loja", "idLoja"})
            @NotNull(message = "id_loja/idLoja é obrigatório")
            Long idLoja
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
        LocalDateTime dataCriacao,
        Long id_usuario,
        Long id_loja,
        List<AnexoResponse> anexos
    ) {}
}
