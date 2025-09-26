package com.projetocefoods.cefoods.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_nota_anexo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Anexo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anexo")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nota", nullable = false)
    @JsonIgnore
    private Nota nota;

    private String nome_arquivo;
    
    private String tipo;
    
    private Long tamanho;

    @Lob
    @Column(name = "dados", columnDefinition = "LONGBLOB")
    private byte[] dados;
}

