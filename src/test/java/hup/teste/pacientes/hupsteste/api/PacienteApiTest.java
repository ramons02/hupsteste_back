package hup.teste.pacientes.hupsteste.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PacienteApiTest {

    @Test
    public void testPacienteApiTestLoads() {
        assertTrue(true, "Teste de Paciente carregado com sucesso");
    }
}