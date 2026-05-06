package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopGamerPontuacaoRequest(
        @NotBlank(message = "Jogo é obrigatório")
        String jogo,

        @NotNull(message = "Pontuação é obrigatória")
        Integer pontuacao
) {}