package br.com.brincalibras.brincalibras.service;

import br.com.brincalibras.brincalibras.dto.LicaoCreateRequest;
import br.com.brincalibras.brincalibras.dto.LicaoResponse;
import br.com.brincalibras.brincalibras.dto.LicaoUpdateRequest;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.model.Licao;
import br.com.brincalibras.brincalibras.model.Unidade;
import br.com.brincalibras.brincalibras.repository.LicaoRepository;
import br.com.brincalibras.brincalibras.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LicaoService {

    private final LicaoRepository licaoRepository;
    private final UnidadeRepository unidadeRepository;

    public LicaoResponse create(LicaoCreateRequest req) {
        Unidade unidade = unidadeRepository.findById(req.unidadeId())
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada (id=" + req.unidadeId() + ")"));

        Licao licao = Licao.builder()
                .nome(req.nome())
                .conteudo(req.conteudo())
                .ordem(req.ordem())
                .tipo(req.tipo())
                .unidade(unidade)
                .build();

        Licao saved = licaoRepository.save(licao);
        return toResponse(saved);
    }

    public List<LicaoResponse> list() {
        return licaoRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LicaoResponse getById(Long id) {
        Licao licao = licaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada (id=" + id + ")"));

        return toResponse(licao);
    }

    public List<LicaoResponse> listByUnidade(Long unidadeId) {
        if (!unidadeRepository.existsById(unidadeId)) {
            throw new NotFoundException("Unidade não encontrada (id=" + unidadeId + ")");
        }

        return licaoRepository.findByUnidadeIdOrderByOrdemAsc(unidadeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LicaoResponse update(Long id, LicaoUpdateRequest req) {
        Licao licao = licaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Lição não encontrada (id=" + id + ")"));

        Unidade unidade = unidadeRepository.findById(req.unidadeId())
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada (id=" + req.unidadeId() + ")"));

        licao.setNome(req.nome());
        licao.setConteudo(req.conteudo());
        licao.setOrdem(req.ordem());
        licao.setTipo(req.tipo());
        licao.setUnidade(unidade);

        Licao saved = licaoRepository.save(licao);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!licaoRepository.existsById(id)) {
            throw new NotFoundException("Lição não encontrada (id=" + id + ")");
        }

        licaoRepository.deleteById(id);
    }

    private LicaoResponse toResponse(Licao licao) {
        return new LicaoResponse(
                licao.getId(),
                licao.getNome(),
                licao.getConteudo(),
                licao.getOrdem(),
                licao.getTipo().name(),
                licao.getUnidade().getId(),
                licao.getUnidade().getNome()
        );
    }
}