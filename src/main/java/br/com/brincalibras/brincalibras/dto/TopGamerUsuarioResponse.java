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
        String ultimoJogo,
        String atualizadoEm
) {}