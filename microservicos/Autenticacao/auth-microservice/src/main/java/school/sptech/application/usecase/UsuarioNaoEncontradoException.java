package school.sptech.application.usecase;

public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException() { super("Usuário não encontrado"); }
}

