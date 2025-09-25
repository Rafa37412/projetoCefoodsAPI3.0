package com.projetocefoods.cefoods.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProdutoDTO {
    public record CreateProduto(
            Long id_loja,
            Long id_categoria,
            String nome,
            String descricao,
            Double preco,
            String imagem,
            Integer estoque,
            Integer estoque_minimo,
            Boolean disponivel) {
    }
}
