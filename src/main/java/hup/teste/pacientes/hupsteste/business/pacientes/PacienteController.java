package hup.teste.pacientes.hupsteste.business.pacientes;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "http://localhost:4200")
public class PacienteController {
    
    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<Paciente> create(@RequestBody @Valid Paciente paciente) {
        return ResponseEntity.ok(service.save(paciente));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> update(@PathVariable UUID id, @RequestBody @Valid Paciente paciente) {
        return ResponseEntity.ok(service.update(id, paciente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
