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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TopGamerService {

    private final TopGamerRepository topGamerRepository;
    private final UserRepository userRepository;

    @Transactional
    public TopGamerUsuarioResponse registrarPontuacao(Long userId, TopGamerPontuacaoRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + userId + ")"));

        TopGamer topGamer = topGamerRepository.findByUserId(userId)
                .orElseGet(() -> TopGamer.builder()
                        .user(user)
                        .pontuacaoTotal(0)
                        .melhorPontuacao(0)
                        .totalPartidas(0)
                        .forca(0)
                        .memoria(0)
                        .pares(0)
                        .soletrando(0)
                        .montarSinalLibras(0)
                        .build()
                );

        int pontos = req.pontuacao() != null ? req.pontuacao() : 0;
        String jogoNormalizado = normalizarJogo(req.jogo());

        somarPontuacaoDoJogo(topGamer, jogoNormalizado, pontos);

        topGamer.setPontuacaoTotal(valorOuZero(topGamer.getPontuacaoTotal()) + pontos);
        topGamer.setMelhorPontuacao(Math.max(valorOuZero(topGamer.getMelhorPontuacao()), pontos));
        topGamer.setTotalPartidas(valorOuZero(topGamer.getTotalPartidas()) + 1);
        topGamer.setUltimoJogo(jogoNormalizado);
        topGamer.setAtualizadoEm(LocalDateTime.now());

        TopGamer saved = topGamerRepository.save(topGamer);

        return toUsuarioResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TopGamerRankingResponse> rankingTop10() {
        List<TopGamer> top10 =
                topGamerRepository
                        .findTop10ByPontuacaoTotalGreaterThanOrderByPontuacaoTotalDescMelhorPontuacaoDescTotalPartidasAscIdAsc(0);

        return IntStream.range(0, top10.size())
                .mapToObj(index -> toRankingResponse(top10.get(index), index + 1))
                .toList();
    }

    @Transactional(readOnly = true)
    public TopGamerUsuarioResponse buscarPorUsuario(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + userId + ")"));

        TopGamer topGamer = topGamerRepository.findByUserId(userId)
                .orElseGet(() -> TopGamer.builder()
                        .user(user)
                        .pontuacaoTotal(0)
                        .melhorPontuacao(0)
                        .totalPartidas(0)
                        .forca(0)
                        .memoria(0)
                        .pares(0)
                        .soletrando(0)
                        .montarSinalLibras(0)
                        .build()
                );

        return toUsuarioResponse(topGamer);
    }

    private void somarPontuacaoDoJogo(TopGamer topGamer, String jogo, int pontos) {
        switch (jogo) {
            case "forca" ->
                    topGamer.setForca(valorOuZero(topGamer.getForca()) + pontos);

            case "memoria" ->
                    topGamer.setMemoria(valorOuZero(topGamer.getMemoria()) + pontos);

            case "pares" ->
                    topGamer.setPares(valorOuZero(topGamer.getPares()) + pontos);

            case "soletrando" ->
                    topGamer.setSoletrando(valorOuZero(topGamer.getSoletrando()) + pontos);

            case "montar_sinal_libras" ->
                    topGamer.setMontarSinalLibras(valorOuZero(topGamer.getMontarSinalLibras()) + pontos);

            default ->
                    throw new IllegalArgumentException("Jogo inválido: " + jogo);
        }
    }

    private String normalizarJogo(String jogo) {
        if (jogo == null) {
            return "";
        }

        String normalizado = jogo
                .trim()
                .toLowerCase()
                .replace("í", "i")
                .replace("ó", "o")
                .replace("á", "a")
                .replace("ã", "a")
                .replace("ç", "c")
                .replace(" ", "_")
                .replace("-", "_");

        return switch (normalizado) {
            case "forca" -> "forca";
            case "memoria", "memoria_libras" -> "memoria";
            case "pares", "pares_drag_and_drop", "pares_draganddrop" -> "pares";
            case "soletrando" -> "soletrando";
            case "montar_sinal_libras", "montar_libras", "montarsinallibras" -> "montar_sinal_libras";
            default -> normalizado;
        };
    }

    private TopGamerRankingResponse toRankingResponse(TopGamer topGamer, int posicao) {
        return new TopGamerRankingResponse(
                posicao,
                topGamer.getUser().getId(),
                topGamer.getUser().getNome(),
                valorOuZero(topGamer.getPontuacaoTotal()),
                valorOuZero(topGamer.getPontuacaoTotal()),
                valorOuZero(topGamer.getMelhorPontuacao()),
                valorOuZero(topGamer.getTotalPartidas()),
                topGamer.getUltimoJogo(),
                topGamer.getAtualizadoEm() != null ? topGamer.getAtualizadoEm().toString() : null
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
                topGamer.getUltimoJogo(),
                topGamer.getAtualizadoEm() != null ? topGamer.getAtualizadoEm().toString() : null
        );
    }

    private int valorOuZero(Integer valor) {
        return valor != null ? valor : 0;
    }
}