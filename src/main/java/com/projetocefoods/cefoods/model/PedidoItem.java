package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_pedido_item")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PedidoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido_item")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    private String nome;
    
    private Double preco;
    
    private Integer quantidade;
}
