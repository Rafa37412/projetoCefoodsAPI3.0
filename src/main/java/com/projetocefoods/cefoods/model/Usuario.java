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
    @Column(name = "idUsuario")
    private Long idUsuario;

    private String nome;
    private String login;
    private String email;
    private String senha;
    private String telefone;
    private String cpf;

    @Column(name = "dataNascimento")
    private LocalDate dataNascimento;

    @Column(name = "tipoUsuario")
    private String tipoUsuario;

    @Column(name = "tipoPerfil")
    private String tipoPerfil;

    @Column(name = "chavePix")
    private String chavePix;

    @Column(name = "fotoPerfil")
    private String fotoPerfil;

    @Column(name = "dataCadastro")
    private LocalDateTime dataCadastro;

    private Boolean ativo;

    @Column(name = "ultimoAcesso")
    private LocalDateTime ultimoAcesso;

    @Column(name = "emailVerificado")
    private Boolean emailVerificado;

    @Column(name = "tokenRecuperacao")
    private String tokenRecuperacao;

    @Column(name = "possuiLoja")
    private Boolean possuiLoja;

}
