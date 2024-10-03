package tests;

import io.restassured.RestAssured;
import models.reqres.register.RegisterRequestModel;
import models.reqres.register.RegisterResponseModel;
import models.reqres.register.UnsuccessfulRegisterResponseModel;
import models.reqres.register.user.CreateUpdateUserRequestModel;
import models.reqres.register.user.CreateUpdateUserResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static specs.DefaultReqresSpec.defaultRequestSpec;
import static specs.DefaultReqresSpec.defaultResponseSpec;

@Tag("reqres")
public class ReqresTests extends TestBase {

    @BeforeAll
    static void prepare() {
        RestAssured.baseURI ="https://reqres.in";
        RestAssured.basePath ="/api";
    }

    @DisplayName("Успешное создание пользователя")
    @Test
    void successfulCreateUserTest() {
        CreateUpdateUserRequestModel bodyData = new CreateUpdateUserRequestModel();
        bodyData.setName("Ronald McDonald");
        bodyData.setJob("Entertainment Manager");

        CreateUpdateUserResponseModel response = step("Make request", ()->
                given(defaultRequestSpec)
                    .body(bodyData)
                .when()
                    .post("/users")
                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(201)
                    .extract().as(CreateUpdateUserResponseModel.class));

        step("Check response", ()-> {
            assertEquals(bodyData.getName(), response.getName());
            assertEquals(bodyData.getJob(), response.getJob());
        });
    }

    @DisplayName("Успешное обновление данных пользователя")
    @Test
    void successfulUpdateUserTest() {
        CreateUpdateUserRequestModel bodyData = new CreateUpdateUserRequestModel();
        bodyData.setName("Ronald McDonald");
        bodyData.setJob("Chief Entertainment Manager");

        CreateUpdateUserResponseModel response = step("Make request", ()->
                given(defaultRequestSpec)
                    .body(bodyData)
                .when()
                    .put("/users/2")
                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(200)
                    .extract().as(CreateUpdateUserResponseModel.class));

        step("Check response", ()-> {
        assertEquals(bodyData.getName(), response.getName());
        assertEquals(bodyData.getJob(), response.getJob());
        });
    }

    @DisplayName("Пользователь не найден")
    @Test
    void userNotFoundTest() {
        step("Make request and check 404 is returned", ()->
                given(defaultRequestSpec)
                .when()
                    .get("/users/23")
                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(404));
    }

    @DisplayName("Успешная регистрация")
    @Test
    void successfulRegisterTest() {
        RegisterRequestModel bodyData = new RegisterRequestModel();
        bodyData.setEmail("eve.holt@reqres.in");
        bodyData.setPassword("pistol");

        RegisterResponseModel response = step("Make request", ()->
                given(defaultRequestSpec)
                    .body(bodyData)
                .when()
                    .post("/register")
                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(200)
                    .extract().as(RegisterResponseModel.class));

        step("Check response", ()-> {
        assertNotEquals(null, response.getId());
        assertThat(response.getToken().length(), greaterThan(10));
        assertThat(response.getToken(), is(notNullValue()));
        assertThat(response.getToken(), matchesPattern("^[a-zA-Z0-9]+$"));
        });
    }

    @DisplayName("Неуспешная регистрация - невалидный пользователь")
    @Test
    void unsuccessfulRegisterUndefinedUserTest() {
        RegisterRequestModel bodyData = new RegisterRequestModel();
        bodyData.setEmail("lewis.carol@reqres.in");
        bodyData.setPassword("alice");

        String expectedErrorText = "Note: Only defined users succeed registration";

        UnsuccessfulRegisterResponseModel response = step("Make request", ()->
                given(defaultRequestSpec)
                    .body(bodyData)
                .when()
                    .post("/register")
                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(400)
                    .extract().as(UnsuccessfulRegisterResponseModel.class));

        step("Check response", ()-> assertEquals(expectedErrorText, response.getError()));
    }

    @DisplayName("Неуспешная регистрация - отсутствует пароль")
    @Test
    void unsuccessfulRegisterMissingPasswordTest() {
        RegisterRequestModel bodyData = new RegisterRequestModel();
        bodyData.setEmail("eveasdas.holt@reqres.in");
        bodyData.setPassword(null);

        String expectedErrorText = "Missing password";

        UnsuccessfulRegisterResponseModel response = step("Make request", ()->
                given(defaultRequestSpec)
                    .body(bodyData)
                .when()
                    .post("/register")

                .then()
                    .spec(defaultResponseSpec)
                    .statusCode(400)
                    .extract().as(UnsuccessfulRegisterResponseModel.class));

        step("Check response", ()-> assertEquals(expectedErrorText, response.getError()));
    }
}