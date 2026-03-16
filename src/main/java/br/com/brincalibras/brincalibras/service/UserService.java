package br.com.brincalibras.brincalibras.service;

import br.com.brincalibras.brincalibras.dto.LoginRequest;
import br.com.brincalibras.brincalibras.dto.LoginResponse;
import br.com.brincalibras.brincalibras.dto.UserCreateRequest;
import br.com.brincalibras.brincalibras.dto.UserResponse;
import br.com.brincalibras.brincalibras.dto.UserUpdateRequest;
import br.com.brincalibras.brincalibras.exception.ConflictException;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.exception.UnauthorizedException;
import br.com.brincalibras.brincalibras.model.User;
import br.com.brincalibras.brincalibras.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service: regra de negócio.
 * Controller só recebe e devolve; Service faz validações, regra e chama Repository.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // você já tem isso funcionando (hash)

    /**
     * CREATE
     */
    public UserResponse create(UserCreateRequest req) {
        // Regra: senha e confirmação devem ser iguais
        if (!req.senha().equals(req.confirmaSenha())) {
            throw new ConflictException("As senhas não coincidem");
        }

        // Regra: email não pode repetir
        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email já cadastrado");
        }

        User user = User.builder()
                .nome(req.nome())
                .email(req.email())
                .senha(passwordEncoder.encode(req.senha())) // hash da senha
                .role("USER")
                .build();

        User saved = userRepository.save(user);

        // retorna DTO sem senha
        return toResponse(saved);
    }

    /**
     * READ - listar todos
     */
    public List<UserResponse> list() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * READ - buscar por id
     */
    public UserResponse getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + id + ")"));

        return toResponse(user);
    }

    /**
     * UPDATE - por enquanto apenas nome e email
     */
    public UserResponse update(Long id, UserUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + id + ")"));

        // Se o email novo já existe em OUTRO usuário -> conflito
        if (userRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new ConflictException("Email já cadastrado por outro usuário");
        }

        user.setNome(req.nome());
        user.setEmail(req.email());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    /**
     * DELETE
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado (id=" + id + ")");
        }
        userRepository.deleteById(id);
    }

    /**
     * Mapper (conversão Entity -> DTO)
     * Mantém a API segura e sem dados sensíveis.
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole()
        );
    }

    public LoginResponse login(LoginRequest req) {
    User user = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos"));

    boolean senhaCorreta = passwordEncoder.matches(req.senha(), user.getSenha());

    if (!senhaCorreta) {
        throw new UnauthorizedException("E-mail ou senha inválidos");
    }

    return new LoginResponse(
            user.getId(),
            user.getNome(),
            user.getEmail(),
            user.getRole(),
            "Login realizado com sucesso"
    );
}
}
