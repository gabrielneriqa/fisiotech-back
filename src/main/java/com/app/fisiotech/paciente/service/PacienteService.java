package com.app.fisiotech.paciente.service;

import com.app.fisiotech.exception.EmailJaCadastradoException;
import com.app.fisiotech.exception.RecursoNaoEncontradoException;
import com.app.fisiotech.paciente.dto.PacienteCreateRequest;
import com.app.fisiotech.paciente.dto.PacienteUpdateRequest;
import com.app.fisiotech.paciente.entity.Paciente;
import com.app.fisiotech.paciente.repository.PacienteRepository;
import com.app.fisiotech.profissional.entity.Profissional;
import com.app.fisiotech.profissional.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Paciente criar(PacienteCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.email());

        if (pacienteRepository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException("Já existe um paciente cadastrado com este email.");
        }

        Profissional profissional = buscarProfissional(request.profissionalId());

        String senhaCriptografada = passwordEncoder.encode(request.senha());

        Paciente paciente = new Paciente(
                request.nome().trim(),
                emailNormalizado,
                senhaCriptografada,
                profissional
        );

        return pacienteRepository.save(paciente);
    }


    @Transactional(readOnly = true)
    public List<Paciente> listarTodos(Long profissionalId){
        return pacienteRepository.findByProfissionalId(profissionalId, Sort.by(Sort.Direction.ASC, "id"));
    }


    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id){
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));
    }


    @Transactional
    public Paciente atualizar(Long id, PacienteUpdateRequest request){
        Paciente pacienteASerAtualizado = buscarPorId(id);

        String emailNormalizado = normalizarEmail(request.email());

        if(pacienteRepository.existsByEmailAndIdNot(emailNormalizado, id)){
            throw new EmailJaCadastradoException("Email já cadastrado.");
        }

        Profissional profissional = buscarProfissional(request.profissionalId());

        pacienteASerAtualizado.setNome(request.nome().trim());
        pacienteASerAtualizado.setEmail(emailNormalizado);
        pacienteASerAtualizado.setSenha(passwordEncoder.encode(request.senha()));
        pacienteASerAtualizado.setProfissional(profissional);

        return pacienteRepository.save(pacienteASerAtualizado);
    }


    @Transactional
    public void deletar(Long id){
        Paciente pacienteASerDeletado = buscarPorId(id);
        pacienteRepository.delete(pacienteASerDeletado);
    }


    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }


    private Profissional buscarProfissional(Long profissionalId) {
        return profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado."));
    }
}
