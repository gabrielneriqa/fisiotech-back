package com.app.fisiotech.profissional.service;

import com.app.fisiotech.exception.EmailJaCadastradoException;
import com.app.fisiotech.exception.RecursoNaoEncontradoException;
import com.app.fisiotech.profissional.dto.ProfissionalCreateRequest;
import com.app.fisiotech.profissional.dto.ProfissionalUpdateRequest;
import com.app.fisiotech.profissional.entity.Profissional;
import com.app.fisiotech.profissional.repository.ProfissionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ProfissionalService {

    private final ProfissionalRepository profissionalRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Profissional criar(ProfissionalCreateRequest request) {
        String emailNormalizado = normalizarEmail(request.email());

        if (profissionalRepository.existsByEmail(emailNormalizado)) {
            throw new EmailJaCadastradoException("Já existe um profissional cadastrado com este email.");
        }

        if (profissionalRepository.existsByRegistroProfissional(request.registroProfissional())) {
            throw new EmailJaCadastradoException("Já existe um profissional cadastrado com este registro profissional.");
        }

        String senhaCriptografada = passwordEncoder.encode(request.senha());

        Profissional profissional = new Profissional(
                request.nome().trim(),
                emailNormalizado,
                senhaCriptografada,
                request.registroProfissional().trim(),
                request.especialidade().trim()
        );

        return profissionalRepository.save(profissional);
    }


    @Transactional(readOnly = true)
    public List<Profissional> listarTodos() {
        return profissionalRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }


    @Transactional(readOnly = true)
    public Profissional buscarPorId(Long id) {
        return profissionalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Profissional não encontrado."));
    }


    @Transactional
    public Profissional atualizar(Long id, ProfissionalUpdateRequest request) {
        Profissional profissionalASerAtualizado = buscarPorId(id);

        String emailNormalizado = normalizarEmail(request.email());

        if (profissionalRepository.existsByEmailAndIdNot(emailNormalizado, id)) {
            throw new EmailJaCadastradoException("Email já cadastrado.");
        }

        if (profissionalRepository.existsByRegistroProfissionalAndIdNot(request.registroProfissional(), id)) {
            throw new EmailJaCadastradoException("Registro profissional já cadastrado.");
        }

        profissionalASerAtualizado.setNome(request.nome().trim());
        profissionalASerAtualizado.setEmail(emailNormalizado);
        profissionalASerAtualizado.setSenha(passwordEncoder.encode(request.senha()));
        profissionalASerAtualizado.setRegistroProfissional(request.registroProfissional().trim());
        profissionalASerAtualizado.setEspecialidade(request.especialidade().trim());

        return profissionalRepository.save(profissionalASerAtualizado);
    }


    @Transactional
    public void deletar(Long id) {
        Profissional profissionalASerDeletado = buscarPorId(id);
        profissionalRepository.delete(profissionalASerDeletado);
    }


    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
