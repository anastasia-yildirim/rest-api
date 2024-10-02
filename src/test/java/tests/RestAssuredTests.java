package tests;

import models.register.RegisterBodyModel;
import models.register.RegisterResponseModel;
import models.register.UnsuccessfulRegisterResponseModel;
import models.user.UserBodyModel;
import models.user.UserResponseModel;
import org.junit.jupiter.api.Test;

import static helpers.CustomAllureListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class RestAssuredTests extends TestBase {

    @Test
    void successfulCreateUserTest() {
        UserBodyModel bodyData = new UserBodyModel();
        bodyData.setName("Ronald McDonald");
        bodyData.setJob("Entertainment Manager");

        UserResponseModel response = step("Make request", ()->
                given()
                    .filter(withCustomTemplates())
                    .log().uri()
                    .log().body()
                    .log().headers()
                    .body(bodyData)
                    .contentType(JSON)
                .when()
                    .post("/users")
                .then()
                    .log().status()
                    .log().body()
                    .statusCode(201)
                    .extract().as(UserResponseModel.class));

        step("Check response", ()-> {
            assertEquals(bodyData.getName(), response.getName());
            assertEquals(bodyData.getJob(), response.getJob());
        });
    }

    @Test
    void successfulUpdateUserTest() {
        UserBodyModel bodyData = new UserBodyModel();
        bodyData.setName("Ronald McDonald");
        bodyData.setJob("Chief Entertainment Manager");

        UserResponseModel response = step("Make request", ()->
                given()
                    .body(bodyData)
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .log().headers()
                .when()
                    .put("/users/2")
                .then()
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .extract().as(UserResponseModel.class));

        step("Check response", ()-> {
        assertEquals(bodyData.getName(), response.getName());
        assertEquals(bodyData.getJob(), response.getJob());
        });
    }

    @Test
    void singleUserNotFoundTest() {
        step("Make request and check 404 is returned", ()->
                given()
                    .log().uri()
                    .log().body()
                    .log().headers()
                .when()
                    .get("/users/23")
                .then()
                    .log().status()
                    .log().body()
                    .statusCode(404));

    }

    @Test
    void successfulRegisterTest() {
        RegisterBodyModel bodyData = new RegisterBodyModel();
        bodyData.setEmail("eve.holt@reqres.in");
        bodyData.setPassword("pistol");

        RegisterResponseModel response = step("Make request", ()->
                given()
                    .body(bodyData)
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .log().headers()
                .when()
                    .post("/register")
                .then()
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .extract().as(RegisterResponseModel.class));

        step("Check response", ()-> {
        assertNotEquals(null, response.getId());
        assertThat(response.getToken().length(), greaterThan(10));
        assertThat(response.getToken(), is(notNullValue()));
        assertThat(response.getToken(), matchesPattern("^[a-zA-Z0-9]+$"));
        });
    }

    @Test
    void unsuccessfulRegisterUndefinedUserTest() {
        RegisterBodyModel bodyData = new RegisterBodyModel();
        bodyData.setEmail("lewis.carol@reqres.in");
        bodyData.setPassword("alice");

        String expectedErrorText = "Note: Only defined users succeed registration";

        UnsuccessfulRegisterResponseModel response = step("Make request", ()->
                given()
                    .body(bodyData)
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .log().headers()
                .when()
                    .post("/register")
                .then()
                    .log().status()
                    .log().body()
                    .statusCode(400)
                    .extract().as(UnsuccessfulRegisterResponseModel.class));

        step("Check response", ()-> assertEquals(expectedErrorText, response.getError()));
    }

    @Test
    void unsuccessfulRegisterMissingPasswordTest() {
        RegisterBodyModel bodyData = new RegisterBodyModel();
        bodyData.setEmail("eveasdas.holt@reqres.in");
        bodyData.setPassword(null);

        String expectedErrorText = "Missing password";

        UnsuccessfulRegisterResponseModel response = step("Make request", ()->
                given()
                    .body(bodyData)
                    .contentType(JSON)
                    .log().uri()
                    .log().body()
                    .log().headers()

                .when()
                    .post("/register")

                .then()
                    .log().status()
                    .log().body()
                    .statusCode(400)
                    .extract().as(UnsuccessfulRegisterResponseModel.class));

        step("Check response", ()-> assertEquals(expectedErrorText, response.getError()));
    }
}