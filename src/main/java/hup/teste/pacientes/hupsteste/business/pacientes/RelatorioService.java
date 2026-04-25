package hup.teste.pacientes.hupsteste.business.pacientes;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import hup.teste.pacientes.hupsteste.business.avaliacoes.Avaliacao;
import hup.teste.pacientes.hupsteste.business.avaliacoes.AvaliacaoRepository;
import hup.teste.pacientes.hupsteste.core.exceptions.ResourceNotFoundException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class
RelatorioService {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioService.class);
    private static final DateTimeFormatter FORMATTER_LABEL = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final PacienteRepository pacienteRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public RelatorioService(PacienteRepository pacienteRepository, AvaliacaoRepository avaliacaoRepository) {
        this.pacienteRepository = pacienteRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    public byte[] gerarRelatorioEvolutivo(UUID pacienteId) {
        logger.info("Gerando relatório evolutivo para paciente: {}", pacienteId);

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + pacienteId + " não encontrado"));

        List<Avaliacao> avaliacoes = avaliacaoRepository.findByPacienteIdOrderByDataHoraCriacaoAsc(pacienteId);

        byte[] imagemGrafico = gerarGraficoLinhas(paciente.getNome(), avaliacoes);

        return montarPdf(paciente, avaliacoes, imagemGrafico);
    }

    private byte[] gerarGraficoLinhas(String nomePaciente, List<Avaliacao> avaliacoes) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Avaliacao a : avaliacoes) {
            String label = a.getDataHoraCriacao().format(FORMATTER_LABEL);
            dataset.addValue(calcularLsiMedio(a), "LSI Médio (%)", label);
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Evolução da Simetria — " + nomePaciente,
                "Data da Avaliação",
                "Simetria (%)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        ValueMarker metaMarcador = new ValueMarker(90.0);
        metaMarcador.setPaint(Color.RED);
        metaMarcador.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10.0f, new float[]{6.0f}, 0.0f));
        metaMarcador.setLabel("Meta: 90%");
        metaMarcador.setLabelAnchor(RectangleAnchor.TOP_RIGHT);
        metaMarcador.setLabelTextAnchor(TextAnchor.BOTTOM_RIGHT);
        plot.addRangeMarker(metaMarcador);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            BufferedImage image = chart.createBufferedImage(550, 300);
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao renderizar gráfico: " + e.getMessage(), e);
        }
    }

    private byte[] montarPdf(Paciente paciente, List<Avaliacao> avaliacoes, byte[] imagemGrafico) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font fonteTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font fonteSub = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD);
            Font fonteNormal = new Font(Font.FontFamily.HELVETICA, 11);

            doc.add(new Paragraph("Relatório de Evolução Clínica", fonteTitulo));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Paciente: " + paciente.getNome(), fonteSub));
            doc.add(new Paragraph("Peso: " + paciente.getPeso() + " kg  |  Altura: " + paciente.getAltura() + " m", fonteNormal));

            String dataCirurgia = paciente.getDataCirugia() != null
                    ? paciente.getDataCirugia().format(FORMATTER_DATA)
                    : "—";
            doc.add(new Paragraph("Data da Cirurgia: " + dataCirurgia, fonteNormal));
            doc.add(new Paragraph("Dias pós-operatório: " + paciente.getDiasPosOperatorio(), fonteNormal));
            doc.add(Chunk.NEWLINE);
            doc.add(new Paragraph("Total de avaliações registradas: " + avaliacoes.size(), fonteNormal));
            doc.add(Chunk.NEWLINE);

            Image grafico = Image.getInstance(imagemGrafico);
            grafico.setAlignment(Image.ALIGN_CENTER);
            grafico.scaleToFit(500, 280);
            doc.add(grafico);

            doc.close();
            logger.info("PDF do relatório gerado com sucesso para paciente: {}", paciente.getId());
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar PDF do relatório: " + e.getMessage(), e);
        }
    }

    private double calcularLsiMedio(Avaliacao a) {
        double lsiSingle = calcularLsi(a.getSingleHopDireita(), a.getSingleHopEsquerda());
        double lsiTriple = calcularLsi(a.getTripleHopDireita(), a.getTripleHopEsquerda());
        double lsiCrossover = calcularLsi(a.getCrossoverHopDireita(), a.getCrossoverHopEsquerda());
        double lsiSix = calcularLsi(a.getSixMeterDireita(), a.getSixMeterEsquerda());
        return (lsiSingle + lsiTriple + lsiCrossover + lsiSix) / 4.0;
    }

    private double calcularLsi(double v1, double v2) {
        if (v1 <= 0 || v2 <= 0) return 0.0;
        return (Math.min(v1, v2) / Math.max(v1, v2)) * 100;
    }
}