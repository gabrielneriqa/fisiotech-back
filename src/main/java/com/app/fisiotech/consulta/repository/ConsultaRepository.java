package com.app.fisiotech.consulta.repository;

import com.app.fisiotech.consulta.entity.Consulta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPaciente_Profissional_Id(Long profissionalId, Sort sort);

    List<Consulta> findByPaciente_IdAndPaciente_Profissional_Id(Long pacienteId, Long profissionalId, Sort sort);

}
