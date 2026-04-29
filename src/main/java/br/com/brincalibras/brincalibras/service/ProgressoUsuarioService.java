package br.com.brincalibras.brincalibras.service;

import br.com.brincalibras.brincalibras.dto.ProgressoCreateRequest;
import br.com.brincalibras.brincalibras.dto.ProgressoResponse;
import br.com.brincalibras.brincalibras.dto.RankingResponse;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.model.Licao;
import br.com.brincalibras.brincalibras.model.ProgressoUsuario;
import br.com.brincalibras.brincalibras.model.User;
import br.com.brincalibras.brincalibras.repository.LicaoRepository;
import br.com.brincalibras.brincalibras.repository.ProgressoUsuarioRepository;
import br.com.brincalibras.brincalibras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgressoUsuarioService {

    private final ProgressoUsuarioRepository progressoUsuarioRepository;
    private final UserRepository userRepository;
    private final LicaoRepository licaoRepository;

    public ProgressoResponse save(ProgressoCreateRequest req) {
        User user = userRepository.findById(req.userId())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + req.userId() + ")"));

        Licao licao = licaoRepository.findById(req.licaoId())
                .orElseThrow(() -> new NotFoundException("Lição não encontrada (id=" + req.licaoId() + ")"));

        ProgressoUsuario progresso = progressoUsuarioRepository
                .findByUserIdAndLicaoId(req.userId(), req.licaoId())
                .orElse(
                        ProgressoUsuario.builder()
                                .user(user)
                                .licao(licao)
                                .build()
                );

        progresso.setConcluido(req.concluido());
        progresso.setPontuacao(req.pontuacao());

        ProgressoUsuario saved = progressoUsuarioRepository.save(progresso);
        return toResponse(saved);
    }

    public List<ProgressoResponse> listByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Usuário não encontrado (id=" + userId + ")");
        }

        return progressoUsuarioRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<RankingResponse> ranking() {
                List<User> usuarios = userRepository.findAll();
                List<Licao> licoes = licaoRepository.findAll();

                int totalLicoes = licoes.size();

                List<RankingResponse> rankingSemPosicao = usuarios.stream()
                        .map(usuario -> {
                                List<ProgressoUsuario> progressos =
                                        progressoUsuarioRepository.findByUserId(usuario.getId());

                                int licoesConcluidas = (int) progressos.stream()
                                        .filter(p -> Boolean.TRUE.equals(p.getConcluido()))
                                        .filter(p -> p.getPontuacao() != null && p.getPontuacao() >= 70)
                                        .count();

                                int pontuacaoTotal = progressos.stream()
                                        .mapToInt(p -> p.getPontuacao() != null ? p.getPontuacao() : 0)
                                        .sum();

                                int percentualGeral = totalLicoes > 0
                                        ? Math.round((licoesConcluidas * 100f) / totalLicoes)
                                        : 0;

                                int unidadesConcluidas = (int) progressos.stream()
                                        .filter(p -> Boolean.TRUE.equals(p.getConcluido()))
                                        .filter(p -> p.getPontuacao() != null && p.getPontuacao() >= 70)
                                        .filter(p -> p.getLicao().getTipo().name().equals("ATIVIDADE"))
                                        .filter(p -> p.getLicao().getOrdem() == 4)
                                        .count();

                                return new RankingResponse(
                                        0,
                                        usuario.getId(),
                                        usuario.getNome(),
                                        percentualGeral,
                                        unidadesConcluidas,
                                        licoesConcluidas,
                                        pontuacaoTotal
                                );
                        })
                        .sorted(
                                Comparator.comparing(RankingResponse::percentualGeral).reversed()
                                        .thenComparing(RankingResponse::unidadesConcluidas, Comparator.reverseOrder())
                                        .thenComparing(RankingResponse::licoesConcluidas, Comparator.reverseOrder())
                                        .thenComparing(RankingResponse::pontuacaoTotal, Comparator.reverseOrder())
                        )
                        .collect(java.util.stream.Collectors.toList());

                for (int i = 0; i < rankingSemPosicao.size(); i++) {
                        RankingResponse item = rankingSemPosicao.get(i);

                        rankingSemPosicao.set(i, new RankingResponse(
                                i + 1,
                                item.userId(),
                                item.nome(),
                                item.percentualGeral(),
                                item.unidadesConcluidas(),
                                item.licoesConcluidas(),
                                item.pontuacaoTotal()
                        ));
                }

                return rankingSemPosicao;
        }

    private ProgressoResponse toResponse(ProgressoUsuario progresso) {
        return new ProgressoResponse(
                progresso.getId(),
                progresso.getUser().getId(),
                progresso.getUser().getNome(),
                progresso.getLicao().getId(),
                progresso.getLicao().getNome(),
                progresso.getConcluido(),
                progresso.getPontuacao()
        );
    }
}