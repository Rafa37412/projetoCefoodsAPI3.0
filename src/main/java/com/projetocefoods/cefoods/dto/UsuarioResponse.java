package com.projetocefoods.cefoods.dto;

public record UsuarioResponse(
    Long id_usuario,
    String nome,
    String email,
    String login
) {}
