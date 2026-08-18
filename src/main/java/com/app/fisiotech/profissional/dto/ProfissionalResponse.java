package com.app.fisiotech.profissional.dto;

import com.app.fisiotech.profissional.entity.Profissional;

import java.time.LocalDateTime;

public record ProfissionalResponse(
        Long id,
        String nome,
        String email,
        String registroProfissional,
        String especialidade,
        LocalDateTime dataCriacao
) {

    public static ProfissionalResponse fromEntity(Profissional profissional) {
        return new ProfissionalResponse(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getRegistroProfissional(),
                profissional.getEspecialidade(),
                profissional.getDataCriacao()
        );
    }
}
