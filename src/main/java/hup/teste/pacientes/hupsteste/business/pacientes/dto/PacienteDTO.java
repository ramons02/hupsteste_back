package hup.teste.pacientes.hupsteste.business.pacientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record PacienteDTO(
        UUID id,
        
        @NotNull(message = "Nome é obrigatório")
        @NotBlank(message = "Nome não pode ser vazio")
        String nome,
        
        @NotNull(message = "Peso é obrigatório")
        @NotBlank(message = "Peso não pode ser vazio")
        String peso,
        
        @NotNull(message = "Altura é obrigatória")
        @NotBlank(message = "Altura não pode ser vazia")
        String altura,
        
        @NotNull(message = "Dias pós RLCA é obrigatório")
        @NotBlank(message = "Dias pós RLCA não pode ser vazio")
        String diasPosRLCA
) {}
