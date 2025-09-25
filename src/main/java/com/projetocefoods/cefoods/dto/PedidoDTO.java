package com.projetocefoods.cefoods.dto;

import java.util.List;

public class PedidoDTO {

    public record PedidoCreate(
            Long id_usuario,
            Long id_loja,
            String forma_pagamento,
            String observacao) {
    }

    public record PedidoResponse(
            Long id_pedido,
            Long id_usuario,
            Long id_loja,
            String nome_cliente,
            String forma_pagamento,
            Double valor_total,
            String status,
            java.time.LocalDateTime data_pedido,
            String horario_retirada,
            List<ItemPedidoDTO.ItemPedidoResponse> itens
    ) {
    }
}
