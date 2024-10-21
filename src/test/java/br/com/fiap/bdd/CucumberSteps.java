package br.com.fiap.bdd;

import br.com.fiap.helper.MensagemHelper;
import br.com.fiap.model.Mensagem;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;

public class CucumberSteps extends MensagemHelper{

    private final String ENDPOINT_MENSAGENS = "/mensagens";
    private Response response;
    private Mensagem mensagemResposta;

    @Before
    public void setup() {
        RestAssured.port = Integer.parseInt(System.getProperty("port"));
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Quando("submeter uma nova mensagem")
    public Mensagem submeter_uma_nova_mensagem() {
        var payload = gerarMensagem();
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(payload)
                .when()
                .post(ENDPOINT_MENSAGENS);
        return response.then().extract().as(Mensagem.class);

    }

    @Então("a mensagem é registrada com sucesso")
    public void a_mensagem_é_registrada_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Dado("que uma mensagem já foi registrada")
    public void que_uma_mensagem_já_foi_registrada() {
        mensagemResposta = submeter_uma_nova_mensagem();
    }

    @Quando("buscar a mensagem registrada")
    public void buscar_a_mensagem_registrada() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(ENDPOINT_MENSAGENS + "/{id}", mensagemResposta.getId().toString());
    }

    @Então("a mensagem é exibida com sucesso")
    public void a_mensagem_é_exibida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

    @Quando("requisitar a remoção da mensagem")
    public void requisitar_a_remoção_da_mensagem() {
        response = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete(ENDPOINT_MENSAGENS + "/{id}", mensagemResposta.getId().toString());
    }

    @Então("a mensagem é removida com sucesso")
    public void a_mensagem_é_removida_com_sucesso() {
        response.then()
                .statusCode(HttpStatus.OK.value());
    }

}
