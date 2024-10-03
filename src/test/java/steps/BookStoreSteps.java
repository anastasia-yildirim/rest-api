package steps;

import io.qameta.allure.Step;

import models.bookstore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static specs.BookStoreSpec.*;

public class BookStoreSteps {

    @Step("Авторизоваться в профиле")
    public LoginResponseModel performBookStoreLogin(String userName, String password) {
        generateToken(userName, password);

        return logInAndGetResponse(userName, password);
    }

    @Step("Создать токен")
    public void generateToken(String userName, String password) {
        GenerateTokenRequestModel bodyData = new GenerateTokenRequestModel();
        bodyData.setUserName(userName);
        bodyData.setPassword(password);

        GenerateTokenResponseModel response = step("Отправить запрос на генерацию токена", ()->
                given(bookStoreRequestSpec)
                        .body(bodyData)
                        .when()
                        .post("/Account/v1/GenerateToken")
                        .then()
                        .spec(bookStoreResponseSpec200)
                        .extract().as(GenerateTokenResponseModel.class));

        step("Проверить ответ", ()-> {
            assertEquals("Success", response.getStatus());
            assertEquals("User authorized successfully.", response.getResult());
        });
    }

    @Step("Залогиниться")
    public LoginResponseModel logInAndGetResponse(String userName, String password) {
        LoginRequestModel bodyData = new LoginRequestModel();
        bodyData.setUserName(userName);
        bodyData.setPassword(password);

        LoginResponseModel response = step("Залогиниться через АПИ", ()->
                given(bookStoreRequestSpec)
                        .body(bodyData)
                        .when()
                        .post("/Account/v1/Login")
                        .then()
                        .spec(bookStoreResponseSpec200)
                        .extract().as(LoginResponseModel.class));

        step("Проверить ответ", ()-> {
            assertThat(response.getUserId().isEmpty(), is(false));
        });

        return response;
    }

    @Step("Удалить все книги из профиля")
    public void deleteAllBooksFromProfile(LoginResponseModel loginResponse) {
        given(bookStoreRequestSpec)
                .header("Authorization", "Bearer " + loginResponse.getToken())
                .queryParam("UserId", loginResponse.getUserId())
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .spec(bookStoreResponseSpec204);
    }

    @Step("Получить список книг, доступных в магазине")
    public List<BookModel> getBooksFromStore() {

        GetBooksFromStoreResponseModel response = step("Отправить запрос", ()->
                given(bookStoreRequestSpec)
                        .when()
                        .get("/BookStore/v1/books")
                        .then()
                        .spec(bookStoreResponseSpec200))
                .extract().as(GetBooksFromStoreResponseModel.class);
        step("Проверить ответ", ()-> {
            assertNotEquals(null, response);
        });

        return response.getBooks();
    }

    @Step("Выбрать любую книгу")
    public String selectRandomBook(List<BookModel> books) {
        Random random = new Random();
        int randomIndex = random.nextInt(books.size());
        BookModel randomBook = books.get(randomIndex);

        return randomBook.getIsbn();
    }

    @Step("Добавить выбранную книгу в профиль")
    public void addBookToProfile(String isbn, LoginResponseModel loginResponse) {
        //Prepare
        BookModel selectedBook = new BookModel();
        selectedBook.setIsbn(isbn);
        List<BookModel> collectionOfIsbns = new ArrayList<>();
        collectionOfIsbns.add(selectedBook);
        AddBookToProfileRequestModel bodyData = new AddBookToProfileRequestModel();
        bodyData.setCollectionOfIsbns(collectionOfIsbns);
        bodyData.setUserId(loginResponse.getUserId());
        //Act
        AddBookToProfileResponseModel response = step("Отправить запрос на добавление книги в профиль", ()->
                given(bookStoreRequestSpec)
                        .header("Authorization", "Bearer " + loginResponse.getToken())
                        .body(bodyData)
                        .when()
                        .post("/BookStore/v1/Books")
                        .then()
                        .spec(bookStoreResponseSpec201)
                        .extract().as(AddBookToProfileResponseModel.class));
        //Assert
        step("В ответе должен вернуться ISBN, переданный в запросе", ()-> {
            assertEquals(bodyData.getCollectionOfIsbns(), response.getBooks());
        });
    }

    //ui
    @Step("Открыть профиль в UI")
    public void openProfile(LoginResponseModel loginResponse) {
        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie("userID", loginResponse.getUserId()));
        getWebDriver().manage().addCookie(new Cookie("token", loginResponse.getToken()));
        getWebDriver().manage().addCookie(new Cookie("expires", loginResponse.getExpires()));
        open("/profile");
    }

    //ui
    @Step("Убедиться, что книга добавилась в профиль")
    public void checkBookIsAddedToProfile(String isbn) {
        $("a[href='/profile?book=" + isbn + "']").shouldBe(visible);
    }

    //ui
    @Step("Удалить книгу из профиля")
    public void deleteBookFromProfile() {
        $("#delete-record-undefined").click();
        $("#closeSmallModal-ok").click();
    }

    //api
    @Step("Убедиться, что книга отсутствует в профиле")
    public void checkProfileIsEmpty(String isbn, LoginResponseModel loginResponse) {
        step("Проверить через UI, что книга не отображается в профиле", ()-> {
            $("a[href='/profile?book=" + isbn + "']").shouldNot(exist);
        });
        GetBooksFromProfileResponseModel response = step("Получить список книг в профиле по API", ()->
                given(bookStoreRequestSpec)
                        .when()
                        .header("Authorization", "Bearer " + loginResponse.getToken())
                        .get("/Account/v1/User/" + loginResponse.getUserId())
                        .then()
                        .spec(bookStoreResponseSpec200))
                .extract().as(GetBooksFromProfileResponseModel.class);
        step("Убедиться, что список пустой", ()-> {
            assertTrue(response.getBooks().isEmpty());
        });
    }
}