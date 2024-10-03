package tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import models.bookstore.BookModel;
import models.bookstore.LoginResponseModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import steps.BookStoreSteps;

import java.util.List;

import static data.TestData.login;
import static data.TestData.password;

@Tag("bookstore")
public class BookStoreTests {

    BookStoreSteps bookStoreSteps = new BookStoreSteps();

    @BeforeAll
    static void prepare() {
        Configuration.baseUrl = System.getProperty("baseUrl", "https://demoqa.com");
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.remote = System.getProperty("remote", "https://user1:1234@selenoid.autotests.cloud/wd/hub");
    }

    @DisplayName("Удаление книги из профиля")
    @Test
    void deleteBookFromProfileTest() {
        LoginResponseModel loginResponse = bookStoreSteps.performBookStoreLogin(login, password);
        bookStoreSteps.deleteAllBooksFromProfile(loginResponse);
        List<BookModel> books = bookStoreSteps.getBooksFromStore();
        String isbn = bookStoreSteps.selectRandomBook(books);
        bookStoreSteps.addBookToProfile(isbn, loginResponse);
        bookStoreSteps.openProfile(loginResponse);
        bookStoreSteps.checkBookIsAddedToProfile(isbn);
        bookStoreSteps.deleteBookFromProfile();
        bookStoreSteps.checkProfileIsEmpty(isbn, loginResponse);
    }
}