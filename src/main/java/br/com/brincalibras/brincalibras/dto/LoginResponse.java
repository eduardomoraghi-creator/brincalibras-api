package br.com.brincalibras.brincalibras.dto;

public record LoginResponse(
    Long id,
    String nome,
    String email,
    String role,
    String message
) {}
