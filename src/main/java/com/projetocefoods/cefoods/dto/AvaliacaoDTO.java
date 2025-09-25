package com.projetocefoods.cefoods.dto;

public class AvaliacaoDTO {
    public record CreateAvaliacao(
        Long id_produto,
        Long id_usuario,
        int estrelas
    ) {}
}
