package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_item_pedido")
@IdClass(ItemPedidoId.class)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedido {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    private Integer quantidade;

    private Double preco_unitario;

    @Column(insertable = false, updatable = false)
    private Double subtotal;

}
