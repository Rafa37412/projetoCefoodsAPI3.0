package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor; // Importe esta anotação do Lombok
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe para transações

import java.util.Optional;
import java.time.LocalDateTime;
import java.security.SecureRandom;

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

    // Gera apenas código de verificação (token UUID legacy removido)
    String code = gerarCodigo6();
    novoUsuario.setEmail_verification_code(code);
    novoUsuario.setEmail_verification_code_expira(LocalDateTime.now().plusMinutes(15));

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 4. ENVIO: E-mail de verificação (somente se forneceu e-mail)
    if (usuarioSalvo.getEmail() != null && !usuarioSalvo.getEmail().isBlank()) {
        String assunto = "Código de Verificação - Cefoods";
        String mensagem = "Olá, " + usuarioSalvo.getNome() + ",\n\n" +
            "Seu código de verificação é: " + code + "\n\n" +
            "Ele expira em 15 minutos. Digite este código no app para confirmar seu e-mail.\n\n" +
            "Se não foi você que solicitou, ignore.";
        mailService.enviarEmailDeTexto(usuarioSalvo.getEmail(), assunto, mensagem);
    }

        return usuarioSalvo;
    }

    // Métodos de verificação via token legacy removidos

    @Transactional
    public void reenviarCodigo(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário com esse e-mail não encontrado"));
        if (Boolean.TRUE.equals(u.getEmail_verificado())) {
            throw new IllegalStateException("E-mail já verificado");
        }
        String code = gerarCodigo6();
        u.setEmail_verification_code(code);
        u.setEmail_verification_code_expira(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(u);
        String assunto = "Novo código de verificação - Cefoods";
        String mensagem = "Olá, " + u.getNome() + ",\n\nSeu novo código é: " + code + " (válido por 15 minutos).";
        mailService.enviarEmailDeTexto(u.getEmail(), assunto, mensagem);
    }

    @Transactional
    public boolean confirmarCodigo(String email, String code) {
        return usuarioRepository.findByEmailAndEmailVerificationCode(email, code)
                .map(u -> {
                    if (u.getEmail_verification_code_expira() != null && u.getEmail_verification_code_expira().isBefore(LocalDateTime.now())) {
                        throw new IllegalStateException("Código expirado");
                    }
                    u.setEmail_verificado(true);
                    u.setEmail_verification_code(null);
                    u.setEmail_verification_code_expira(null);
                    usuarioRepository.save(u);
                    return true;
                }).orElse(false);
    }

    private String gerarCodigo6() {
        SecureRandom r = new SecureRandom();
        int n = r.nextInt(1_000_000); // 0..999999
        return String.format("%06d", n);
    }

    // ======================= RECUPERAÇÃO DE SENHA =======================
    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        if (!Boolean.TRUE.equals(u.getEmail_verificado())) {
            throw new IllegalStateException("E-mail ainda não verificado");
        }
        String code = gerarCodigo6();
        u.setToken_recuperacao(code);
        u.setToken_recuperacao_expira(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(u);
        String assunto = "Código para redefinição de senha - Cefoods";
        String msg = "Olá, " + u.getNome() + ",\n\nSeu código para redefinir a senha é: " + code + " (válido por 15 minutos).\n\nSe não foi você, ignore.";
        mailService.enviarEmailDeTexto(u.getEmail(), assunto, msg);
    }

    @Transactional
    public void reenviarRecuperacaoSenha(String email) {
        solicitarRecuperacaoSenha(email); // mesma lógica
    }

    @Transactional
    public void validarCodigoRecuperacao(String email, String code) {
        Usuario u = usuarioRepository.findByEmailAndTokenRecuperacao(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
        if (u.getToken_recuperacao_expira() != null && u.getToken_recuperacao_expira().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Código expirado");
        }
        // Marca apenas que o código foi validado mantendo-o até redefinição ou podemos limpar já.
        // Aqui optamos por manter até a redefinição efetiva.
    }

    @Transactional
    public void redefinirSenha(String email, String code, String novaSenha) {
        Usuario u = usuarioRepository.findByEmailAndTokenRecuperacao(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
        if (u.getToken_recuperacao_expira() != null && u.getToken_recuperacao_expira().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Código expirado" );
        }
        if (!Boolean.TRUE.equals(u.getEmail_verificado())) {
            throw new IllegalStateException("E-mail não verificado" );
        }
        u.setSenha(passwordEncoder.encode(novaSenha));
        u.setToken_recuperacao(null);
        u.setToken_recuperacao_expira(null);
        usuarioRepository.save(u);
    }

    // ... (outros métodos do seu serviço)
}