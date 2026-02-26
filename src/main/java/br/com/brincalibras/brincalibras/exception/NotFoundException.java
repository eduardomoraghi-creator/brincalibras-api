package br.com.brincalibras.brincalibras.exception;

/**
 * Usamos para quando algo não existe no banco (ex: usuário não encontrado).
 * Vamos transformar isso em HTTP 404 no handler global.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
