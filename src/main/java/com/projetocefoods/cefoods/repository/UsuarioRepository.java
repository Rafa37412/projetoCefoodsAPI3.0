package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByEmailVerificationToken(String email_verification_token);
    Optional<Usuario> findByEmailAndEmailVerificationCode(String email, String email_verification_code);
}
