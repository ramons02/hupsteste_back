package hup.teste.pacientes.hupsteste.business.pacientes;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import hup.teste.pacientes.hupsteste.business.pacientes.dto.PacienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {
    
    private static final Logger logger = LoggerFactory.getLogger(PacienteController.class);
    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<PacienteDTO> create(@RequestBody @Valid PacienteDTO dto) {
        logger.info("POST /api/v1/pacientes - Criando novo paciente");
        PacienteDTO created = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<PacienteDTO>> findAll() {
        logger.info("GET /api/v1/pacientes - Listando pacientes");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteDTO> findById(@PathVariable UUID id) {
        logger.info("GET /api/v1/pacientes/{} - Buscando paciente", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteDTO> update(@PathVariable UUID id, @RequestBody @Valid PacienteDTO dto) {
        logger.info("PUT /api/v1/pacientes/{} - Atualizando paciente", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("DELETE /api/v1/pacientes/{} - Deletando paciente", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
