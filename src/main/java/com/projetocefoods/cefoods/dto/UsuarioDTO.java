package com.projetocefoods.cefoods.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UsuarioDTO {
    public record CreateUsuario(
            @NotBlank String nome,
            @NotBlank String login,
            @Email @NotBlank String email,
            @NotBlank String senha,
            String telefone,
            String cpf,
            LocalDate data_nascimento,

            @NotBlank String tipo_usuario, // Ex: "aluno", "professor", etc.
            @NotBlank String tipo_perfil,  // Ex: "admin", "cliente", "vendedor"

            String chave_pix,
            String foto_perfil
    ) {}
}
