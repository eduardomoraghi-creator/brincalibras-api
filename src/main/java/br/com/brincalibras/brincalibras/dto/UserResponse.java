package br.com.brincalibras.brincalibras.dto;

/**
 * DTO de resposta (o que a API devolve).
 * Objetivo: NUNCA devolver a senha (nem hash) para o cliente.
 */
public record UserResponse(
        Long id,
        String nome,
        String email,
        String role
) {}
