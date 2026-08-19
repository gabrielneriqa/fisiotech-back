package com.app.fisiotech.paciente.service;

import com.app.fisiotech.exception.EmailJaCadastradoException;
import com.app.fisiotech.exception.RecursoNaoEncontradoException;
import com.app.fisiotech.paciente.dto.PacienteAdminUpdateRequest;
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
    public Paciente criar(PacienteCreateRequest request, Long profissionalId) {
        String emailNormalizado = normalizarEmail(request.email());

        if (pacienteRepository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException("Já existe um paciente cadastrado com este email.");
        }

        Profissional profissional = buscarProfissional(profissionalId);

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
    public Paciente buscarPorId(Long id, Long profissionalId){
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));

        if (!paciente.getProfissional().getId().equals(profissionalId)) {
            throw new RecursoNaoEncontradoException("Paciente não encontrado.");
        }

        return paciente;
    }


    @Transactional
    public Paciente atualizar(Long id, PacienteUpdateRequest request, Long profissionalId){
        Paciente paciente = buscarPorId(id, profissionalId);
        return aplicarAtualizacao(paciente, request);
    }


    @Transactional
    public void deletar(Long id, Long profissionalId){
        Paciente pacienteASerDeletado = buscarPorId(id, profissionalId);
        pacienteRepository.delete(pacienteASerDeletado);
    }


    @Transactional(readOnly = true)
    public Paciente buscarProprioPerfil(Long pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));
    }


    @Transactional
    public Paciente atualizarProprioPerfil(Long pacienteId, PacienteUpdateRequest request) {
        Paciente paciente = buscarProprioPerfil(pacienteId);
        return aplicarAtualizacao(paciente, request);
    }


    private Paciente aplicarAtualizacao(Paciente paciente, PacienteUpdateRequest request) {
        String emailNormalizado = normalizarEmail(request.email());

        if (pacienteRepository.existsByEmailAndIdNot(emailNormalizado, paciente.getId())) {
            throw new EmailJaCadastradoException("Email já cadastrado.");
        }

        paciente.setNome(request.nome().trim());
        paciente.setEmail(emailNormalizado);
        paciente.setSenha(passwordEncoder.encode(request.senha()));

        return pacienteRepository.save(paciente);
    }


    @Transactional(readOnly = true)
    public List<Paciente> listarTodosAdmin() {
        return pacienteRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }


    @Transactional(readOnly = true)
    public Paciente buscarPorIdAdmin(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado."));
    }


    @Transactional
    public Paciente atualizarAdmin(Long id, PacienteAdminUpdateRequest request) {
        Paciente paciente = buscarPorIdAdmin(id);
        String emailNormalizado = normalizarEmail(request.email());

        if (pacienteRepository.existsByEmailAndIdNot(emailNormalizado, paciente.getId())) {
            throw new EmailJaCadastradoException("Email já cadastrado.");
        }

        Profissional profissional = buscarProfissional(request.profissionalId());

        paciente.setNome(request.nome().trim());
        paciente.setEmail(emailNormalizado);
        paciente.setProfissional(profissional);

        if (request.senha() != null && !request.senha().isBlank()) {
            paciente.setSenha(passwordEncoder.encode(request.senha()));
        }

        return pacienteRepository.save(paciente);
    }


    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }


    private Profissional buscarProfissional(Long profissionalId) {
        return profissionalRepository.findById(profissionalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado."));
    }
}
