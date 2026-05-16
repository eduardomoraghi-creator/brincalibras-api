package br.com.brincalibras.brincalibras.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(destinatario);
        mensagem.setSubject("Recuperação de senha - BrincaLibras");
        mensagem.setText(
                "Olá!\n\n" +
                "Recebemos uma solicitação para recuperar a senha da sua conta no BrincaLibras.\n\n" +
                "Seu código de recuperação é: " + codigo + "\n\n" +
                "Este código é válido por 15 minutos.\n\n" +
                "Se você não solicitou essa recuperação, ignore este e-mail.\n\n" +
                "Equipe BrincaLibras"
        );

        mailSender.send(mensagem);
    }
}
