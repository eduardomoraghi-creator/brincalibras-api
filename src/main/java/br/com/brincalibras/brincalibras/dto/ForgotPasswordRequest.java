package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "Digite um e-mail válido")
        String email
) {
}
