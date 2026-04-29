package br.com.brincalibras.brincalibras.service;

import br.com.brincalibras.brincalibras.dto.UnidadeCreateRequest;
import br.com.brincalibras.brincalibras.dto.UnidadeResponse;
import br.com.brincalibras.brincalibras.dto.UnidadeUpdateRequest;
import br.com.brincalibras.brincalibras.exception.ConflictException;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.model.Unidade;
import br.com.brincalibras.brincalibras.repository.LicaoRepository;
import br.com.brincalibras.brincalibras.repository.UnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;
    private final LicaoRepository licaoRepository;

    public UnidadeResponse create(UnidadeCreateRequest req) {
        Unidade unidade = Unidade.builder()
                .nome(req.nome())
                .descricao(req.descricao())
                .build();

        Unidade saved = unidadeRepository.save(unidade);
        return toResponse(saved);
    }

    public List<UnidadeResponse> list() {
        return unidadeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UnidadeResponse getById(Long id) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada (id=" + id + ")"));

        return toResponse(unidade);
    }

    public UnidadeResponse update(Long id, UnidadeUpdateRequest req) {
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Unidade não encontrada (id=" + id + ")"));

        unidade.setNome(req.nome());
        unidade.setDescricao(req.descricao());

        Unidade saved = unidadeRepository.save(unidade);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!unidadeRepository.existsById(id)) {
            throw new NotFoundException("Unidade não encontrada (id=" + id + ")");
        }

        if (licaoRepository.existsByUnidadeId(id)) {
            throw new ConflictException("Não é possível excluir a unidade, pois existem lições vinculadas a ela");
        }

        unidadeRepository.deleteById(id);
    }

    private UnidadeResponse toResponse(Unidade unidade) {
        return new UnidadeResponse(
                unidade.getId(),
                unidade.getNome(),
                unidade.getDescricao()
        );
    }
}
