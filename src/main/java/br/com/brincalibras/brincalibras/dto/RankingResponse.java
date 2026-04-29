package br.com.brincalibras.brincalibras.dto;

public record RankingResponse(
        Integer posicao,
        Long userId,
        String nome,
        Integer percentualGeral,
        Integer unidadesConcluidas,
        Integer licoesConcluidas,
        Integer pontuacaoTotal
) {}
