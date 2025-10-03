package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailAndEmailVerificationCode(String email, String emailVerificationCode);
    Optional<Usuario> findByTokenRecuperacao(String token_recuperacao);
    Optional<Usuario> findByEmailAndTokenRecuperacao(String email, String token_recuperacao);
}
