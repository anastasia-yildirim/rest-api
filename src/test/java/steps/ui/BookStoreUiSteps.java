package steps.ui;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BookStoreUiSteps {

    @Step("Открыть профиль в UI")
    public void openProfile() {
        open("/profile");
    }

    @Step("Убедиться, что книга добавилась в профиль")
    public void checkBookIsAddedToProfile(String isbn) {
        $(".ReactTable").$("a[href='/profile?book=" + isbn + "']").shouldBe(visible);
    }

    @Step("Удалить книгу из профиля")
    public void deleteBookFromProfile() {
        $("#delete-record-undefined").scrollTo().click();
        $("#closeSmallModal-ok").click();
    }

    @Step("Убедиться, что книга не отображается в профиле")
    public void checkBookIsNotDisplayedInProfile(String isbn) {
        $(".ReactTable").$("a[href='/profile?book=" + isbn + "']").shouldNot(exist);
    }
}
