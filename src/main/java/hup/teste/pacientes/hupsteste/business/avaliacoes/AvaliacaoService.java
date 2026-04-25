package hup.teste.pacientes.hupsteste.business.avaliacoes;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import hup.teste.pacientes.hupsteste.core.exceptions.ResourceNotFoundException;
import hup.teste.pacientes.hupsteste.business.avaliacoes.dto.AvaliacaoDTO;
import hup.teste.pacientes.hupsteste.business.pacientes.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AvaliacaoService {

    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoService.class);
    private static final DateTimeFormatter DTF_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private PacienteRepository pacienteRepository;

    private final AvaliacaoRepository repository;

    public AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    private double calcularLsi(double v1, double v2) {
        if (v1 <= 0 || v2 <= 0) return 0.0;
        return (Math.min(v1, v2) / Math.max(v1, v2)) * 100;
    }

    private AvaliacaoDTO mapToDTO(Avaliacao avaliacao) {
        String dataAvaliacao = avaliacao.getDataHoraCriacao() != null
                ? avaliacao.getDataHoraCriacao().format(DTF_DATA)
                : null;
        return new AvaliacaoDTO(
                avaliacao.getId(),
                avaliacao.getPaciente().getId(),
                dataAvaliacao,
                avaliacao.getSingleHopDireita(),
                avaliacao.getSingleHopEsquerda(),
                avaliacao.getTripleHopDireita(),
                avaliacao.getTripleHopEsquerda(),
                avaliacao.getCrossoverHopDireita(),
                avaliacao.getCrossoverHopEsquerda(),
                avaliacao.getSixMeterDireita(),
                avaliacao.getSixMeterEsquerda()
        );
    }

    public AvaliacaoDTO save(AvaliacaoDTO dto) {
        logger.info("Criando nova avaliação para paciente: {}", dto.pacienteId());

        var paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + dto.pacienteId() + " não encontrado"));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPaciente(paciente);
        avaliacao.setSingleHopDireita(dto.singleHopDireita());
        avaliacao.setSingleHopEsquerda(dto.singleHopEsquerda());
        avaliacao.setTripleHopDireita(dto.tripleHopDireita());
        avaliacao.setTripleHopEsquerda(dto.tripleHopEsquerda());
        avaliacao.setCrossoverHopDireita(dto.crossoverHopDireita());
        avaliacao.setCrossoverHopEsquerda(dto.crossoverHopEsquerda());
        avaliacao.setSixMeterDireita(dto.sixMeterDireita());
        avaliacao.setSixMeterEsquerda(dto.sixMeterEsquerda());

        Avaliacao saved = repository.save(avaliacao);
        logger.info("Avaliação criada com sucesso. ID: {}", saved.getId());

        return mapToDTO(saved);
    }

    public List<AvaliacaoDTO> findAll() {
        logger.info("Listando todas as avaliações");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvaliacaoDTO findById(UUID id) {
        logger.info("Buscando avaliação por ID: {}", id);
        Avaliacao avaliacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação com ID " + id + " não encontrada"));
        return mapToDTO(avaliacao);
    }

    public List<AvaliacaoDTO> findByPacienteId(UUID pacienteId) {
        logger.info("Buscando avaliações por paciente: {}", pacienteId);
        return repository.findByPacienteIdOrderByDataHoraCriacaoAsc(pacienteId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AvaliacaoDTO update(UUID id, AvaliacaoDTO dto) {
        logger.info("Atualizando avaliação com ID: {}", id);

        Avaliacao avaliacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação com ID " + id + " não encontrada"));

        if (dto.pacienteId() != null) {
            var paciente = pacienteRepository.findById(dto.pacienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + dto.pacienteId() + " não encontrado"));
            avaliacao.setPaciente(paciente);
        }

        avaliacao.setSingleHopDireita(dto.singleHopDireita());
        avaliacao.setSingleHopEsquerda(dto.singleHopEsquerda());
        avaliacao.setTripleHopDireita(dto.tripleHopDireita());
        avaliacao.setTripleHopEsquerda(dto.tripleHopEsquerda());
        avaliacao.setCrossoverHopDireita(dto.crossoverHopDireita());
        avaliacao.setCrossoverHopEsquerda(dto.crossoverHopEsquerda());
        avaliacao.setSixMeterDireita(dto.sixMeterDireita());
        avaliacao.setSixMeterEsquerda(dto.sixMeterEsquerda());

        Avaliacao updated = repository.save(avaliacao);
        logger.info("Avaliação atualizada com sucesso. ID: {}", id);

        return mapToDTO(updated);
    }

    public void delete(UUID id) {
        logger.info("Deletando avaliação com ID: {}", id);

        Avaliacao avaliacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação com ID " + id + " não encontrada"));

        repository.delete(avaliacao);
        logger.info("Avaliação deletada com sucesso. ID: {}", id);
    }

    public byte[] gerarPdf(UUID id) {
        logger.info("Gerando PDF para avaliação: {}", id);

        Avaliacao avaliacao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação com ID " + id + " não encontrada"));

        Context context = new Context();

        double singleHopDireita = avaliacao.getSingleHopDireita();
        double singleHopEsquerda = avaliacao.getSingleHopEsquerda();
        double tripleHopDireita = avaliacao.getTripleHopDireita();
        double tripleHopEsquerda = avaliacao.getTripleHopEsquerda();
        double crossoverHopDireita = avaliacao.getCrossoverHopDireita();
        double crossoverHopEsquerda = avaliacao.getCrossoverHopEsquerda();
        double sixMeterDireita = avaliacao.getSixMeterDireita();
        double sixMeterEsquerda = avaliacao.getSixMeterEsquerda();

        double lsiSingle = calcularLsi(singleHopDireita, singleHopEsquerda);
        double lsiTriple = calcularLsi(tripleHopDireita, tripleHopEsquerda);
        double lsiCrossover = calcularLsi(crossoverHopDireita, crossoverHopEsquerda);
        double lsiSix = calcularLsi(sixMeterDireita, sixMeterEsquerda);

        List<Avaliacao> historico = repository.findTop5ByPacienteIdOrderByDataHoraCriacaoAsc(avaliacao.getPaciente().getId());

        String historicoLsi = historico.stream()
                .map(a -> String.format(java.util.Locale.US, "%.1f",
                        (calcularLsi(a.getSingleHopDireita(), a.getSingleHopEsquerda()) +
                                calcularLsi(a.getTripleHopDireita(), a.getTripleHopEsquerda())) / 2))
                .collect(java.util.stream.Collectors.joining(","));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        context.setVariable("avaliacao", avaliacao);
        context.setVariable("dataFormatada", avaliacao.getDataHoraCriacao().format(dtf));
        context.setVariable("paciente", avaliacao.getPaciente());
        context.setVariable("singleHopDireita", String.format("%.1f", singleHopDireita));
        context.setVariable("singleHopEsquerda", String.format("%.1f", singleHopEsquerda));
        context.setVariable("tripleHopDireita", String.format("%.1f", tripleHopDireita));
        context.setVariable("tripleHopEsquerda", String.format("%.1f", tripleHopEsquerda));
        context.setVariable("crossoverHopDireita", String.format("%.1f", crossoverHopDireita));
        context.setVariable("crossoverHopEsquerda", String.format("%.1f", crossoverHopEsquerda));
        context.setVariable("sixMeterDireita", String.format("%.1f", sixMeterDireita));
        context.setVariable("sixMeterEsquerda", String.format("%.1f", sixMeterEsquerda));
        context.setVariable("lsiSingle", String.format("%.1f", lsiSingle));
        context.setVariable("lsiTriple", String.format("%.1f", lsiTriple));
        context.setVariable("lsiCrossover", String.format("%.1f", lsiCrossover));
        context.setVariable("lsiSix", String.format("%.1f", lsiSix));
        context.setVariable("aptoSingle", lsiSingle >= 90);
        context.setVariable("aptoTriple", lsiTriple >= 90);
        context.setVariable("aptoCrossover", lsiCrossover >= 90);
        context.setVariable("aptoSix", lsiSix >= 90);
        context.setVariable("historicoLsi", historicoLsi);
        context.setVariable("rawSingleD", singleHopDireita);
        context.setVariable("rawSingleE", singleHopEsquerda);
        context.setVariable("rawTripleD", tripleHopDireita);
        context.setVariable("rawTripleE", tripleHopEsquerda);
        context.setVariable("rawCrossD", crossoverHopDireita);
        context.setVariable("rawCrossE", crossoverHopEsquerda);
        context.setVariable("rawSixD", sixMeterDireita);
        context.setVariable("rawSixE", sixMeterEsquerda);

        String html = templateEngine.process("relatorio-avaliacao", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("Erro ao gerar PDF para avaliação: {}", id, e);
            throw new RuntimeException("Erro ao gerar PDF: " + e.getMessage());
        }
    }
}