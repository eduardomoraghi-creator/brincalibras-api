package br.com.brincalibras.brincalibras.service;

import br.com.brincalibras.brincalibras.dto.TopGamerPontuacaoRequest;
import br.com.brincalibras.brincalibras.dto.TopGamerRankingResponse;
import br.com.brincalibras.brincalibras.dto.TopGamerUsuarioResponse;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.model.TopGamer;
import br.com.brincalibras.brincalibras.model.User;
import br.com.brincalibras.brincalibras.repository.TopGamerRepository;
import br.com.brincalibras.brincalibras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TopGamerService {

    private static final String JOGO_FORCA = "forca";
    private static final String JOGO_MEMORIA = "memoria";
    private static final String JOGO_PARES = "pares";
    private static final String JOGO_SOLETRANDO = "soletrando";
    private static final String JOGO_MONTAR_SINAL_LIBRAS = "montar_sinal_libras";
    private static final String JOGO_SINAL_RUSH = "sinal_rush";

    private final TopGamerRepository topGamerRepository;
    private final UserRepository userRepository;

    @Transactional
    public TopGamerUsuarioResponse registrarPontuacao(
            Long userId,
            TopGamerPontuacaoRequest req
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + userId + ")"));

        TopGamer topGamer = topGamerRepository.findByUserIdForUpdate(userId)
                .orElseGet(() -> criarTopGamerZerado(user));

        corrigirCamposNulos(topGamer);

        int pontos = req.pontuacao() != null ? req.pontuacao() : 0;
        String jogoNormalizado = normalizarJogo(req.jogo());

        somarPontuacaoDoJogo(topGamer, jogoNormalizado, pontos);

        int novaPontuacaoTotal = somar(topGamer.getPontuacaoTotal(), pontos);

        topGamer.setPontuacaoTotal(novaPontuacaoTotal);
        topGamer.setMelhorPontuacao(
                Math.max(valorOuZero(topGamer.getMelhorPontuacao()), novaPontuacaoTotal)
        );

        /*
         * Não incrementamos totalPartidas aqui porque este endpoint é usado
         * para atualizar pontuação em tempo real a cada acerto, erro ou tempo esgotado.
         */

        topGamer.setUltimoJogo(jogoNormalizado);
        topGamer.setAtualizadoEm(LocalDateTime.now());

        TopGamer saved = topGamerRepository.save(topGamer);

        return toUsuarioResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TopGamerRankingResponse> rankingTop10() {
        List<TopGamer> top10 =
                topGamerRepository
                        .findTop10ByOrderByPontuacaoTotalDescMelhorPontuacaoDescTotalPartidasAscIdAsc();

        return IntStream.range(0, top10.size())
                .mapToObj(index -> toRankingResponse(top10.get(index), index + 1))
                .toList();
    }

    @Transactional(readOnly = true)
    public TopGamerUsuarioResponse buscarPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + userId + ")"));

        TopGamer topGamer = topGamerRepository.findByUserId(userId)
                .orElseGet(() -> criarTopGamerZerado(user));

        corrigirCamposNulos(topGamer);

        return toUsuarioResponse(topGamer);
    }

    private TopGamer criarTopGamerZerado(User user) {
        return TopGamer.builder()
                .user(user)
                .pontuacaoTotal(0)
                .melhorPontuacao(0)
                .totalPartidas(0)
                .forca(0)
                .memoria(0)
                .pares(0)
                .soletrando(0)
                .montarSinalLibras(0)
                .sinalRush(0)
                .build();
    }

    private void corrigirCamposNulos(TopGamer topGamer) {
        if (topGamer.getPontuacaoTotal() == null) {
            topGamer.setPontuacaoTotal(0);
        }

        if (topGamer.getMelhorPontuacao() == null) {
            topGamer.setMelhorPontuacao(0);
        }

        if (topGamer.getTotalPartidas() == null) {
            topGamer.setTotalPartidas(0);
        }

        if (topGamer.getForca() == null) {
            topGamer.setForca(0);
        }

        if (topGamer.getMemoria() == null) {
            topGamer.setMemoria(0);
        }

        if (topGamer.getPares() == null) {
            topGamer.setPares(0);
        }

        if (topGamer.getSoletrando() == null) {
            topGamer.setSoletrando(0);
        }

        if (topGamer.getMontarSinalLibras() == null) {
            topGamer.setMontarSinalLibras(0);
        }

        if (topGamer.getSinalRush() == null) {
            topGamer.setSinalRush(0);
        }
    }

    private void somarPontuacaoDoJogo(
            TopGamer topGamer,
            String jogo,
            int pontos
    ) {
        switch (jogo) {
            case JOGO_FORCA ->
                    topGamer.setForca(somar(topGamer.getForca(), pontos));

            case JOGO_MEMORIA ->
                    topGamer.setMemoria(somar(topGamer.getMemoria(), pontos));

            case JOGO_PARES ->
                    topGamer.setPares(somar(topGamer.getPares(), pontos));

            case JOGO_SOLETRANDO ->
                    topGamer.setSoletrando(somar(topGamer.getSoletrando(), pontos));

            case JOGO_MONTAR_SINAL_LIBRAS ->
                    topGamer.setMontarSinalLibras(somar(topGamer.getMontarSinalLibras(), pontos));

            case JOGO_SINAL_RUSH ->
                    topGamer.setSinalRush(somar(topGamer.getSinalRush(), pontos));

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Jogo inválido: " + jogo
            );
        }
    }

    private String normalizarJogo(String jogo) {
        if (jogo == null || jogo.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Jogo é obrigatório"
            );
        }

        String semAcentos = Normalizer
                .normalize(jogo.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        String normalizado = semAcentos
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        return switch (normalizado) {
            case "forca" -> JOGO_FORCA;

            case "memoria", "memoria_libras" -> JOGO_MEMORIA;

            case "pares", "pares_drag_and_drop", "pares_draganddrop" -> JOGO_PARES;

            case "soletrando" -> JOGO_SOLETRANDO;

            case "montar_sinal_libras", "montar_libras", "montarsinallibras" ->
                    JOGO_MONTAR_SINAL_LIBRAS;

            case "sinal_rush", "sinalrush", "rush", "rush_sinais", "corrida_sinais" ->
                    JOGO_SINAL_RUSH;

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Jogo inválido: " + jogo
            );
        };
    }

    private TopGamerRankingResponse toRankingResponse(
            TopGamer topGamer,
            int posicao
    ) {
        return new TopGamerRankingResponse(
                posicao,
                topGamer.getUser().getId(),
                topGamer.getUser().getNome(),
                valorOuZero(topGamer.getPontuacaoTotal()),
                valorOuZero(topGamer.getPontuacaoTotal()),
                valorOuZero(topGamer.getMelhorPontuacao()),
                valorOuZero(topGamer.getTotalPartidas()),
                topGamer.getUltimoJogo(),
                topGamer.getAtualizadoEm() != null
                        ? topGamer.getAtualizadoEm().toString()
                        : null
        );
    }

    private TopGamerUsuarioResponse toUsuarioResponse(TopGamer topGamer) {
        return new TopGamerUsuarioResponse(
                topGamer.getUser().getId(),
                topGamer.getUser().getNome(),
                valorOuZero(topGamer.getPontuacaoTotal()),
                valorOuZero(topGamer.getMelhorPontuacao()),
                valorOuZero(topGamer.getTotalPartidas()),
                valorOuZero(topGamer.getForca()),
                valorOuZero(topGamer.getMemoria()),
                valorOuZero(topGamer.getPares()),
                valorOuZero(topGamer.getSoletrando()),
                valorOuZero(topGamer.getMontarSinalLibras()),
                valorOuZero(topGamer.getSinalRush()),
                topGamer.getUltimoJogo(),
                topGamer.getAtualizadoEm() != null
                        ? topGamer.getAtualizadoEm().toString()
                        : null
        );
    }

    private int somar(Integer valorAtual, int pontos) {
        return valorOuZero(valorAtual) + pontos;
    }

    private int valorOuZero(Integer valor) {
        return valor != null ? valor : 0;
    }
}