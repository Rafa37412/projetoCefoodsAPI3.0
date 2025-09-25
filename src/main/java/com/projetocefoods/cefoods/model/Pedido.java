/*package com.projetocefoods.cefoods.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.projetocefoods.cefoods.model.FormaPagamento;
import com.projetocefoods.cefoods.model.StatusPedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "tb_pedido")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    private Long idUsuario;

    @ManyToOne
    @JoinColumn(name = "idLoja", nullable = false)
    @JsonIgnoreProperties({"usuario"})
    private Loja loja;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private LocalDateTime dataPedido;

    private LocalDateTime horarioRetirada;

    private Double valorTotal;

    private String observacao;
}
*/
package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loja", nullable = false)
    private Loja loja;

    @Column(name = "nome_cliente")
    private String nomeCliente;
    
    @Column(name = "forma_pagamento")
    private String formaPagamento;
    
    @Column(name = "total")
    private Double total;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "data_pedido")
    private LocalDateTime dataPedido;
    
    @Column(name = "horario_retirada")
    private String horarioRetirada;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoItem> itens;
}
