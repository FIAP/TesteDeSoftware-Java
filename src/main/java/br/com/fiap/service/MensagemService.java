package br.com.fiap.service;

import br.com.fiap.model.Mensagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MensagemService {

    Mensagem registrarMensagem(Mensagem mensagem);

    Mensagem obterMensagem(UUID id);

    Mensagem atualizarMensagem(UUID id, Mensagem mensagemNova);

    boolean removerMensagem(UUID id);

    Page<Mensagem> obterMensagens(Pageable pageable);
}
