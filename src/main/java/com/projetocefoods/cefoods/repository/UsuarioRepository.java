package com.projetocefoods.cefoods.repository;

import com.projetocefoods.cefoods.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    Optional<Usuario> findByEmail(String email);

    // Usamos @Query porque o campo na entidade está em snake_case (email_verification_code)
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.email_verification_code = :code")
    Optional<Usuario> findByEmailAndEmail_verification_code(@Param("email") String email, @Param("code") String code);

    // Recuperação (buscar só pelo token)
    @Query("SELECT u FROM Usuario u WHERE u.token_recuperacao = :token")
    Optional<Usuario> findByTokenRecuperacao(@Param("token") String token);

    // Recuperação de senha: combinar email + código
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.token_recuperacao = :code")
    Optional<Usuario> findByEmailAndToken_recuperacao(@Param("email") String email, @Param("code") String tokenRecuperacao);
}