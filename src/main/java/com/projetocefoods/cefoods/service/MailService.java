package com.projetocefoods.cefoods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // Pega o valor do application.properties
    @Value("${spring.mail.username}")
    private String remetente;

    /**
     * Método para enviar um e-mail de texto simples.
     * @param destinatario O e-mail do destinatário.
     * @param assunto O assunto do e-mail.
     * @param mensagem O corpo do e-mail.
     * @return Uma mensagem de sucesso ou falha.
     */
    public String enviarEmailDeTexto(String destinatario, String assunto, String mensagem) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(remetente);
            simpleMailMessage.setTo(destinatario);
            simpleMailMessage.setSubject(assunto);
            simpleMailMessage.setText(mensagem);
            javaMailSender.send(simpleMailMessage);
            return "Email enviado com sucesso!";
        } catch (Exception e) {
            return "Erro ao tentar enviar o email: " + e.getLocalizedMessage();
        }
    }
}