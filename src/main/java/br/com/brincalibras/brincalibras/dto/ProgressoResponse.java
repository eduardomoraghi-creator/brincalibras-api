package br.com.brincalibras.brincalibras.dto;

public record ProgressoResponse(
        Long id,
        Long userId,
        String userNome,
        Long licaoId,
        String licaoNome,
        Boolean concluido,
        Integer pontuacao
) {}
