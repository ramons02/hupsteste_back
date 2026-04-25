package hup.teste.pacientes.hupsteste.business.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrarRequest(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String senha
) {}