package hup.teste.pacientes.hupsteste.business.pacientes;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PacienteService {
    
    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }
    
    public Paciente save(Paciente paciente) {
        return repository.save(paciente);
    }

    public List<Paciente> findAll() {
        return repository.findAll();
    }

    public Paciente findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Paciente update(UUID id, Paciente pacienteAtualizado) {
        Paciente paciente = findById(id);
        BeanUtils.copyProperties(pacienteAtualizado, paciente, "id", "dataHoraCriacao");
        return repository.save(paciente);
    }

    public void delete(UUID id) {
        Paciente paciente = findById(id);
        repository.delete(paciente);
    }
}
