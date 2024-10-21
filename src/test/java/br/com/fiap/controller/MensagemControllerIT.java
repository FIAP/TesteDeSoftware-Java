package br.com.fiap.controller;

import br.com.fiap.helper.MensagemHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"/db_load.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class MensagemControllerIT extends MensagemHelper {

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class RegistrarMensagem {

        @Test
        void devePermitirRegistrarMensagem() {
            var mensagemRequest = gerarMensagem();
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagemRequest)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("$", hasKey("id"))
                    .body("$", hasKey("usuario"))
                    .body("$", hasKey("conteudo"))
                    .body("$", hasKey("gostei"))
                    .body("$", hasKey("dataCriacao"))
                    .body("usuario", equalTo(mensagemRequest.getUsuario()))
                    .body("conteudo", equalTo(mensagemRequest.getConteudo()));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_UsuarioEmBranco() {
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setUsuario("");

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagemRequest)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("message"))
                    .body("$", hasKey("errors"))
                    .body("message", equalTo("Validation error"))
                    .body("errors[0]", equalTo("usuário não pode estar vazio"));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_ConteudoEmBranco() {
            var mensagemRequest = gerarMensagem();
            mensagemRequest.setConteudo("");

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagemRequest)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("$", hasKey("message"))
                    .body("$", hasKey("errors"))
                    .body("message", equalTo("Validation error"))
                    .body("errors[0]", equalTo("conteúdo da mensagem não pode estar vazio"));
        }


        @Test
        void deveGerarExcecao_QuandoRegistrarMensagem_PayloadComXml() {
            String xmlPayload = "<mensagem><usuario>John</usuario><conteudo>Conteúdo da mensagem</conteudo></mensagem>";

            given()
                    .contentType(MediaType.APPLICATION_XML_VALUE)
                    .body(xmlPayload)
                    .when()
                    .post("/mensagens")
                    .then()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }
    }

    @Nested
    class ObterMensagem {
        @Test
        public void devePermitirObterMensagem() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";

            when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("$", hasKey("id"))
                    .body("$", hasKey("usuario"))
                    .body("$", hasKey("conteudo"))
                    .body("$", hasKey("gostei"))
                    .body("$", hasKey("dataCriacao"))
                    .body("usuario", equalTo("Jose"))
                    .body("conteudo", equalTo("mensagem de José"));

        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdNaoExistente() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(equalTo("mensagem não encontrada"));
        }

        @Test
        void deveGerarExcecao_QuandoBuscarMensagem_IdInvalido() {
            var id = "2";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("ID Inválido"));
        }
    }

    @Nested
    class AlterarMensagem {

        @Test
        void devePermirirAlterarMensagem() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
            var mensagem = MensagemHelper.gerarMensagemCompleta();
            mensagem.setId(UUID.fromString(id));

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("conteudo", equalTo(mensagem.getConteudo()));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdNaoCoincide() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
            var mensagem = MensagemHelper.gerarMensagemCompleta();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(equalTo("mensagem não apresenta o ID correto"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_IdInvalido() {
            var id = "5";
            var mensagem = MensagemHelper.gerarMensagemCompleta();

            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(mensagem)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("ID inválido"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarMensagem_PayloadComXml() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
            String xmlPayload = "<mensagem><usuario>John</usuario><conteudo>Conteúdo da mensagem</conteudo></mensagem>";

            given()
                    .contentType(ContentType.XML)
                    .body(xmlPayload)
                    .when()
                    .put("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }
    }

    @Nested
    class RemoverMensagem {

        @Test
        void devePermitirRemoverMensagem() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db2";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(equalTo("mensagem removida"));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdNaoExistente() {
            var id = "5f789b39-4295-42c1-a65b-cfca5b987db3";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body(equalTo("mensagem não encontrada"));
        }

        @Test
        void deveGerarExcecao_QuandoRemoverMensagem_IdInvalido() {
            var id = "2";
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .delete("/mensagens/{id}", id)
                    .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body(equalTo("ID inválido"));
        }
    }

    @Nested
    class ObterMensagens {

        @Test
        void devePermitirListarMensagens() {
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("number", equalTo(0))
                    .body("size", equalTo(10))
                    .body("totalElements", equalTo(3));
        }

        @Test
        void devePermitirListarMensagens_QuandoInformadoParametros() {
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .queryParam("page", "2")
                    .queryParam("size", "2")
                    .when()
                    .get("/mensagens")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("number", equalTo(2))
                    .body("size", equalTo(2))
                    .body("totalElements", equalTo(3));
        }

        @Test
        @Sql(scripts = {"/db_clean.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
        void devePermitirListarMensagens_QuandoNaoExisteRegistro() {
            given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get("/mensagens")
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("number", equalTo(0))
                    .body("size", equalTo(10))
                    .body("totalElements", equalTo(0));
        }
    }

}
