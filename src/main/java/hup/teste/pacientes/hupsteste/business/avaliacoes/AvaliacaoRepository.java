package hup.teste.pacientes.hupsteste.business.avaliacoes;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    List<Avaliacao> findTop5ByPacienteIdOrderByDataHoraCriacaoAsc(UUID pacienteId);

    List<Avaliacao> findByPacienteIdOrderByDataHoraCriacaoAsc(UUID pacienteId);
}
