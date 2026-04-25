
package hup.teste.pacientes.hupsteste.business.pacientes;
import hup.teste.pacientes.hupsteste.core.domains.BaseModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "pacientes")
public class Paciente extends BaseModel {

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "peso", nullable = false)
    private String peso;

    @Column(name = "altura", nullable = false)
    private String altura;

    // No banco a coluna chama data_cirurgia, mas no Java usamos dataCirugia
    @Column(name = "data_cirurgia", nullable = false)
    private LocalDate dataCirugia;

    @Column(name = "membro_op", nullable = false)
    private String membro_operado;

    @Transient
    public Long getDiasPosOperatorio() { // Mudado para 'O'
        if (this.dataCirugia == null) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(this.dataCirugia, LocalDate.now());
    }

}