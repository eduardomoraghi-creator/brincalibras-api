package br.com.brincalibras.brincalibras.handler;

import br.com.brincalibras.brincalibras.exception.ConflictException;
import br.com.brincalibras.brincalibras.exception.NotFoundException;
import br.com.brincalibras.brincalibras.exception.UnauthorizedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Intercepta exceções da aplicação e devolve respostas HTTP padronizadas.
 * Ajuda MUITO no Swagger/Postman, porque você vê claramente o erro e o status.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 404 - Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(defaultBody(404, ex.getMessage()));
    }

    /**
     * 409 - Conflict (ex: email já cadastrado)
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        Map<String, Object> body = defaultBody(409, ex.getMessage());
        Map<String, String> fields = new HashMap<>();

        if ("Email já cadastrado".equals(ex.getMessage())) {
            fields.put("email", ex.getMessage());
        } else if ("As senhas não coincidem".equals(ex.getMessage())) {
            fields.put("confirmaSenha", ex.getMessage());
        }

        body.put("fields", fields);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * 400 - Bad Request (validação @Valid falhou)
     * Ex: nome em branco, email inválido, etc.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fields.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = defaultBody(400, "Dados inválidos");
        body.put("fields", fields);

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(defaultBody(401, ex.getMessage()));
    }

    /**
     * Fallback: qualquer erro não tratado vira 500.
     * (Não é obrigatório, mas ajuda a não ficar “cru” em runtime)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = defaultBody(500, "Erro interno");
        body.put("details", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> defaultBody(int status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status);
        body.put("message", message);
        return body;
    }
}
