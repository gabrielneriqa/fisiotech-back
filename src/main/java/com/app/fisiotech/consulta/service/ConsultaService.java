package com.app.fisiotech.consulta.service;

import com.app.fisiotech.avaliacao.repository.AvaliacaoRepository;
import com.app.fisiotech.consulta.dto.ConsultaCreateRequest;
import com.app.fisiotech.consulta.dto.ConsultaUpdateRequest;
import com.app.fisiotech.consulta.entity.Consulta;
import com.app.fisiotech.consulta.repository.ConsultaRepository;
import com.app.fisiotech.exception.RecursoNaoEncontradoException;
import com.app.fisiotech.paciente.entity.Paciente;
import com.app.fisiotech.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    @Transactional
    public Consulta criar(ConsultaCreateRequest request, Long profissionalId) {
        Paciente paciente = buscarPacienteDoProfissional(request.pacienteId(), profissionalId);

        Consulta consulta = new Consulta(
                paciente,
                request.dataHora(),
                request.tipo(),
                request.convenio(),
                request.valor()
        );

        return consultaRepository.save(consulta);
    }


    @Transactional(readOnly = true)
    public List<Consulta> listarTodos(Long profissionalId, Long pacienteId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "dataHora");

        if (pacienteId != null) {
            return consultaRepository.findByPaciente_IdAndPaciente_Profissional_Id(pacienteId, profissionalId, sort);
        }

        return consultaRepository.findByPaciente_Profissional_Id(profissionalId, sort);
    }


    @Transactional(readOnly = true)
    public Consulta buscarPorId(Long id, Long profissionalId) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));

        if (!consulta.getPaciente().getProfissional().getId().equals(profissionalId)) {
            throw new RecursoNaoEncontradoException("Consulta não encontrada.");
        }

        return consulta;
    }


    @Transactional
    public Consulta atualizar(Long id, ConsultaUpdateRequest request, Long profissionalId) {
        Consulta consulta = buscarPorId(id, profissionalId);

        consulta.setDataHora(request.dataHora());
        consulta.setTipo(request.tipo());
        consulta.setStatus(request.status());
        consulta.setConvenio(request.convenio());
        consulta.setValor(request.valor());

        if (request.quadroClinico() != null) {
            consulta.setQuadroClinico(request.quadroClinico().toEntity());
        }
        if (request.habitosVida() != null) {
            consulta.setHabitosVida(request.habitosVida().toEntity());
        }
        if (request.exameFisico() != null) {
            consulta.setExameFisico(request.exameFisico().toEntity());
        }
        if (request.diagnostico() != null) {
            consulta.setDiagnostico(request.diagnostico().toEntity());
        }

        return consultaRepository.save(consulta);
    }


    @Transactional
    public void deletar(Long id, Long profissionalId) {
        Consulta consulta = buscarPorId(id, profissionalId);
        avaliacaoRepository.findByConsultaId(id).ifPresent(avaliacaoRepository::delete);
        consultaRepository.delete(consulta);
    }


    @Transactional(readOnly = true)
    public List<Consulta> listarDoPacienteLogado(Long pacienteId) {
        return consultaRepository.findByPaciente_Id(pacienteId, Sort.by(Sort.Direction.DESC, "dataHora"));
    }


    @Transactional(readOnly = true)
    public Consulta buscarPorIdEPaciente(Long id, Long pacienteId) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada."));

        if (!consulta.getPaciente().getId().equals(pacienteId)) {
            throw new RecursoNaoEncontradoException("Consulta não encontrada.");
        }

        return consulta;
    }


    private Paciente buscarPacienteDoProfissional(Long pacienteId, Long profissionalId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        if (!paciente.getProfissional().getId().equals(profissionalId)) {
            throw new RecursoNaoEncontradoException("Paciente não encontrado.");
        }

        return paciente;
    }
}
