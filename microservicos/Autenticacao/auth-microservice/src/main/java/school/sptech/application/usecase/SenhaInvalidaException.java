package school.sptech.application.usecase;

public class SenhaInvalidaException extends RuntimeException {
    public SenhaInvalidaException() { super("Senha inválida"); }
}
