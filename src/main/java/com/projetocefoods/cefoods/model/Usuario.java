package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    // A senha só pode ser escrita (entrada), nunca exposta em respostas JSON
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
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

    // Token para verificação de e-mail pós cadastro
    private String email_verification_token;

    // Data/hora de expiração do token de verificação
    private LocalDateTime email_verification_expira;

    private Boolean possui_loja;

}