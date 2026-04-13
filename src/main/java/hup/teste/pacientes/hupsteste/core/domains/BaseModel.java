package hup.teste.pacientes.hupsteste.core.domains;

import java.time.LocalDateTime; // Alterado para a API moderna de data
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)    
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    // Removida a anotação @Temporal, o Hibernate já mapeia LocalDateTime como TIMESTAMP automaticamente
    @Column(name = "data_hora_criacao", nullable = false, updatable = false)
    private LocalDateTime dataHoraCriacao;

    @PrePersist
    protected void onCreateBase() {
        if (this.dataHoraCriacao == null) {
            this.dataHoraCriacao = LocalDateTime.now();
        }
    }
}
