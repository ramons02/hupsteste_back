package hup.teste.pacientes.hupsteste.business.pacientes;

import hup.teste.pacientes.hupsteste.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "pacientes")
public class Paciente extends BaseModel {
    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotBlank(message = "O peso é obrigatório")
    @Column(name = "peso", nullable = false)
    private String peso;

    @NotBlank(message = "A altura é obrigatória")
    @Column(name = "altura", nullable = false)
    private String altura;

    @NotBlank(message = "Os dias pós RLCA são obrigatórios")
    @Column(name = "dias_pos_rlca", nullable = false)
    private String diasPosRLCA;
}
