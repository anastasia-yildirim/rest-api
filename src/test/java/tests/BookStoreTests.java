package tests;

import com.codeborne.selenide.Configuration;
import models.Session;
import helpers.LoginExtension;
import helpers.WithLogin;
import io.restassured.RestAssured;
import models.bookstore.BookModel;
import org.junit.jupiter.api.*;
import steps.api.BookStoreApiSteps;
import steps.ui.BookStoreUiSteps;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.$;
import static helpers.LoginExtension.clearSession;
import static org.junit.jupiter.api.Assertions.*;

@Tag("bookstore")
public class BookStoreTests extends TestBase {

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
        //Arrange
        Session session = LoginExtension.getSession();
        List<BookModel> books;
        String isbn;
        List<BookModel> collectionOfIsbns = new ArrayList<>();

        //Act
        bookStoreApiSteps.deleteAllBooksFromProfile(session);
        books = bookStoreApiSteps.getBooksFromStore();
        assertNotEquals(null, books);

        isbn = bookStoreApiSteps.selectRandomBook(books);
        bookStoreApiSteps.addBookToIsbnCollection(isbn, collectionOfIsbns);
        books = bookStoreApiSteps.addBookToProfile(collectionOfIsbns, session);
        assertEquals(collectionOfIsbns, books);

        bookStoreUiSteps.openProfile();
        bookStoreUiSteps.deleteBookFromProfile(isbn);

        //Assert
        $(".ReactTable").$("a[href='/profile?book=" + isbn + "']").shouldNot(exist);
        books = bookStoreApiSteps.getBooksFromProfile(session);
        assertTrue(books.isEmpty());
    }
}