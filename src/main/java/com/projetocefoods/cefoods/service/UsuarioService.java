package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.dto.UsuarioDTO.CreateUsuario;
import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository repo;

    public Usuario cadastrar(CreateUsuario dto) {
        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .login(dto.login())
                .email(dto.email())
                .senha(dto.senha()) // HASH é recomendado
                .telefone(dto.telefone())
                .cpf(dto.cpf())
                .dataNascimento(dto.dataNascimento())
                .tipoUsuario(dto.tipoUsuario())
                .tipoPerfil(dto.tipoPerfil())
                .chavePix(dto.chavePix())
                .fotoPerfil(dto.fotoPerfil())
                .dataCadastro(LocalDateTime.now())
                .ativo(true)
                .emailVerificado(false)
                .build();

        return repo.save(usuario);
    }

    public List<Usuario> listar() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorLogin(String login) {
        return repo.findByLogin(login);
    }
}
