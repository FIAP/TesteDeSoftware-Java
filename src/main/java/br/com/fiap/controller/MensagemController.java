package br.com.fiap.controller;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.model.Mensagem;
import br.com.fiap.service.MensagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemController {

    private final MensagemService mensagemService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Mensagem> registrarMensagem(@Valid @RequestBody Mensagem mensagem) {
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        return new ResponseEntity<>(mensagemRegistrada, HttpStatus.CREATED);
    }

    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> buscarMensagem(@PathVariable String id) {
        try {
            var uid = UUID.fromString(id);
            var mensagemEncontrada = mensagemService.obterMensagem(uid);
            return new ResponseEntity<>(mensagemEncontrada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID Inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> atualizarMensagem(
            @PathVariable String id,
            @RequestBody @Valid Mensagem mensagem) {
        try {
            var uuid = UUID.fromString(id);
            var mensagemAtualizada = mensagemService.atualizarMensagem(uuid, mensagem);
            return new ResponseEntity<>(mensagemAtualizada, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerMensagem(@PathVariable String id) {
        try {
            var uuid = UUID.fromString(id);
            mensagemService.removerMensagem(uuid);
            return new ResponseEntity<>("mensagem removida", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID inválido");
        } catch (MensagemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(
            value = "",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Mensagem>> obterMensagens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Mensagem> mensagens = mensagemService.obterMensagens(pageable);
        return new ResponseEntity<>(mensagens, HttpStatus.OK);
    }
}
