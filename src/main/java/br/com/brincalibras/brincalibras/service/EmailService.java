package br.com.brincalibras.brincalibras.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void enviarCodigoRecuperacao(String destinatario, String codigo) {
        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        String body = """
        {
          "sender": {
            "name": "BrincaLibras",
            "email": "brincalibras@gmail.com"
          },
          "to": [
            {
              "email": "%s"
            }
          ],
          "subject": "Recuperação de senha - BrincaLibras",
          "htmlContent": "<h2>Recuperação de senha</h2><p>Olá!</p><p>Recebemos uma solicitação para recuperar a senha da sua conta no <strong>BrincaLibras</strong>.</p><p>Seu código de recuperação é:</p><h1>%s</h1><p>Este código é válido por 15 minutos.</p><p>Se você não solicitou essa recuperação, ignore este e-mail.</p><br><p>Equipe BrincaLibras</p>"
        }
        """.formatted(destinatario, codigo);

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    request,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Erro ao enviar e-mail pela Brevo: " + response.getBody());
            }
        } catch (Exception e) {
            System.out.println("ERRO AO ENVIAR EMAIL PELA BREVO:");
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
