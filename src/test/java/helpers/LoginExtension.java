package helpers;

import data.Session;
import models.bookstore.LoginResponseModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import steps.api.BookStoreAuthorizationApi;

import static io.qameta.allure.Allure.step;

public class LoginExtension implements BeforeEachCallback {

    private static final ThreadLocal<Session> session = new ThreadLocal<>();

    public static Session getSession() {
        return session.get();
    }

    public static void clearSession() {
        session.remove();
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        step("Авторизация и сохранение сессии", () -> {
            Session newSession = new Session();
            LoginResponseModel authResponse = BookStoreAuthorizationApi.getAuthorization();
            newSession.setUserId(authResponse.getUserId());
            newSession.setToken(authResponse.getToken());
            newSession.setExpires(authResponse.getExpires());
            session.set(newSession);
        });
        step("Генерация куки для UI", () -> {
            BookStoreAuthorizationApi.buildAuthorizationCookie(session.get());
        });
    }
}