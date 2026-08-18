package com.app.fisiotech.mensagem.dto;

import com.app.fisiotech.mensagem.entity.AutorMensagem;
import com.app.fisiotech.mensagem.entity.Mensagem;

import java.time.LocalDateTime;

public record CaixaEntradaItemResponse(
        Long pacienteId,
        String pacienteNome,
        String ultimaMensagem,
        AutorMensagem ultimoAutor,
        LocalDateTime dataUltimaMensagem
) {

    public static CaixaEntradaItemResponse fromUltimaMensagem(Mensagem ultimaMensagem) {
        return new CaixaEntradaItemResponse(
                ultimaMensagem.getPaciente().getId(),
                ultimaMensagem.getPaciente().getNome(),
                ultimaMensagem.getConteudo(),
                ultimaMensagem.getAutor(),
                ultimaMensagem.getDataEnvio()
        );
    }
}
