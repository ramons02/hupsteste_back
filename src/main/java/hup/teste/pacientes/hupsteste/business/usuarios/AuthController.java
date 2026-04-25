package hup.teste.pacientes.hupsteste.business.usuarios;

import hup.teste.pacientes.hupsteste.business.usuarios.dto.AuthResponse;
import hup.teste.pacientes.hupsteste.business.usuarios.dto.LoginRequest;
import hup.teste.pacientes.hupsteste.business.usuarios.dto.RegistrarRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registrar")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistrarRequest request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}