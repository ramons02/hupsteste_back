package hup.teste.pacientes.hupsteste.business.avaliacoes;

import hup.teste.pacientes.hupsteste.business.pacientes.RelatorioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
    private final RelatorioService relatorioService;

    public AvaliacaoController(AvaliacaoService service, RelatorioService relatorioService) {
        this.service = service;
        this.relatorioService = relatorioService;
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

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AvaliacaoDTO>> findByPacienteId(@PathVariable UUID pacienteId) {
        logger.info("GET /api/v1/avaliacoes/paciente/{} - Buscando avaliações por paciente", pacienteId);
        return ResponseEntity.ok(service.findByPacienteId(pacienteId));
    }

    @GetMapping("/paciente/{pacienteId}/relatorio")
    public ResponseEntity<byte[]> gerarRelatorio(@PathVariable UUID pacienteId) {
        logger.info("GET /api/v1/avaliacoes/paciente/{}/relatorio - Gerando relatório evolutivo", pacienteId);
        byte[] pdf = relatorioService.gerarRelatorioEvolutivo(pacienteId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"relatorio-evolutivo-" + pacienteId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
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
        logger.info("GET /api/v1/avaliacoes/{}/pdf - Gerando PDF da avaliação", id);
        byte[] pdfBytes = service.gerarPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"relatorio_avaliacao_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}