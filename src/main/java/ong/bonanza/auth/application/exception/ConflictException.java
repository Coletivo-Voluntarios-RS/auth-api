package ong.bonanza.auth.application.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(String descricao) {
        super(String.format("Conflito [%s]", descricao));
    }

}
