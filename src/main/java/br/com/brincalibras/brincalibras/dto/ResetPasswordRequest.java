package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Código é obrigatório")
        String codigo,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
        String novaSenha,

        @NotBlank(message = "Confirmação de senha é obrigatória")
        String confirmaSenha
) {
}
