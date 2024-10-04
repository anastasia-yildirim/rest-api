package steps.api;

import data.Session;
import io.qameta.allure.Step;
import models.bookstore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static specs.BookStoreSpec.*;

public class BookStoreApiSteps {

    @Step("Удалить все книги из профиля")
    public void deleteAllBooksFromProfile(Session session) {
        given(bookStoreRequestSpec)
                .header("Authorization", "Bearer " + session.getToken())
                .queryParam("UserId", session.getUserId())
                .when()
                .delete("/BookStore/v1/Books")
                .then()
                .spec(bookStoreResponseSpec204);
    }

    @Step("Получить список книг, доступных в магазине")
    public List<BookModel> getBooksFromStore() {

        GetBooksFromStoreResponseModel response = step("Отправить запрос через API", () ->
                given(bookStoreRequestSpec)
                        .when()
                        .get("/BookStore/v1/books")
                        .then()
                        .spec(bookStoreResponseSpec200))
                .extract().as(GetBooksFromStoreResponseModel.class);
        step("Убедиться, что ответ не null", () -> {
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
    public void addBookToProfile(String isbn, Session session) {
        //Prepare
        BookModel selectedBook = new BookModel();
        selectedBook.setIsbn(isbn);
        List<BookModel> collectionOfIsbns = new ArrayList<>();
        collectionOfIsbns.add(selectedBook);
        AddBookToProfileRequestModel bodyData = new AddBookToProfileRequestModel();
        bodyData.setCollectionOfIsbns(collectionOfIsbns);
        bodyData.setUserId(session.getUserId());
        //Act
        AddBookToProfileResponseModel response = step("Отправить запрос на добавление книги в профиль", ()->
                given(bookStoreRequestSpec)
                        .header("Authorization", "Bearer " + session.getToken())
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

    @Step("Убедиться, что в профиле нет книг")
    public void checkProfileIsEmpty(Session session) {
        GetBooksFromProfileResponseModel response = step("Получить список книг в профиле по API", ()->
                given(bookStoreRequestSpec)
                        .when()
                        .header("Authorization", "Bearer " + session.getToken())
                        .get("/Account/v1/User/" + session.getUserId())
                        .then()
                        .spec(bookStoreResponseSpec200))
                .extract().as(GetBooksFromProfileResponseModel.class);
        step("Убедиться, что список пустой", ()-> {
            assertTrue(response.getBooks().isEmpty());
        });
    }
}