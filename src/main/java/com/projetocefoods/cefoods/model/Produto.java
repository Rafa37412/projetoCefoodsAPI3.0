package com.projetocefoods.cefoods.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_produto")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private Long idProduto;

    @ManyToOne
    @JoinColumn(name = "id_loja", nullable = false)
    @JsonIgnoreProperties({ "usuario" }) // Ignora o campo "usuario" dentro da Loja
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "nome")
    private String nome;
    
    @Column(name = "descricao")
    private String descricao;
    
    @Column(name = "preco")
    private Double preco;
    
    @Column(name = "imagem")
    private String imagem;
    
    @Column(name = "estoque")
    private Integer estoque;
    
    @Column(name = "estoque_minimo")
    private Integer estoqueMinimo;
    
    @Column(name = "disponivel")
    private Boolean disponivel;
    
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;
    
    @Column(name = "vezes_vendido")
    private Integer vezesVendido;

    @Column(name = "avaliacao_media")
    private Double avaliacaoMedia = 0.0;

}
