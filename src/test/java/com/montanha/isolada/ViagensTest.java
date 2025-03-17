package com.montanha.isolada;
import com.montanha.config.Configuracoes;
import org.aeonbits.owner.ConfigFactory;
import org.junit.Before;
import org.junit.Test;

import com.montanha.factory.UsuarioDataFactory;
import com.montanha.factory.ViagemDataFactory;
import com.montanha.pojo.Usuario;
import com.montanha.pojo.Viagem;
import io.restassured.http.ContentType;
import java.io.IOException;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static io.restassured.module.jsv.JsonSchemaValidator.*;

public class ViagensTest {
    private String token;
    private String tokenUsuario;

    @Before
    public void setUp() {
        // Configurações Rest-Assured
        Configuracoes configuracoes = ConfigFactory.create(Configuracoes.class);
        baseURI = configuracoes.baseURI();
        port = configuracoes.port();
        basePath = configuracoes.basePath();

        Usuario usuarioAdmin = UsuarioDataFactory.criarUsuarioAdministrador();
        // Realiza a solicitação HTTP
        this.token = given()
                .contentType("application/json")
                .body(usuarioAdmin)
                .when()
                .post("/v1/auth")
                .then()
                .extract().path("data.token"); // Extrai o token do corpo da resposta

        Usuario usuarioComum = UsuarioDataFactory.criarUsuarioComum();

        this.tokenUsuario = given()
                .contentType("application/json")
                .body(usuarioComum)
                .when()
                .post("/v1/auth")
                .then()
                .extract().path("data.token"); // Extrai o token do corpo da resposta

    }

    @Test
    public void testCadastroDeViagemValidaRetornaSucesso() throws IOException {

        Viagem viagemValida = ViagemDataFactory.criarViagemValida();

        given()
                .contentType(ContentType.JSON)
                .body(viagemValida)
                .header("Authorization", token)
                .when()
                .post("/v1/viagens")
                .then()
                .assertThat()
                .statusCode(201)
                .body("data.localDeDestino", equalTo("Salvador"))
                .body("data.acompanhante", equalToIgnoringCase("welita"));

    }

    @Test
    public void testViagemNaoPodemSerCadastradasSemLocalDestino() throws IOException {

        Viagem viagemSemLocalDeDestino = ViagemDataFactory.criarViagemSemLocalDeDestino();

        given()
                .contentType(ContentType.JSON)
                .body(viagemSemLocalDeDestino)
                .header("Authorization", token)
                .when()
                .post("/v1/viagens")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @Test
    public void testCadastroDeViagemValidaContrato() throws IOException {

        Viagem viagemValida = ViagemDataFactory.criarViagemValida();

        given()
                .contentType(ContentType.JSON)
                .body(viagemValida)
                .header("Authorization", token)
        .when()
                .post("/v1/viagens")
        .then()
                .assertThat()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/postV1ViagensValida.json"));
    }

    @Test
    public void testRetornaUmaViagemPossuiStatusCode200EMostraLocalDeDestino() {
        given()
                .header("Authorization", tokenUsuario)
                .when()
                .get("/v1/viagens/1")
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.localDeDestino", equalTo("Osasco"));
    }

    @Test
    public void testViagemProcessaCorretamenteRetornoDaApiDoTempo() {
        given()
                .header("Authorization", tokenUsuario)
        .when()
                .get("/v1/viagens/1")
        .then()
                .assertThat()
                .statusCode(200)
                .body("data.temperatura", equalTo(35.5f));
    }

    @Test
    public void testViagemProcessaCorretamenteRetornoDaApiDoTempoComErro() {
        given()
                .header("Authorization", tokenUsuario)
                .when()
                .get("/v1/viagens/1")
                .then()
                .assertThat()
                .statusCode(201)
                .body("data.temperatura", equalTo(35.5f));
    }
}
