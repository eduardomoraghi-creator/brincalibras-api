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
import br.com.brincalibras.brincalibras.security.JwtService;
import br.com.brincalibras.brincalibras.dto.UserPasswordUpdateRequest;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * CREATE
     */
    public UserResponse create(UserCreateRequest req) {
        if (!req.senha().equals(req.confirmaSenha())) {
            throw new ConflictException("As senhas não coincidem");
        }

        if (userRepository.existsByEmail(req.email())) {
            throw new ConflictException("Email já cadastrado");
        }

        User user = User.builder()
                .nome(req.nome())
                .email(req.email())
                .senha(passwordEncoder.encode(req.senha()))
                .role("USER")
                .build();

        User saved = userRepository.save(user);
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
     * UPDATE
     */
    public UserResponse update(Long id, UserUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + id + ")"));

        if (userRepository.existsByEmailAndIdNot(req.email(), id)) {
            throw new ConflictException("Email já cadastrado por outro usuário");
        }

        user.setNome(req.nome());
        user.setEmail(req.email());

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse updatePassword(Long id, UserPasswordUpdateRequest req) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado (id=" + id + ")"));

        if (!req.senha().equals(req.confirmaSenha())) {
            throw new ConflictException("As senhas não coincidem");
        }

        user.setSenha(passwordEncoder.encode(req.senha()));

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
     * Mapper
     */
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * LOGIN COM JWT
     */
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new UnauthorizedException("E-mail ou senha inválidos"));

        boolean senhaCorreta = passwordEncoder.matches(req.senha(), user.getSenha());

        if (!senhaCorreta) {
            throw new UnauthorizedException("E-mail ou senha inválidos");
        }

        String token = jwtService.gerarToken(user.getEmail(), user.getRole());

        return new LoginResponse(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}