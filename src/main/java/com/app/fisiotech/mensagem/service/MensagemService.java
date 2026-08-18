package com.app.fisiotech.mensagem.service;

import com.app.fisiotech.exception.RecursoNaoEncontradoException;
import com.app.fisiotech.mensagem.dto.MensagemCreateRequest;
import com.app.fisiotech.mensagem.entity.AutorMensagem;
import com.app.fisiotech.mensagem.entity.Mensagem;
import com.app.fisiotech.mensagem.repository.MensagemRepository;
import com.app.fisiotech.paciente.entity.Paciente;
import com.app.fisiotech.paciente.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final PacienteRepository pacienteRepository;

    @Transactional
    public Mensagem enviar(MensagemCreateRequest request, Long profissionalId) {
        Paciente paciente = buscarPacienteDoProfissional(request.pacienteId(), profissionalId);

        Mensagem mensagem = new Mensagem(paciente, request.autor(), request.conteudo());

        return mensagemRepository.save(mensagem);
    }


    @Transactional(readOnly = true)
    public List<Mensagem> listarPorPaciente(Long pacienteId, Long profissionalId) {
        buscarPacienteDoProfissional(pacienteId, profissionalId);

        return mensagemRepository.findByPacienteId(pacienteId, Sort.by(Sort.Direction.ASC, "dataEnvio"));
    }


    @Transactional
    public Mensagem enviarComoPaciente(Long pacienteId, String conteudo) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        Mensagem mensagem = new Mensagem(paciente, AutorMensagem.PACIENTE, conteudo);

        return mensagemRepository.save(mensagem);
    }


    @Transactional(readOnly = true)
    public List<Mensagem> listarDoPacienteLogado(Long pacienteId) {
        return mensagemRepository.findByPacienteId(pacienteId, Sort.by(Sort.Direction.ASC, "dataEnvio"));
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
