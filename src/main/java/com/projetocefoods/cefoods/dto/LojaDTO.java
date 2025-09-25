package com.projetocefoods.cefoods.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LojaDTO {

    public record CreateLoja(
            @NotNull Long id_usuario,
            @NotBlank String nome_fantasia,
            String descricao,
            String foto_capa,
            String localizacao,
            String horario_abertura,
            String horario_fechamento,
            Boolean status,
            Boolean visivel,
            Boolean aceita_pix,
            Boolean aceita_dinheiro,
            Boolean aceita_cartao,

            // ✅ Novo campo para turnos abertos
            List<HorarioFuncionamentoDTO> horarios_funcionamento) {
    }

    public record UpdateLoja(
            String nome_fantasia,
            String descricao,
            String foto_capa,
            String localizacao,
            String horario_abertura,
            String horario_fechamento,
            Boolean status,
            Boolean visivel,
            Boolean aceita_pix,
            Boolean aceita_dinheiro,
            Boolean aceita_cartao) {
    }

    public record UpdateLojaStatusReq(
        @NotNull Boolean status,
        Boolean manual_override // ✅ permite o toggle manual
) {}

}
