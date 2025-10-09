package com.projetocefoods.cefoods.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
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

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(columnDefinition = "TEXT")
    private String foto_perfil;

    private LocalDateTime data_cadastro;

    private Boolean ativo;

    private LocalDateTime ultimo_acesso;

    private Boolean email_verificado;

    private String token_recuperacao;
    private LocalDateTime token_recuperacao_expira;
    // (Legacy token-based verification removed. Using only 6-digit code now.)
    // Código numérico de 6 dígitos para verificação rápida
    private String email_verification_code;

    // Expiração específica do código numérico (pode ser menor, ex: 15 min)
    private LocalDateTime email_verification_code_expira;

    private Boolean possui_loja;

}