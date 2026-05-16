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
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();

            mensagem.setFrom("brincalibras@gmail.com");
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
        } catch (Exception e) {
            System.out.println("ERRO AO ENVIAR EMAIL:");
            System.out.println(e.getClass().getName());
            System.out.println(e.getMessage());

            if (e.getCause() != null) {
                System.out.println("CAUSA:");
                System.out.println(e.getCause().getClass().getName());
                System.out.println(e.getCause().getMessage());
            }

            throw e;
        }
    }
}
