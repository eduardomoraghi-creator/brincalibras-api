package br.com.brincalibras.brincalibras.controller;

import br.com.brincalibras.brincalibras.dto.LicaoCreateRequest;
import br.com.brincalibras.brincalibras.dto.LicaoResponse;
import br.com.brincalibras.brincalibras.dto.LicaoUpdateRequest;
import br.com.brincalibras.brincalibras.service.LicaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8081",
        "https://brincalibras-mobile.onrender.com"
})
@RestController
@RequestMapping("/licoes")
@RequiredArgsConstructor
public class LicaoController {

    private final LicaoService licaoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LicaoResponse create(@Valid @RequestBody LicaoCreateRequest req) {
        return licaoService.create(req);
    }

    @GetMapping
    public List<LicaoResponse> list() {
        return licaoService.list();
    }

    @GetMapping("/{id}")
    public LicaoResponse getById(@PathVariable Long id) {
        return licaoService.getById(id);
    }

    @GetMapping("/unidade/{unidadeId}")
    public List<LicaoResponse> listByUnidade(@PathVariable Long unidadeId) {
        return licaoService.listByUnidade(unidadeId);
    }

    @PutMapping("/{id}")
    public LicaoResponse update(@PathVariable Long id,
                                @Valid @RequestBody LicaoUpdateRequest req) {
        return licaoService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        licaoService.delete(id);
    }
}