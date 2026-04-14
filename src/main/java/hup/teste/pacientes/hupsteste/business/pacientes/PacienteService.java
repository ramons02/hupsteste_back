package hup.teste.pacientes.hupsteste.business.pacientes;

import org.springframework.stereotype.Service;
import hup.teste.pacientes.hupsteste.core.exceptions.ResourceNotFoundException;
import hup.teste.pacientes.hupsteste.business.pacientes.dto.PacienteDTO;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PacienteService {
    
    private static final Logger logger = LoggerFactory.getLogger(PacienteService.class);
    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
    }
    
    public PacienteDTO save(PacienteDTO dto) {
        logger.info("Criando novo paciente: {}", dto.nome());
        
        Paciente paciente = new Paciente();
        paciente.setNome(dto.nome());
        paciente.setPeso(dto.peso());
        paciente.setAltura(dto.altura());
        paciente.setDiasPosRLCA(dto.diasPosRLCA());
        
        Paciente saved = repository.save(paciente);
        logger.info("Paciente criado com sucesso. ID: {}", saved.getId());
        
        return mapToDTO(saved);
    }

    public List<PacienteDTO> findAll() {
        logger.info("Listando todos os pacientes");
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PacienteDTO findById(UUID id) {
        logger.info("Buscando paciente por ID: {}", id);
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + id + " não encontrado"));
        return mapToDTO(paciente);
    }

    public PacienteDTO update(UUID id, PacienteDTO dto) {
        logger.info("Atualizando paciente com ID: {}", id);
        
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + id + " não encontrado"));
        
        paciente.setNome(dto.nome());
        paciente.setPeso(dto.peso());
        paciente.setAltura(dto.altura());
        paciente.setDiasPosRLCA(dto.diasPosRLCA());
        
        Paciente updated = repository.save(paciente);
        logger.info("Paciente atualizado com sucesso. ID: {}", id);
        
        return mapToDTO(updated);
    }

    public void delete(UUID id) {
        logger.info("Deletando paciente com ID: {}", id);
        
        Paciente paciente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente com ID " + id + " não encontrado"));
        
        repository.delete(paciente);
        logger.info("Paciente deletado com sucesso. ID: {}", id);
    }
    
    private PacienteDTO mapToDTO(Paciente paciente) {
        return new PacienteDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getPeso(),
                paciente.getAltura(),
                paciente.getDiasPosRLCA()
        );
    }
}
