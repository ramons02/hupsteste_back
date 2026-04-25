package hup.teste.pacientes.hupsteste.business.usuarios;

import hup.teste.pacientes.hupsteste.business.usuarios.dto.AuthResponse;
import hup.teste.pacientes.hupsteste.business.usuarios.dto.LoginRequest;
import hup.teste.pacientes.hupsteste.business.usuarios.dto.RegistrarRequest;
import hup.teste.pacientes.hupsteste.core.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse registrar(RegistrarRequest request) {
        if (usuarioRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));

        // ADICIONE ESTA LINHA:
        // (Certifique-se de que o nome do enum é Role.USER ou conforme definido no seu projeto)
        usuario.setRole(Role.USER);

        // Se o campo data_hora_criacao também for NOT NULL e não tiver @PrePersist:
        // usuario.setDataHoraCriacao(LocalDateTime.now());

        usuarioRepository.save(usuario);

        return new AuthResponse(
                jwtService.generateToken(usuario),
                usuario.getEmail(),
                usuario.getNome()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.senha())
        );
        Usuario usuario = usuarioRepository.findByEmail(request.email()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(usuario), usuario.getEmail(), usuario.getNome());
    }
}