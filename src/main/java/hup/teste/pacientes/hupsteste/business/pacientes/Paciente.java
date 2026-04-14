package hup.teste.pacientes.hupsteste.business.pacientes;

import hup.teste.pacientes.hupsteste.core.domains.BaseModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "pacientes")
public class Paciente extends BaseModel {
    @NotNull(message = "O nome é obrigatório")
    @NotBlank(message = "O nome não pode ser vazio")
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull(message = "O peso é obrigatório")
    @NotBlank(message = "O peso não pode ser vazio")
    @Column(name = "peso", nullable = false)
    private String peso;

    @NotNull(message = "A altura é obrigatória")
    @NotBlank(message = "A altura não pode ser vazia")
    @Column(name = "altura", nullable = false)
    private String altura;

    @NotNull(message = "Os dias pós RLCA são obrigatórios")
    @NotBlank(message = "Os dias pós RLCA não podem ser vazios")
    @Column(name = "dias_pos_rlca", nullable = false)
    private String diasPosRLCA;
}
