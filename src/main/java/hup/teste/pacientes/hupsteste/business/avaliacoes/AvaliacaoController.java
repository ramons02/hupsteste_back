package hup.teste.pacientes.hupsteste.business.avaliacoes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }
    
    @PostMapping
    public ResponseEntity<Avaliacao> create(@RequestBody @Valid Avaliacao avaliacao) {
        return ResponseEntity.ok(service.save(avaliacao));
    }

    @GetMapping
    public ResponseEntity<List<Avaliacao>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avaliacao> update(@PathVariable UUID id, @RequestBody @Valid Avaliacao avaliacao) {
        return ResponseEntity.ok(service.update(id, avaliacao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable UUID id) {
        Avaliacao avaliacao = service.findById(id);
        byte[] pdfBytes = service.gerarPdf(avaliacao);
        
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"relatorio_avaliacao_" + id + ".pdf\"")
                .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                .body(pdfBytes);
    }
}
