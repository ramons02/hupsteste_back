package hup.teste.pacientes.hupsteste.business.avaliacoes;

import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter; // Adicionado para formatar a data

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class AvaliacaoService {

    @Autowired
    private TemplateEngine templateEngine;

    // Método auxiliar para calcular a simetria (LSI)
    private double calcularLsi(double v1, double v2) {
        if (v1 <= 0 || v2 <= 0) return 0.0;
        return (Math.min(v1, v2) / Math.max(v1, v2)) * 100;
    }

    public byte[] gerarPdf(Avaliacao avaliacao) {
        Context context = new Context();

        // Valores originais
        double singleHopDireita = avaliacao.getSingleHopDireita();
        double singleHopEsquerda = avaliacao.getSingleHopEsquerda();
        double tripleHopDireita = avaliacao.getTripleHopDireita();
        double tripleHopEsquerda = avaliacao.getTripleHopEsquerda();
        double crossoverHopDireita = avaliacao.getCrossoverHopDireita();
        double crossoverHopEsquerda = avaliacao.getCrossoverHopEsquerda();
        double sixMeterDireita = avaliacao.getSixMeterDireita();
        double sixMeterEsquerda = avaliacao.getSixMeterEsquerda();

        // Cálculos de Simetria (LSI)
        double lsiSingle = calcularLsi(singleHopDireita, singleHopEsquerda);
        double lsiTriple = calcularLsi(tripleHopDireita, tripleHopEsquerda);
        double lsiCrossover = calcularLsi(crossoverHopDireita, crossoverHopEsquerda);
        double lsiSix = calcularLsi(sixMeterDireita, sixMeterEsquerda);

        context.setVariable("avaliacao", avaliacao);
        
        // Formatação da Data para o PDF
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        context.setVariable("dataFormatada", avaliacao.getDataHoraCriacao().format(dtf));
        context.setVariable("avaliacao", avaliacao);
        context.setVariable("paciente", avaliacao.getPaciente());

        // Variáveis de valor formatado
        context.setVariable("singleHopDireita", String.format("%.1f", singleHopDireita));
        context.setVariable("singleHopEsquerda", String.format("%.1f", singleHopEsquerda));
        context.setVariable("tripleHopDireita", String.format("%.1f", tripleHopDireita));
        context.setVariable("tripleHopEsquerda", String.format("%.1f", tripleHopEsquerda));
        context.setVariable("crossoverHopDireita", String.format("%.1f", crossoverHopDireita));
        context.setVariable("crossoverHopEsquerda", String.format("%.1f", crossoverHopEsquerda));
        context.setVariable("sixMeterDireita", String.format("%.1f", sixMeterDireita));
        context.setVariable("sixMeterEsquerda", String.format("%.1f", sixMeterEsquerda));

        // Variáveis de LSI enviadas para o HTML
        context.setVariable("lsiSingle", String.format("%.1f", lsiSingle));
        context.setVariable("lsiTriple", String.format("%.1f", lsiTriple));
        context.setVariable("lsiCrossover", String.format("%.1f", lsiCrossover));
        context.setVariable("lsiSix", String.format("%.1f", lsiSix));
        
        // Booleano para facilitar o check de "Apto" (Geralmente > 90%)
        context.setVariable("aptoSingle", lsiSingle >= 90);
        context.setVariable("aptoTriple", lsiTriple >= 90);
        context.setVariable("aptoCrossover", lsiCrossover >= 90);
        context.setVariable("aptoSix", lsiSix >= 90);

        String html = templateEngine.process("relatorio-avaliacao", context);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro interno no Java ao gerar PDF: " + e.getMessage());
        }
    }

    private final AvaliacaoRepository repository;

    public AvaliacaoService(AvaliacaoRepository repository) {
        this.repository = repository;
    }

    public Avaliacao save(Avaliacao avaliacao) {
        return repository.save(avaliacao);
    }

    public List<Avaliacao> findAll() {
        return repository.findAll();
    }

    public Avaliacao findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
    }

    public Avaliacao update(UUID id, Avaliacao avaliacaoAtualizada) {
        Avaliacao avaliacao = findById(id);
        BeanUtils.copyProperties(avaliacaoAtualizada, avaliacao, "id", "dataHoraCriacao");
        return repository.save(avaliacao);
    }

    public void delete(UUID id) {
        Avaliacao avaliacao = findById(id);
        repository.delete(avaliacao);
    }
}