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
    private Long id_loja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String nome_fantasia;
    
    @Column(length = 1000)
    private String descricao;
    
    private String foto_capa;
    
    private String localizacao;

    private Boolean status;

    private Boolean manual_override; // ✅ novo campo

    private Boolean visivel;
    
    private Boolean aceita_pix;
    
    private Boolean aceita_dinheiro;
    
    private Boolean aceita_cartao;

    private LocalDateTime data_criacao;
    
    private Integer qtd_produtos_vendidos;
    
    private Double avaliacao_media;
    
    private Integer total_pedidos;
}
