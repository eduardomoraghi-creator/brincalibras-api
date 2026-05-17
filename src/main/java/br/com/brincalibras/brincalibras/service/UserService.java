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
import br.com.brincalibras.brincalibras.repository.ProgressoUsuarioRepository;
import br.com.brincalibras.brincalibras.repository.TopGamerRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.brincalibras.brincalibras.dto.ForgotPasswordRequest;
import br.com.brincalibras.brincalibras.dto.ResetPasswordRequest;
import br.com.brincalibras.brincalibras.model.PasswordResetCode;
import br.com.brincalibras.brincalibras.repository.PasswordResetCodeRepository;

import java.time.LocalDateTime;
import java.util.Random;

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
    private final ProgressoUsuarioRepository progressoUsuarioRepository;
    private final TopGamerRepository topGamerRepository;
    private final PasswordResetCodeRepository passwordResetCodeRepository;
    private final EmailService emailService;

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
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Usuário não encontrado (id=" + id + ")");
        }

        passwordResetCodeRepository.deleteByUserId(id);
        progressoUsuarioRepository.deleteByUserId(id);
        topGamerRepository.deleteByUserId(id);

        userRepository.deleteById(id);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new NotFoundException("E-mail não encontrado"));

        String codigo = String.format("%06d", new Random().nextInt(999999));

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .codigo(codigo)
                .expiracao(LocalDateTime.now().plusMinutes(15))
                .utilizado(false)
                .user(user)
                .build();

        passwordResetCodeRepository.save(resetCode);

        emailService.enviarCodigoRecuperacao(user.getEmail(), codigo);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        if (!req.novaSenha().equals(req.confirmaSenha())) {
            throw new ConflictException("As senhas não coincidem");
        }

        PasswordResetCode resetCode = passwordResetCodeRepository
                .findTopByUserEmailAndCodigoAndUtilizadoFalseOrderByIdDesc(
                        req.email(),
                        req.codigo()
                )
                .orElseThrow(() -> new UnauthorizedException("Código inválido"));

        if (resetCode.getExpiracao().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Código expirado");
        }

        User user = resetCode.getUser();

        user.setSenha(passwordEncoder.encode(req.novaSenha()));
        userRepository.save(user);

        resetCode.setUtilizado(true);
        passwordResetCodeRepository.save(resetCode);
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