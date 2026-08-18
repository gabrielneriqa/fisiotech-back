package com.app.fisiotech.paciente.dto;

import com.app.fisiotech.paciente.entity.Paciente;
import java.time.LocalDateTime;

public record PacienteResponse(
        Long id,
        String nome,
        String email,
        Long profissionalId,
        LocalDateTime dataCriacao
) {

    public static PacienteResponse fromEntity(Paciente paciente){
        return new PacienteResponse(
                paciente.getId(),
                paciente.getNome(),
                paciente.getEmail(),
                paciente.getProfissional().getId(),
                paciente.getDataCriacao()
        );
    }
}
