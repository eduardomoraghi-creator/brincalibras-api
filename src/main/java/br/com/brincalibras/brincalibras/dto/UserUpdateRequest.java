package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de atualização.
 * Por enquanto, vamos permitir atualizar apenas nome e email.
 * Senha a gente faz depois em um endpoint separado.
 */
public record UserUpdateRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email
) {}