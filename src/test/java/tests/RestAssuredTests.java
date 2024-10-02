package tests;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class RestAssuredTests extends TestBase {

    @Test
    void successfulCreateUserTest() {
        String bodyData = "{\"name\": \"Ronald McDonald\", \"job\": \"Entertainment Manager\"}";

        given()
                .body(bodyData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post(baseURI + basePath + "/users")

                .then()
                .log().status()
                .log().body()
                .statusCode(201)
                .body("name", is("Ronald McDonald"))
                .body("job", is("Entertainment Manager"));
    }

    @Test
    void successfulUpdateUserTest() {
        String bodyData = "{\"name\": \"Ronald McDonald\", \"job\": \"Chief Entertainment Manager\"}";

        given()
                .body(bodyData)
                .contentType(JSON)
                .log().uri()

                .when()
                .put(baseURI + basePath + "/users/2")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("name", is("Ronald McDonald"))
                .body("job", is("Chief Entertainment Manager"));
    }

    @Test
    void singleUserNotFoundTest() {
        given()
                .log().uri()

                .when()
                .get(baseURI + basePath + "/users/23")

                .then()
                .log().status()
                .log().body()
                .statusCode(404);
    }

    @Test
    void successfulRegisterTest() {
        String bodyData = "{\"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";

        given()
                .body(bodyData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post(baseURI + basePath + "/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(200)
                .body("id", is(notNullValue()))
                .body("token.length()", greaterThan(10))
                .body("token", is(notNullValue()))
                .body("token", matchesPattern("^[a-zA-Z0-9]*$"));
    }

    @Test
    void unsuccessfulRegisterUndefinedUserTest() {
        String bodyData = "{\"email\": \"lewis.carol@reqres.in\", \"password\": \"alice\"}";

        given()
                .body(bodyData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post(baseURI + basePath + "/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Note: Only defined users succeed registration"));
    }

    @Test
    void unsuccessfulRegisterMissingPasswordTest() {
        String bodyData = "{\"email\": \"eveasdas.holt@reqres.in\"}";

        given()
                .body(bodyData)
                .contentType(JSON)
                .log().uri()

                .when()
                .post(baseURI + basePath + "/register")

                .then()
                .log().status()
                .log().body()
                .statusCode(400)
                .body("error", is("Missing password"));
    }
}