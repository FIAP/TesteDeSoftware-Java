package br.com.fiap.service;

import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.repository.MensagemRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MensagemServiceIT extends MensagemHelper{

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private MensagemService mensagemService;

    @Test
    void devePermitirRegistrarMensagem() {
        // Arrange
        var mensagem = gerarMensagem();
        // Act
        var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);
        // Assert
        assertThat(mensagemRegistrada)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemRegistrada.getUsuario())
                .isEqualTo(mensagem.getUsuario());
        assertThat(mensagemRegistrada.getId())
                .isNotNull();
        assertThat(mensagemRegistrada.getConteudo())
                .isEqualTo(mensagem.getConteudo());
    }

    @Test
    void devePermirirAlterarMensagem() {
        var mensagem = gerarMensagem();
        var mensagemOriginal = mensagemService.registrarMensagem(mensagem);
        var mensagemModificada = mensagemOriginal.toBuilder().build();
        mensagemModificada.setConteudo("abcd");

        var mensagemObtida = mensagemService.atualizarMensagem(mensagemOriginal.getId(),
                mensagemModificada);

        assertThat(mensagemObtida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemObtida.getId())
                .isEqualTo(mensagemModificada.getId());
        assertThat(mensagemObtida.getUsuario())
                .isEqualTo(mensagemModificada.getUsuario());
        assertThat(mensagemObtida.getConteudo())
                .isEqualTo(mensagemModificada.getConteudo());
    }


    @Test
    void devePermitirObterMensagem() {
        // Arrange
        var id = UUID.fromString("5f789b39-4295-42c1-a65b-cfca5b987db2");
        // Act
        var mensagemObtida = mensagemService.obterMensagem(id);
        // Assert
        assertThat(mensagemObtida)
                .isInstanceOf(Mensagem.class)
                .isNotNull();
        assertThat(mensagemObtida.getId())
                .isNotNull();
        assertThat(mensagemObtida.getUsuario())
                .isNotNull();
        assertThat(mensagemObtida.getConteudo())
                .isNotNull();
    }

    @Test
    void devePermitirRemoverMensagem() {
        // Arrange
        var id = UUID.fromString("65b1bbee-c784-4457-be6d-d00b0be5c9e0");
        // Act
        var mensagemRemovida = mensagemService.removerMensagem(id);
        // Assert
        assertThat(mensagemRemovida).isTrue();
    }

    @Test
    void devePermitirObterMensagens() {
        Page<Mensagem> mensagens = mensagemService.obterMensagens(Pageable.unpaged());

        assertThat(mensagens).hasSize(3);
        assertThat(mensagens.getContent())
                .asList()
                .allSatisfy(mensagem -> {
                    assertThat(mensagem).isNotNull();
                    assertThat(mensagem).isInstanceOf(Mensagem.class);
                });
    }


}
