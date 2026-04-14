package hup.teste.pacientes.hupsteste.business.avaliacoes.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.UUID;

public record AvaliacaoDTO(
        UUID id,
        
        @NotNull(message = "Paciente ID é obrigatório")
        UUID pacienteId,
        
        @NotNull(message = "Single Hop Direita é obrigatória")
        @Positive(message = "Single Hop Direita deve ser positiva")
        Double singleHopDireita,
        
        @NotNull(message = "Single Hop Esquerda é obrigatória")
        @Positive(message = "Single Hop Esquerda deve ser positiva")
        Double singleHopEsquerda,
        
        @NotNull(message = "Triple Hop Direita é obrigatória")
        @Positive(message = "Triple Hop Direita deve ser positiva")
        Double tripleHopDireita,
        
        @NotNull(message = "Triple Hop Esquerda é obrigatória")
        @Positive(message = "Triple Hop Esquerda deve ser positiva")
        Double tripleHopEsquerda,
        
        @NotNull(message = "Crossover Hop Direita é obrigatória")
        @Positive(message = "Crossover Hop Direita deve ser positiva")
        Double crossoverHopDireita,
        
        @NotNull(message = "Crossover Hop Esquerda é obrigatória")
        @Positive(message = "Crossover Hop Esquerda deve ser positiva")
        Double crossoverHopEsquerda,
        
        @NotNull(message = "6 Meter Direita é obrigatória")
        @Positive(message = "6 Meter Direita deve ser positiva")
        Double sixMeterDireita,
        
        @NotNull(message = "6 Meter Esquerda é obrigatória")
        @Positive(message = "6 Meter Esquerda deve ser positiva")
        Double sixMeterEsquerda
) {}
