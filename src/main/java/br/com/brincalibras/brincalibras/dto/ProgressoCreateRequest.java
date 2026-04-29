package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.NotNull;

public record ProgressoCreateRequest(
        @NotNull(message = "userId é obrigatório")
        Long userId,

        @NotNull(message = "licaoId é obrigatório")
        Long licaoId,

        @NotNull(message = "concluido é obrigatório")
        Boolean concluido,

        @NotNull(message = "pontuacao é obrigatória")
        Integer pontuacao
) {}
