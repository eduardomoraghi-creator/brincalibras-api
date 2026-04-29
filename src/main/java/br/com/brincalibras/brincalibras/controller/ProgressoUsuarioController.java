package br.com.brincalibras.brincalibras.controller;

import br.com.brincalibras.brincalibras.dto.ProgressoCreateRequest;
import br.com.brincalibras.brincalibras.dto.ProgressoResponse;
import br.com.brincalibras.brincalibras.dto.RankingResponse;
import br.com.brincalibras.brincalibras.service.ProgressoUsuarioService;
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
@RequestMapping("/progresso")
@RequiredArgsConstructor
public class ProgressoUsuarioController {

    private final ProgressoUsuarioService progressoUsuarioService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProgressoResponse save(@Valid @RequestBody ProgressoCreateRequest req) {
        return progressoUsuarioService.save(req);
    }

    @GetMapping("/usuario/{userId}")
    public List<ProgressoResponse> listByUser(@PathVariable Long userId) {
        return progressoUsuarioService.listByUser(userId);
    }

    @GetMapping("/ranking")
    public List<RankingResponse> ranking() {
        return progressoUsuarioService.ranking();
    }
}