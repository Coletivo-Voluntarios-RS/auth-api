package ong.bonanza.auth.application.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
        super("Você não está autenticado");
    }

}