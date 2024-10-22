package steps.ui;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BookStoreUiSteps {

    @Step("Открыть профиль в UI")
    public void openProfile() {
        open("/profile");
    }

    @Step("Удалить книгу из профиля")
    public void deleteBookFromProfile(String isbn) {
        $(".ReactTable").$("a[href='/profile?book=" + isbn + "']").shouldBe(visible);
        $("#delete-record-undefined").scrollTo().click();
        $("#closeSmallModal-ok").click();
    }
}