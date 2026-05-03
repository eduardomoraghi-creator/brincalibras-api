package br.com.brincalibras.brincalibras.controller;

import br.com.brincalibras.brincalibras.dto.TopGamerPontuacaoRequest;
import br.com.brincalibras.brincalibras.dto.TopGamerRankingResponse;
import br.com.brincalibras.brincalibras.dto.TopGamerUsuarioResponse;
import br.com.brincalibras.brincalibras.exception.UnauthorizedException;
import br.com.brincalibras.brincalibras.model.User;
import br.com.brincalibras.brincalibras.service.TopGamerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {
        "http://localhost:8081",
        "https://brincalibras-mobile.onrender.com"
})
@RestController
@RequestMapping("/top-gamers")
@RequiredArgsConstructor
public class TopGamerController {

    private final TopGamerService topGamerService;

    @GetMapping("/ranking")
    public List<TopGamerRankingResponse> rankingTop10() {
        return topGamerService.rankingTop10();
    }

    @PostMapping("/pontuacao")
    @ResponseStatus(HttpStatus.CREATED)
    public TopGamerUsuarioResponse registrarPontuacao(
            @Valid @RequestBody TopGamerPontuacaoRequest req,
            Authentication authentication
    ) {
        User user = getUsuarioAutenticado(authentication);
        return topGamerService.registrarPontuacao(user.getId(), req);
    }

    @GetMapping("/me")
    public TopGamerUsuarioResponse meuTopGamer(Authentication authentication) {
        User user = getUsuarioAutenticado(authentication);
        return topGamerService.buscarPorUsuario(user.getId());
    }

    @GetMapping("/usuario/{userId}")
    public TopGamerUsuarioResponse buscarPorUsuario(@PathVariable Long userId) {
        return topGamerService.buscarPorUsuario(userId);
    }

    private User getUsuarioAutenticado(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new UnauthorizedException("Usuário não autenticado");
        }

        return user;
    }
}