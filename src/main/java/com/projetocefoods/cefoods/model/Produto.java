package com.projetocefoods.cefoods.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_loja", nullable = false)
    @JsonIgnoreProperties({ "usuario" }) // Ignora o campo "usuario" dentro da Loja
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    private String nome;
    
    private String descricao;
    
    private Double preco;
    
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String imagem;
    
    private Integer estoque;
    
    private Integer estoque_minimo;
    
    private Boolean disponivel;
    
    private LocalDateTime data_cadastro;
    
    private Integer vezes_vendido;

    @Builder.Default
    private Double avaliacao_media = 0.0;

}
