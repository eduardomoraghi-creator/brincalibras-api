package br.com.brincalibras.brincalibras.exception;

/**
 * Usamos para conflito de dados (ex: email já cadastrado).
 * Vamos transformar isso em HTTP 409 no handler global.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
