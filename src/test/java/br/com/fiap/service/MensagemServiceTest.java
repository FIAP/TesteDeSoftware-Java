package br.com.fiap.service;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.repository.MensagemRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MensagemServiceTest extends  MensagemHelper {

    @Mock
    private MensagemRepository mensagemRepository;
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        mensagemService = new MensagemServiceImp(mensagemRepository);
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() {
            var mensagem = gerarMensagem();
            when(mensagemRepository.save(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            var mensagemRegistrada = mensagemService.registrarMensagem(mensagem);

            assertThat(mensagemRegistrada)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
            assertThat(mensagemRegistrada.getUsuario())
                    .isEqualTo(mensagem.getUsuario());
            assertThat(mensagemRegistrada.getId())
                    .isNotNull();
            assertThat(mensagemRegistrada.getConteudo())
                    .isEqualTo(mensagem.getConteudo());
            verify(mensagemRepository, times(1)).save(mensagem);
        }
    }

    @Nested
    class ObterMensagem {

        @Test
        void devePermitirObterMensagem() {
            var id = UUID.randomUUID();
            var mensagem = gerarMensagem();
            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(mensagem));

            var mensagemObtida = mensagemService.obterMensagem(id);

            verify(mensagemRepository, times(1))
                    .findById(id);
            assertThat(mensagemObtida)
                    .isEqualTo(mensagem);
            assertThat(mensagemObtida.getId())
                    .isEqualTo(mensagem.getId());
            assertThat(mensagemObtida.getUsuario())
                    .isEqualTo(mensagem.getUsuario());
            assertThat(mensagemObtida.getConteudo())
                    .isEqualTo(mensagem.getConteudo());
            assertThat(mensagemObtida.getDataCriacao())
                    .isEqualTo(mensagem.getDataCriacao());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
            var id = UUID.randomUUID();

            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> mensagemService.obterMensagem(id))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("mensagem não encontrada");
            verify(mensagemRepository, times(1)).findById(id);
        }
    }

    @Nested
    class AtualizarMensagem {

        @Test
        void devePermirirAtualizarMensagem() {
            var id = UUID.randomUUID();
            var mensagemAntiga = gerarMensagem();
            mensagemAntiga.setId(id);
            var mensagemNova = mensagemAntiga;
            mensagemNova.setConteudo("abcd");

            when(mensagemRepository.findById(any(UUID.class)))
                    .thenReturn(Optional.of(mensagemAntiga));

            when(mensagemRepository.save(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));

            var mensagemObtida = mensagemService
                    .atualizarMensagem(id, mensagemNova);

            assertThat(mensagemObtida)
                    .isInstanceOf(Mensagem.class)
                    .isNotNull();
            assertThat(mensagemObtida.getId())
                    .isEqualTo(mensagemNova.getId());
            assertThat(mensagemObtida.getUsuario())
                    .isEqualTo(mensagemNova.getUsuario());
            assertThat(mensagemObtida.getConteudo())
                    .isEqualTo(mensagemNova.getConteudo());
            verify(mensagemRepository, times(1)).save(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAtualizarMensagem_IdNaoCoincide() {
            var id = UUID.randomUUID();
            var mensagemAntiga = gerarMensagem();
            mensagemAntiga.setId(id);
            var mensagemNova = mensagemAntiga.toBuilder().build();
            mensagemNova.setId(UUID.randomUUID());

            assertThatThrownBy(
                    () -> mensagemService.atualizarMensagem(id, mensagemNova))
                    .isInstanceOf(MensagemNotFoundException.class)
                    .hasMessage("mensagem não encontrada");
            verify(mensagemRepository, never()).save(any(Mensagem.class));
        }

    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = UUID.fromString("51fa607a-1e61-11ee-be56-0242ac120002");
            var mensagem = gerarMensagem();
            mensagem.setId(id);
            when(mensagemRepository.findById(id))
                    .thenReturn(Optional.of(mensagem));
            doNothing()
                    .when(mensagemRepository).deleteById(id);

            var resultado = mensagemService.removerMensagem(id);

            assertThat(resultado).isTrue();
            verify(mensagemRepository, times(1)).findById(any(UUID.class));
            verify(mensagemRepository, times(1)).delete(any(Mensagem.class));
        }

    }


    @Nested
    class ObterMensagens {

        @Test
        void devePermitirObterMensagens() {
            Page<Mensagem> page = new PageImpl<>(Arrays.asList(
                    gerarMensagem(),
                    gerarMensagem()
            ));

            when(mensagemRepository.obterMensagens(any(Pageable.class)))
                    .thenReturn(page);

            Page<Mensagem> mensagens = mensagemService.obterMensagens(Pageable.unpaged());

            assertThat(mensagens).hasSize(2);
            assertThat(mensagens.getContent())
                    .asList()
                    .allSatisfy(mensagem -> {
                        assertThat(mensagem).isNotNull();
                        assertThat(mensagem).isInstanceOf(Mensagem.class);
                    });
            verify(mensagemRepository, times(1)).obterMensagens(any(Pageable.class));
        }

        @Test
        void devePermitirObterMensagens_QuandoNaoExisteRegistro() {
            Page<Mensagem> page = new PageImpl<>(Collections.emptyList());

            when(mensagemRepository.obterMensagens(any(Pageable.class)))
                    .thenReturn(page);

            Page<Mensagem> mensagens = mensagemService.obterMensagens(Pageable.unpaged());

            assertThat(mensagens).isEmpty();
            verify(mensagemRepository, times(1)).obterMensagens(any(Pageable.class));
        }
    }

}
