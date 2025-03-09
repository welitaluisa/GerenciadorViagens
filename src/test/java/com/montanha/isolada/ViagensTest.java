package com.montanha.isolada;
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

    @Before
    public void setUp() {
        // Configurações Rest-Assured
        baseURI = "http://localhost";
        port = 8089;
        basePath = "/api";

        Usuario usuarioAdmin = UsuarioDataFactory.criarUsuarioAdministrador();


        // Realiza a solicitação HTTP
        this.token = given()
                .contentType("application/json")
                .body(usuarioAdmin)
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
    public void testCadastroDeViagemValidaContrato() throws IOException {

        // configurações Rest-Assured
        baseURI = "http://localhost";
        port = 8089;
        basePath = "/api";

        Usuario usuarioAdmin = UsuarioDataFactory.criarUsuarioAdministrador();

        String token = given()
                .contentType(ContentType.JSON)
                .body(usuarioAdmin)
        .when()
                .post("/v1/auth")
        .then()
                .extract()
                .path("data.token");

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
}