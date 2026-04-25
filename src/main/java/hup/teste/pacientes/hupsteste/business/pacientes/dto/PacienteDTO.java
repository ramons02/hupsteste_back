package hup.teste.pacientes.hupsteste.business.pacientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record PacienteDTO(
        UUID id,

        @NotBlank(message = "Nome não pode ser vazio")
        String nome,

        @NotBlank(message = "Peso não pode ser vazio")
        String peso,

        @NotBlank(message = "Altura não pode ser vazia")
        String altura,

        @NotNull(message = "Data da cirurgia é obrigatória")
        @JsonFormat(pattern = "yyyy-MM-dd") // GARANTE QUE O JAVA ENTENDA A DATA DO ANGULAR
        LocalDate dataCirugia,

        @NotBlank(message = "Por favor informar menbro operado")
        String membro_operado,

        Long diasPosOperatorio
) {}