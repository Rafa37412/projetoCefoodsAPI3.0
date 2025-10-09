package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor; // Importe esta anotação do Lombok
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe para transações

import java.time.LocalDateTime;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor // Anotação do Lombok que cria o construtor para você
@Slf4j
public class UsuarioService {

    // Com @RequiredArgsConstructor, as dependências podem ser 'final' e não
    // precisam de @Autowired
    private final UsuarioRepository usuarioRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder; // Injetando o codificador de senhas

    /**
     * Busca um usuário pelo seu login.
     * (Mantendo o método que você já tinha para o AuthController)
     */
    public java.util.Optional<Usuario> buscarPorLogin(String login) {
        var lista = usuarioRepository.findAllByLogin(login);
        if (lista.isEmpty())
            return java.util.Optional.empty();
        if (lista.size() > 1) {
            log.error("Inconsistência: {} usuários encontrados com login='{}' ao buscarPorLogin", lista.size(), login);
            // Política temporária: escolher o de maior id (assumindo mais recente) até
            // limpeza dos duplicados
            return java.util.Optional.of(lista.stream().max(java.util.Comparator.comparingLong(Usuario::getId)).get());
        }
        return java.util.Optional.of(lista.get(0));
    }

    /**
     * Autentica usuário com suporte a migração transparente de senhas legadas
     * não-BCrypt.
     * Caso a senha armazenada ainda esteja em texto plano (não começa com
     * $2a/$2b/$2y),
     * faz a comparação direta; se bater, re-hash e salva imediatamente.
     */
    @Transactional
    public java.util.Optional<Usuario> autenticar(String login, String senhaPura) {
        var lista = usuarioRepository.findAllByLogin(login);
        if (lista.isEmpty())
            return java.util.Optional.empty();
        if (lista.size() > 1) {
            log.error("Login duplicado detectado durante autenticação: login='{}' quantidade={}", login, lista.size());
        }
        return lista.stream().flatMap(u -> {
            String armazenada = u.getSenha();
            if (armazenada == null)
                return java.util.stream.Stream.empty();
            boolean ehBCrypt = armazenada.startsWith("$2a$") || armazenada.startsWith("$2b$")
                    || armazenada.startsWith("$2y$");
            if (ehBCrypt) {
                if (passwordEncoder.matches(senhaPura, armazenada)) {
                    return java.util.stream.Stream.of(u);
                } else {
                    return java.util.stream.Stream.empty();
                }
            } else {
                // Legacy: texto puro no banco
                if (armazenada.equals(senhaPura)) {
                    // Atualiza para BCrypt (upgrade de segurança transparente)
                    u.setSenha(passwordEncoder.encode(senhaPura));
                    usuarioRepository.save(u);
                    return java.util.stream.Stream.of(u);
                } else {
                    return java.util.stream.Stream.empty();
                }
            }
        }).findFirst();
    }

    /**
     * Cadastra um novo usuário, com validação de duplicidade,
     * criptografia de senha e envio de e-mail de boas-vindas.
     * A anotação @Transactional garante que todas as operações (salvar e enviar
     * email)
     * sejam concluídas com sucesso, ou nenhuma delas é efetivada.
     */
    @Transactional
    public Usuario cadastrarNovoUsuario(Usuario novoUsuario) {
        // 1. VALIDAÇÃO: Verifica se o login já existe
        if (!usuarioRepository.findAllByLogin(novoUsuario.getLogin()).isEmpty()) {
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

        // Dentro do método cadastrarNovoUsuario, substitua o bloco de envio de e-mail
        // por este:

        // 4. ENVIO: E-mail de verificação
        if (usuarioSalvo.getEmail() != null && !usuarioSalvo.getEmail().isBlank()) {
            String assunto = "Código de Verificação - Cefoods";
            String mensagem = "Olá, " + usuarioSalvo.getNome() + ",\n\n" +
                    "Seu código de verificação é: " + code + "\n\n" +
                    "Ele expira em 15 minutos. Digite este código no app para confirmar seu e-mail.\n\n" +
                    "Se não foi você que solicitou, ignore.";
            try {
                mailService.enviarEmailDeTexto(usuarioSalvo.getEmail(), assunto, mensagem);
                log.info("E-mail de verificação enviado com sucesso para {}", usuarioSalvo.getEmail());
            } catch (Exception e) {
                log.error("Cadastro do usuário {} bem-sucedido, MAS FALHOU ao enviar o e-mail de verificação.",
                        usuarioSalvo.getLogin(), e);
                // Aqui você não lança a exceção, para não reverter o cadastro,
                // mas o log.error irá te avisar sobre a falha no envio.
            }
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
        try {
            mailService.enviarEmailDeTexto(u.getEmail(), assunto, mensagem);
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail de verificação para {}", email, ex);
            throw new IllegalStateException("Não foi possível enviar o e-mail de verificação", ex);
        }
    }

    @Transactional
    public boolean confirmarCodigo(String email, String code) {
        return usuarioRepository.findByEmailAndEmail_verification_code(email, code)
                .map(u -> {
                    if (u.getEmail_verification_code_expira() != null
                            && u.getEmail_verification_code_expira().isBefore(LocalDateTime.now())) {
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
            log.info("Solicitação de recuperação para {} com e-mail não verificado; enviando mesmo assim", email);
        }
        String code = gerarCodigo6();
        u.setToken_recuperacao(code);
        u.setToken_recuperacao_expira(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(u);
        String assunto = "Código para redefinição de senha - Cefoods";
        String msg = "Olá, " + u.getNome() + ",\n\nSeu código para redefinir a senha é: " + code
                + " (válido por 15 minutos).\n\nSe não foi você, ignore.";
        try {
            mailService.enviarEmailDeTexto(u.getEmail(), assunto, msg);
        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail de recuperação para {}", email, ex);
            throw new IllegalStateException("Não foi possível enviar o e-mail de recuperação", ex);
        }
    }

    @Transactional
    public void reenviarRecuperacaoSenha(String email) {
        solicitarRecuperacaoSenha(email); // mesma lógica
    }

    @Transactional
    public void validarCodigoRecuperacao(String email, String code) {
        Usuario u = usuarioRepository.findByEmailAndToken_recuperacao(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
        if (u.getToken_recuperacao_expira() != null && u.getToken_recuperacao_expira().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Código expirado");
        }
        // Marca apenas que o código foi validado mantendo-o até redefinição ou podemos
        // limpar já.
        // Aqui optamos por manter até a redefinição efetiva.
    }

    @Transactional
    public void redefinirSenha(String email, String code, String novaSenha) {
        Usuario u = usuarioRepository.findByEmailAndToken_recuperacao(email, code)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido"));
        if (u.getToken_recuperacao_expira() != null && u.getToken_recuperacao_expira().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Código expirado");
        }
        u.setSenha(passwordEncoder.encode(novaSenha));
        u.setToken_recuperacao(null);
        u.setToken_recuperacao_expira(null);
        usuarioRepository.save(u);
    }

    // ... (outros métodos do seu serviço)
}