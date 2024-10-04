package tests;

import com.codeborne.selenide.Configuration;
import data.Session;
import helpers.LoginExtension;
import helpers.WithLogin;
import io.restassured.RestAssured;
import models.bookstore.BookModel;
import org.junit.jupiter.api.*;
import steps.api.BookStoreApiSteps;
import steps.ui.BookStoreUiSteps;

import java.util.List;

import static helpers.LoginExtension.clearSession;

@Tag("bookstore")
public class BookStoreTests {

    BookStoreApiSteps bookStoreApiSteps = new BookStoreApiSteps();
    BookStoreUiSteps bookStoreUiSteps = new BookStoreUiSteps();

    @BeforeAll
    static void prepare() {
        Configuration.baseUrl = System.getProperty("baseUrl", "https://demoqa.com");
        RestAssured.baseURI = "https://demoqa.com";
        Configuration.remote = System.getProperty("remote", "https://user1:1234@selenoid.autotests.cloud/wd/hub");
    }

    @AfterEach
    void clearData() {
        clearSession();
    }

    @WithLogin
    @DisplayName("Удаление книги из профиля")
    @Test
    void deleteBookFromProfileTest() {
        Session session = LoginExtension.getSession();
        bookStoreApiSteps.deleteAllBooksFromProfile(session);
        List<BookModel> books = bookStoreApiSteps.getBooksFromStore();
        String isbn = bookStoreApiSteps.selectRandomBook(books);
        bookStoreApiSteps.addBookToProfile(isbn, session);
        bookStoreUiSteps.openProfile();
        bookStoreUiSteps.checkBookIsAddedToProfile(isbn);
        bookStoreUiSteps.deleteBookFromProfile();
        bookStoreUiSteps.checkBookIsNotDisplayedInProfile(isbn);
        bookStoreApiSteps.checkProfileIsEmpty(session);
    }
}