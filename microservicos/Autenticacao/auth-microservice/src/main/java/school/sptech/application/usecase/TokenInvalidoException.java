// application/usecase/TokenInvalidoException.java
package school.sptech.application.usecase;

public class TokenInvalidoException extends RuntimeException {
    public TokenInvalidoException() { super("Token inválido ou expirado"); }
}