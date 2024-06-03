package ong.bonanza.auth.application.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String para) {
        super(String.format("Sem permissão para [%s]", para));
    }

}
