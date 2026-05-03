package br.com.brincalibras.brincalibras.dto;

public record TopGamerRankingResponse(
        Integer posicao,
        Long userId,
        String nome,

        Integer pontuacao,
        Integer pontuacaoTotal,
        Integer melhorPontuacao,
        Integer totalPartidas,

        String ultimoJogo,
        String atualizadoEm
) {}