package br.com.brincalibras.brincalibras.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopGamerPontuacaoRequest(
        @NotBlank(message = "Jogo é obrigatório")
        String jogo,

        @NotNull(message = "Pontuação é obrigatória")
        @Min(value = 0, message = "Pontuação deve ser maior ou igual a zero")
        Integer pontuacao
) {}