package br.com.brincalibras.brincalibras.dto;

public record TopGamerUsuarioResponse(
        Long userId,
        String nome,
        Integer pontuacaoTotal,
        Integer melhorPontuacao,
        Integer totalPartidas,
        Integer forca,
        Integer memoria,
        Integer pares,
        Integer soletrando,
        Integer montarSinalLibras,
        Integer sinalRush,
        String ultimoJogo,
        String atualizadoEm
) {}
