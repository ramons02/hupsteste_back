package hup.teste.pacientes.hupsteste.business.avaliacoes;

import hup.teste.pacientes.hupsteste.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "avaliacoes")
public class Avaliacao extends BaseModel {

    @NotNull(message = "O paciente é obrigatório")
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private hup.teste.pacientes.hupsteste.business.pacientes.Paciente paciente;

    // --- Single Leg Hop Test ---
    @Column(name = "single_hop_direita", nullable = false)
    private double singleHopDireita;

    @Column(name = "single_hop_esquerda", nullable = false)
    private double singleHopEsquerda;

    // --- Triple Hop Test ---
    @Column(name = "triple_hop_direita", nullable = false)
    private double tripleHopDireita;

    @Column(name = "triple_hop_esquerda", nullable = false)
    private double tripleHopEsquerda;

    // --- Crossover Hop Test ---
    @Column(name = "crossover_hop_direita", nullable = false)
    private double crossoverHopDireita;

    @Column(name = "crossover_hop_esquerda", nullable = false)
    private double crossoverHopEsquerda;

    // --- 6 Meter Timed Hop Test ---
    @Column(name = "six_meter_direita", nullable = false)
    private double sixMeterDireita;

    @Column(name = "six_meter_esquerda", nullable = false)
    private double sixMeterEsquerda;
}
