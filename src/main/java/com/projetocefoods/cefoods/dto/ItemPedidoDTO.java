package com.projetocefoods.cefoods.dto;

public class ItemPedidoDTO {

    public record ItemPedidoCreate(
            Long id_pedido,
            Long id_produto,
            Integer quantidade,
            Double preco_unitario) {
    }

    public record ItemPedidoResponse(
            Long id_pedido,
            Long id_produto,
            String nome_produto, // 🔹 novo campo
            Integer quantidade,
            Double preco_unitario,
            Double subtotal) {
    }

    public record ItemPedidoUpdate(
            Integer quantidade,
            Double preco_unitario) {
    }
}
