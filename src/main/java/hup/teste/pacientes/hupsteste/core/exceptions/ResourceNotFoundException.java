package hup.teste.pacientes.hupsteste.core.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensagem) {
        super(mensagem);
    }

    public ResourceNotFoundException(String mensagem, Throwable cause) {
        super(mensagem, cause);
    }
}
