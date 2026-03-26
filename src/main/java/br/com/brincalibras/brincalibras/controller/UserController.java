package br.com.brincalibras.brincalibras.controller;

import br.com.brincalibras.brincalibras.dto.LoginRequest;
import br.com.brincalibras.brincalibras.dto.LoginResponse;
import br.com.brincalibras.brincalibras.dto.UserCreateRequest;
import br.com.brincalibras.brincalibras.dto.UserResponse;
import br.com.brincalibras.brincalibras.dto.UserUpdateRequest;
import br.com.brincalibras.brincalibras.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller: camada HTTP (endpoints).
 * Aqui a gente define rotas e status code.
 */

@CrossOrigin(origins = {
    "http://localhost:8081",
    "https://brincalibras-mobile.onrender.com"
})
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor


public class UserController {

    private final UserService userService;

    /**
     * POST /users
     * Cria um usuário.
     * Retorna 201 Created.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserCreateRequest req) {
        return userService.create(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest req) {
        return userService.login(req);
    }
    

    /**
     * GET /users
     * Lista usuários.
     */
    @GetMapping
    public List<UserResponse> list() {
        return userService.list();
    }

    /**
     * GET /users/{id}
     * Busca usuário por id.
     */
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * PUT /users/{id}
     * Atualiza nome e email.
     */
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UserUpdateRequest req) {
        return userService.update(id, req);
    }

    /**
     * DELETE /users/{id}
     * Deleta um usuário.
     * Retorna 204 No Content (sem corpo).
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    

}