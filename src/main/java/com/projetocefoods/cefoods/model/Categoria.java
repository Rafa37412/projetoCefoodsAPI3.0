package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    private String nome;

    private String descricao;

    private String icone;

    // ➤ Nenhum relacionamento com Produto
}
