package com.projetocefoods.cefoods.service;

import com.projetocefoods.cefoods.model.Usuario;
import com.projetocefoods.cefoods.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor; // Importe esta anotação do Lombok
import org.springframework.security.crypto.password.PasswordEncoder; // Importe o PasswordEncoder
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importe para transações

import java.util.Optional;

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

        // 2. SEGURANÇA: Criptografa a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(novoUsuario.getSenha());
        novoUsuario.setSenha(senhaCriptografada);
        
        // 3. PERSISTÊNCIA: Salva o novo usuário no banco de dados
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        // 4. NOTIFICAÇÃO: Envia o e-mail de boas-vindas
        String emailDestinatario = usuarioSalvo.getEmail();
        String nomeUsuario = usuarioSalvo.getNome();
        
        String assunto = "Bem-vindo ao Cefoods!";
        String mensagem = "Olá, " + nomeUsuario + "!\n\nSeu cadastro na plataforma Cefoods foi realizado com sucesso.\n\nAtenciosamente,\nEquipe Cefoods";
        
        mailService.enviarEmailDeTexto(emailDestinatario, assunto, mensagem);
        
        return usuarioSalvo;
    }

    // ... (outros métodos do seu serviço)
}