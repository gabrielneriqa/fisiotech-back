package com.app.fisiotech.me.controller;

import com.app.fisiotech.auth.security.AuthenticatedUser;
import com.app.fisiotech.avaliacao.dto.AvaliacaoCreateRequest;
import com.app.fisiotech.avaliacao.dto.AvaliacaoResponse;
import com.app.fisiotech.avaliacao.service.AvaliacaoService;
import com.app.fisiotech.consulta.dto.ConsultaResponse;
import com.app.fisiotech.consulta.service.ConsultaService;
import com.app.fisiotech.me.dto.MinhaMensagemCreateRequest;
import com.app.fisiotech.mensagem.dto.MensagemResponse;
import com.app.fisiotech.mensagem.service.MensagemService;
import com.app.fisiotech.paciente.dto.PacienteResponse;
import com.app.fisiotech.paciente.dto.PacienteUpdateRequest;
import com.app.fisiotech.paciente.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final PacienteService pacienteService;
    private final ConsultaService consultaService;
    private final MensagemService mensagemService;
    private final AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<PacienteResponse> perfil(@AuthenticationPrincipal AuthenticatedUser usuarioLogado) {
        return ResponseEntity.ok(PacienteResponse.fromEntity(pacienteService.buscarProprioPerfil(usuarioLogado.getId())));
    }

    @PutMapping
    public ResponseEntity<Void> atualizarPerfil(
            @Valid @RequestBody PacienteUpdateRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuarioLogado
    ) {
        pacienteService.atualizarProprioPerfil(usuarioLogado.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/consultas")
    public ResponseEntity<List<ConsultaResponse>> minhasConsultas(@AuthenticationPrincipal AuthenticatedUser usuarioLogado) {
        List<ConsultaResponse> response = consultaService.listarDoPacienteLogado(usuarioLogado.getId())
                .stream()
                .map(ConsultaResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/consultas/{id}")
    public ResponseEntity<ConsultaResponse> minhaConsulta(
            @PathVariable Long id,
            @AuthenticationPrincipal AuthenticatedUser usuarioLogado
    ) {
        return ResponseEntity.ok(ConsultaResponse.fromEntity(consultaService.buscarPorIdEPaciente(id, usuarioLogado.getId())));
    }

    @GetMapping("/mensagens")
    public ResponseEntity<List<MensagemResponse>> minhasMensagens(@AuthenticationPrincipal AuthenticatedUser usuarioLogado) {
        List<MensagemResponse> response = mensagemService.listarDoPacienteLogado(usuarioLogado.getId())
                .stream()
                .map(MensagemResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mensagens")
    public ResponseEntity<Void> enviarMensagem(
            @Valid @RequestBody MinhaMensagemCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuarioLogado
    ) {
        mensagemService.enviarComoPaciente(usuarioLogado.getId(), request.conteudo());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/avaliacoes")
    public ResponseEntity<Void> avaliar(
            @Valid @RequestBody AvaliacaoCreateRequest request,
            @AuthenticationPrincipal AuthenticatedUser usuarioLogado
    ) {
        avaliacaoService.criarComoPaciente(request, usuarioLogado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/avaliacoes/consulta/{consultaId}")
    public ResponseEntity<AvaliacaoResponse> minhaAvaliacao(
            @PathVariable Long consultaId,
            @AuthenticationPrincipal AuthenticatedUser usuarioLogado
    ) {
        return ResponseEntity.ok(AvaliacaoResponse.fromEntity(avaliacaoService.buscarPorConsultaEPaciente(consultaId, usuarioLogado.getId())));
    }

}
