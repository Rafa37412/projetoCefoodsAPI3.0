package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    private String nome;

    private String login;

    private String email;

    private String senha;

    private String telefone;

    private String cpf;

    private LocalDate data_nascimento;

    private String tipo_usuario;

    private String tipo_perfil;

    private String chave_pix;

    private String foto_perfil;

    private LocalDateTime data_cadastro;

    private Boolean ativo;

    private LocalDateTime ultimo_acesso;

    private Boolean email_verificado;

    private String token_recuperacao;

    private Boolean possui_loja;

}