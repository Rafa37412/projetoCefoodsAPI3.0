package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor; // Importe esta anotação do Lombok
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe para transações

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Anotação do Lombok que cria o construtor para você
public class UsuarioService {

    // Com @RequiredArgsConstructor, as dependências podem ser 'final' e não precisam de @Autowired
    private final UsuarioRepository usuarioRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder; // Injetando o codificador de senhas

    /**
     * Busca um usuário pelo seu login.
     * (Mantendo o método que você já tinha para o AuthController)
     */
    public Optional<Usuario> buscarPorLogin(String login) {
        return usuarioRepository.findByLogin(login);
    }
    
    /**
     * Cadastra um novo usuário, com validação de duplicidade,
     * criptografia de senha e envio de e-mail de boas-vindas.
     * A anotação @Transactional garante que todas as operações (salvar e enviar email)
     * sejam concluídas com sucesso, ou nenhuma delas é efetivada.
     */
    @Transactional
    public Usuario cadastrarNovoUsuario(Usuario novoUsuario) {
        // 1. VALIDAÇÃO: Verifica se o login já existe
        if (usuarioRepository.findByLogin(novoUsuario.getLogin()).isPresent()) {
            // Lança uma exceção específica que pode ser tratada no Controller
            throw new IllegalStateException("O login '" + novoUsuario.getLogin() + "' já está em uso.");
        }

        // Verifica e-mail duplicado (se fornecido)
        if (novoUsuario.getEmail() != null && usuarioRepository.findByEmail(novoUsuario.getEmail()).isPresent()) {
            throw new IllegalStateException("O e-mail '" + novoUsuario.getEmail() + "' já está em uso.");
        }

        // 2. SEGURANÇA: Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        
        // 3. PERSISTÊNCIA: Salva o novo usuário no banco de dados
        // Inicializa status de verificação
        novoUsuario.setEmail_verificado(false);
        novoUsuario.setData_cadastro(LocalDateTime.now());
        novoUsuario.setAtivo(true);

        // Gera token de verificação
        String token = UUID.randomUUID().toString();
        novoUsuario.setEmail_verification_token(token);
        novoUsuario.setEmail_verification_expira(LocalDateTime.now().plusHours(24));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 4. ENVIO: E-mail de verificação (somente se forneceu e-mail)
        if (usuarioSalvo.getEmail() != null && !usuarioSalvo.getEmail().isBlank()) {
            String linkVerificacao = "https://seu-dominio-ou-frontend.com/verificar-email?token=" + token;
            String assunto = "Verifique seu e-mail - Cefoods";
            String mensagem = "Olá, " + usuarioSalvo.getNome() + ",\n\n" +
                    "Obrigado por se cadastrar no Cefoods. Por favor confirme seu e-mail clicando no link abaixo (válido por 24h):\n" +
                    linkVerificacao + "\n\nSe você não solicitou este cadastro, ignore este e-mail.";
            mailService.enviarEmailDeTexto(usuarioSalvo.getEmail(), assunto, mensagem);
        }

        return usuarioSalvo;
    }

    @Transactional
    public boolean verificarEmail(String token) {
        if (token == null || token.isBlank()) return false;
        return usuarioRepository.findByEmailVerificationToken(token)
                .map(u -> {
                    if (u.getEmail_verificado() != null && u.getEmail_verificado()) return true; // já verificado
                    if (u.getEmail_verification_expira() != null && u.getEmail_verification_expira().isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Token expirado. Solicite novo envio.");
                    }
                    u.setEmail_verificado(true);
                    u.setEmail_verification_token(null);
                    u.setEmail_verification_expira(null);
                    usuarioRepository.save(u);
                    return true;
                }).orElse(false);
    }

    @Transactional
    public void reenviarVerificacao(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com esse e-mail não encontrado"));
        if (Boolean.TRUE.equals(u.getEmail_verificado())) {
            throw new IllegalStateException("E-mail já verificado");
        }
        String token = UUID.randomUUID().toString();
        u.setEmail_verification_token(token);
        u.setEmail_verification_expira(LocalDateTime.now().plusHours(24));
        usuarioRepository.save(u);
        String linkVerificacao = "https://seu-dominio-ou-frontend.com/verificar-email?token=" + token;
        String assunto = "Novo link de verificação - Cefoods";
        String mensagem = "Olá, " + u.getNome() + ",\n\nSegue novo link para verificar seu e-mail (24h):\n" + linkVerificacao;
        mailService.enviarEmailDeTexto(u.getEmail(), assunto, mensagem);
    }

    // ... (outros métodos do seu serviço)
}