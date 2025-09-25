package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_loja")
public class Loja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_loja")
    private Long idLoja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    
    @Column(name = "descricao", length = 1000)
    private String descricao;
    
    @Column(name = "foto_capa")
    private String fotoCapa;
    
    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "manual_override") // ✅ novo campo
    private Boolean manualOverride;

    @Column(name = "visivel")
    private Boolean visivel;
    
    @Column(name = "aceita_pix")
    private Boolean aceitaPix;
    
    @Column(name = "aceita_dinheiro")
    private Boolean aceitaDinheiro;
    
    @Column(name = "aceita_cartao")
    private Boolean aceitaCartao;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    
    @Column(name = "qtd_produtos_vendidos")
    private Integer qtdProdutosVendidos;
    
    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia;
    
    @Column(name = "total_pedidos")
    private Integer totalPedidos;
}
