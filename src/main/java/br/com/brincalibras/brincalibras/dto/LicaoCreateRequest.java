package br.com.brincalibras.brincalibras.dto;

import br.com.brincalibras.brincalibras.model.TipoLicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LicaoCreateRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Conteúdo é obrigatório")
        @Size(max = 1000, message = "Conteúdo deve ter no máximo 1000 caracteres")
        String conteudo,

        @NotNull(message = "Ordem é obrigatória")
        Integer ordem,

        @NotNull(message = "Tipo é obrigatório")
        TipoLicao tipo,

        @NotNull(message = "unidadeId é obrigatório")
        Long unidadeId
) {}

