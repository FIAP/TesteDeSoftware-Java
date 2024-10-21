package br.com.fiap.helper;

import br.com.fiap.model.Mensagem;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class MensagemHelper {

    public Mensagem gerarMensagem() {
        return Mensagem.builder()
                .usuario("João")
                .conteudo("conteudo")
                .build();
    }

    public static Mensagem gerarMensagemCompleta() {
        var timestamp = LocalDateTime.now();
        return Mensagem.builder()
                .id(UUID.randomUUID())
                .usuario("João")
                .conteudo("conteudo")
                .dataCriacao(timestamp)
                .dataAlteracao(timestamp)
                .build();
    }
}
