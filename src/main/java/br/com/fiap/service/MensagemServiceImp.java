package br.com.fiap.service;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.model.Mensagem;
import br.com.fiap.repository.MensagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemServiceImp implements MensagemService {

    private final MensagemRepository mensagemRepository;

    @Override
    public Mensagem registrarMensagem(Mensagem mensagem) {
        mensagem.setId(UUID.randomUUID());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public Mensagem obterMensagem(UUID id) {
        return mensagemRepository.findById(id)
                .orElseThrow(() -> new MensagemNotFoundException("mensagem não encontrada"));
    }

    @Override
    public Mensagem atualizarMensagem(UUID id, Mensagem mensagemAtualizada) {
        var mensagem = obterMensagem(id);
        if (!mensagem.getId().equals(mensagemAtualizada.getId())) {
            throw new MensagemNotFoundException("mensagem não apresenta o ID correto");
        }
        mensagem.setDataAlteracao(LocalDateTime.now());
        mensagem.setConteudo(mensagemAtualizada.getConteudo());
        return mensagemRepository.save(mensagem);
    }

    @Override
    public boolean removerMensagem(UUID id) {
        var mensagem = obterMensagem(id);
        mensagemRepository.delete(mensagem);
        return true;
    }

    @Override
    public Page<Mensagem> obterMensagens(Pageable pageable) {
        return mensagemRepository.obterMensagens(pageable);
    }

}
