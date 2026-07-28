package com.cert.calibration_hub.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "Activation de votre compte - Calibration Hub";
        String link = baseUrl + "/api/auth/verify?token=" + token;
        String body = "Bonjour,\n\n"
                + "Votre compte Calibration Hub a été créé. "
                + "Veuillez cliquer sur le lien suivant pour l'activer :\n\n"
                + link + "\n\n"
                + "Ce lien expire dans 24 heures.\n\n"
                + "Cordialement,\nCERT - Calibration Hub";

        sendEmail(toEmail, subject, body);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String subject = "Réinitialisation de votre mot de passe - Calibration Hub";
        String link = baseUrl + "/api/auth/reset-password?token=" + token;
        String body = "Bonjour,\n\n"
                + "Vous avez demandé la réinitialisation de votre mot de passe. "
                + "Cliquez sur le lien suivant pour définir un nouveau mot de passe :\n\n"
                + link + "\n\n"
                + "Ce lien expire dans 1 heure. Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n"
                + "Cordialement,\nCERT - Calibration Hub";

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}