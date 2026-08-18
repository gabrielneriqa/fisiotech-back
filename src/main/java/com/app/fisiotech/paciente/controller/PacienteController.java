package com.app.fisiotech.paciente.controller;

import com.app.fisiotech.paciente.dto.PacienteCreateRequest;
import com.app.fisiotech.paciente.dto.PacienteResponse;
import com.app.fisiotech.paciente.dto.PacienteUpdateRequest;
import com.app.fisiotech.paciente.entity.Paciente;
import com.app.fisiotech.paciente.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody PacienteCreateRequest request) {
        Paciente pacienteCriado = pacienteService.criar(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pacienteCriado.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }


    @GetMapping
    public ResponseEntity<List<PacienteResponse>> listarTodos(){
        List<PacienteResponse> response = pacienteService.listarTodos()
                .stream()
                .map(PacienteResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id){
        Paciente paciente = pacienteService.buscarPorId(id);
        return ResponseEntity.ok(PacienteResponse.fromEntity(paciente));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizarPaciente(
            @PathVariable Long id,
            @Valid @RequestBody PacienteUpdateRequest request
            ){
        pacienteService.atualizar(id, request);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Long id){
        pacienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
