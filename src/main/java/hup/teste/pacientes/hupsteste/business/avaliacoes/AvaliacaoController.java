package hup.teste.pacientes.hupsteste.business.avaliacoes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import hup.teste.pacientes.hupsteste.business.avaliacoes.dto.AvaliacaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/v1/avaliacoes")
public class AvaliacaoController {

    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoController.class);
    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<AvaliacaoDTO> create(@RequestBody @Valid AvaliacaoDTO dto) {
        logger.info("POST /api/v1/avaliacoes - Criando nova avaliação para paciente: {}", dto.pacienteId());
        AvaliacaoDTO created = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AvaliacaoDTO>> findAll() {
        logger.info("GET /api/v1/avaliacoes - Listando avaliações");
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> findById(@PathVariable UUID id) {
        logger.info("GET /api/v1/avaliacoes/{} - Buscando avaliação", id);
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> update(@PathVariable UUID id, @RequestBody @Valid AvaliacaoDTO dto) {
        logger.info("PUT /api/v1/avaliacoes/{} - Atualizando avaliação", id);
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        logger.info("DELETE /api/v1/avaliacoes/{} - Deletando avaliação", id);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable UUID id) {
        logger.info("GET /api/v1/avaliacoes/{}/pdf - Gerando PDF", id);
        byte[] pdfBytes = service.gerarPdf(id);
        
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio_avaliacao_" + id + ".pdf\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                .body(pdfBytes);
    }
}
