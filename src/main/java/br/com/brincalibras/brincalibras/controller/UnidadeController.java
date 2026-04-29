package br.com.brincalibras.brincalibras.controller;

import br.com.brincalibras.brincalibras.dto.UnidadeCreateRequest;
import br.com.brincalibras.brincalibras.dto.UnidadeResponse;
import br.com.brincalibras.brincalibras.dto.UnidadeUpdateRequest;
import br.com.brincalibras.brincalibras.service.UnidadeService;
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
@RequestMapping("/unidades")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService unidadeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UnidadeResponse create(@Valid @RequestBody UnidadeCreateRequest req) {
        return unidadeService.create(req);
    }

    @GetMapping
    public List<UnidadeResponse> list() {
        return unidadeService.list();
    }

    @GetMapping("/{id}")
    public UnidadeResponse getById(@PathVariable Long id) {
        return unidadeService.getById(id);
    }

    @PutMapping("/{id}")
    public UnidadeResponse update(@PathVariable Long id,
                                  @Valid @RequestBody UnidadeUpdateRequest req) {
        return unidadeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        unidadeService.delete(id);
    }
}