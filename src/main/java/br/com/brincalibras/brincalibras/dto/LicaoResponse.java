package br.com.brincalibras.brincalibras.dto;

public record LicaoResponse(
        Long id,
        String nome,
        String conteudo,
        Integer ordem,
        String tipo,
        Long unidadeId,
        String unidadeNome
) {}
