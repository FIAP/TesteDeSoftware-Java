package br.com.fiap.controller;

import br.com.fiap.exception.MensagemNotFoundException;
import br.com.fiap.handler.GlobalExceptionHandler;
import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import br.com.fiap.service.MensagemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MensagemControllerTest extends MensagemHelper {

    private MockMvc mockMvc;

    @Mock
    private MensagemService mensagemService;

    AutoCloseable mock;

    @BeforeEach
    void setup() {
        mock = MockitoAnnotations.openMocks(this);
        MensagemController mensagemController = new MensagemController(mensagemService);
        mockMvc = MockMvcBuilders.standaloneSetup(mensagemController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }

    @AfterEach
    void teardown() throws Exception {
        mock.close();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() throws Exception {
            // Arrange
            var mensagemRequest = gerarMensagem();
            when(mensagemService.registrarMensagem(any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(0));
            // Act + Assert
            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isCreated());
            verify(mensagemService, times(1)).registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBraco() throws Exception {
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setUsuario("");

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("usuário não pode estar vazio"));
            verify(mensagemService, never())
                    .registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_ConteudoEmBranco() throws Exception {
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setConteudo("");

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Validation error"))
                    .andExpect(jsonPath("$.errors.[0]").value("conteúdo da mensagem não pode estar vazio"));
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadComXml() throws Exception {
            String xmlPayload = "<mensagem><usuario>John</usuario><conteudo>Conteúdo da mensagem</conteudo></mensagem>";

            mockMvc.perform(post("/mensagens")
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).registrarMensagem(any(Mensagem.class));
        }
    }

    @Nested
    class ObterMensagem {

        @Test
        void devePermitirObterMensagem() throws Exception {
            // Arrange
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            var mensagemResponse = gerarMensagem();
            mensagemResponse.setId(id);
            mensagemResponse.setDataCriacao(LocalDateTime.now());
            mensagemResponse.setDataAlteracao(LocalDateTime.now());

            when(mensagemService.obterMensagem(any(UUID.class))).thenReturn(mensagemResponse);

            // Act + Assert
            mockMvc.perform(get("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
            verify(mensagemService, times(1)).obterMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecaoAoObterMensagemComIdNaoExistente() throws Exception {
            // Arrange
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            when(mensagemService.obterMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));

            // Act + Assert
            mockMvc.perform(get("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
            verify(mensagemService, times(1)).obterMensagem(any(UUID.class));

        }

        @Test
        void deveGerarExcecaoAoObterMensagemComIdInvalido() throws Exception {
            // Arrange
            var id = "123";

            // Act + Assert
            mockMvc.perform(get("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
            verify(mensagemService, times(0)).obterMensagem(any(UUID.class));
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermirirAlterarMensagem() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setId(id);

            when(mensagemService.atualizarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenAnswer(i -> i.getArgument(1));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(mensagemRequest.getId().toString()))
                    .andExpect(jsonPath("$.conteudo").value(mensagemRequest.getConteudo()))
                    .andExpect(jsonPath("$.usuario").value(mensagemRequest.getUsuario()))
                    .andExpect(jsonPath("$.dataCriacao").value(mensagemRequest.getDataCriacao()))
                    .andExpect(jsonPath("$.gostei").value(mensagemRequest.getGostei()));
            verify(mensagemService, times(1))
                    .atualizarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoCoincide() throws Exception {
            var id = "259bdc02-1ab5-11ee-be56-0242ac120002";
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setId(UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120003"));

            when(mensagemService.atualizarMensagem(any(UUID.class), any(Mensagem.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não apresenta o ID correto"));

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("mensagem não apresenta o ID correto"));
            verify(mensagemService, times(1)).atualizarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdInvalido() throws Exception {
            var id = "2";
            var mensagemRequest = gerarMensagem();

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(mensagemRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(mensagemService, never())
                    .atualizarMensagem(any(UUID.class), any(Mensagem.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadComXml() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            String xmlPayload = "<mensagem><usuario>John</usuario><conteudo>Conteúdo da mensagem</conteudo></mensagem>";

            mockMvc.perform(put("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_XML)
                            .content(xmlPayload))
                    .andExpect(status().isUnsupportedMediaType());
            verify(mensagemService, never()).atualizarMensagem(any(UUID.class), any(Mensagem.class));
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() throws Exception {
            var id = UUID.fromString("259bdc02-1ab5-11ee-be56-0242ac120002");
            when(mensagemService.removerMensagem(any(UUID.class)))
                    .thenReturn(true);

            mockMvc.perform(delete("/mensagens/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(content().string("mensagem removida"));
            verify(mensagemService, times(1))
                    .removerMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdInvalido()
                throws Exception {
            var id = "2";

            mockMvc.perform(delete("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("ID inválido"));
            verify(mensagemService, never())
                    .removerMensagem(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExistente()
                throws Exception {
            var id = UUID.randomUUID();

            when(mensagemService.removerMensagem(any(UUID.class)))
                    .thenThrow(new MensagemNotFoundException("mensagem não encontrada"));

            mockMvc.perform(delete("/mensagens/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("mensagem não encontrada"));
            verify(mensagemService, times(1))
                    .removerMensagem(any(UUID.class));
        }

    }


    @Nested
    class ObterMensagens {

        @Test
        void devePermitirListarMensagens() throws Exception {
            var mensagem = MensagemHelper.gerarMensagemCompleta();
            Page<Mensagem> page = new PageImpl<>(Collections.singletonList(
                    mensagem
            ));
            when(mensagemService.obterMensagens(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(mensagem.getId().toString()))
                    .andExpect(jsonPath("$.content[0].conteudo").value(mensagem.getConteudo()))
                    .andExpect(jsonPath("$.content[0].usuario").value(mensagem.getUsuario()))
                    .andExpect(jsonPath("$.content[0].dataCriacao").exists())
                    .andExpect(jsonPath("$.content[0].gostei").exists());
            verify(mensagemService, times(1))
                    .obterMensagens(any(Pageable.class));
        }

        @Test
        void devePermitirListarMensagens_QuandoNaoExisteRegistro()
                throws Exception {
            Page<Mensagem> page = new PageImpl<>(Collections.emptyList());
            when(mensagemService.obterMensagens(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/mensagens")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", empty()))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            verify(mensagemService, times(1))
                    .obterMensagens(any(Pageable.class));
        }

        @Test
        void devePermitirListarMensagens_QuandoReceberParametrosInvalidos()
                throws Exception {
            Page<Mensagem> page = new PageImpl<>(Collections.emptyList());
            when(mensagemService.obterMensagens(any(Pageable.class)))
                    .thenReturn(page);
            mockMvc.perform(get("/mensagens?page=2&ping=pong")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", empty()))
                    .andExpect(jsonPath("$.content", hasSize(0)));
            verify(mensagemService, times(1)).obterMensagens(any(Pageable.class));
        }
    }
    
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
