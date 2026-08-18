package com.app.fisiotech.mensagem.repository;

import com.app.fisiotech.mensagem.entity.Mensagem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    List<Mensagem> findByPacienteId(Long pacienteId, Sort sort);

    Optional<Mensagem> findFirstByPacienteIdOrderByDataEnvioDesc(Long pacienteId);

}
