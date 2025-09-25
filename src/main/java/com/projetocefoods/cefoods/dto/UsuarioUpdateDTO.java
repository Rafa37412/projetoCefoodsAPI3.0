// src/main/java/com/projetocefoods/cefoods/dto/UsuarioUpdateDTO.java
package com.projetocefoods.cefoods.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateDTO {
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
    private Boolean ativo;
    private Boolean email_verificado;
    private String token_recuperacao;
    private LocalDateTime ultimo_acesso;
    private Boolean possui_loja;

    // getters e setters (pode usar Lombok @Getter @Setter também)
}
