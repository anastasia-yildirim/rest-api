package helpers;

import models.Session;
import models.bookstore.response.LoginResponseModel;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import steps.api.BookStoreAuthorizationApi;

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
            Session newSession = new Session();
            LoginResponseModel authResponse = BookStoreAuthorizationApi.getAuthorization();
            newSession.setUserId(authResponse.getUserId());
            newSession.setToken(authResponse.getToken());
            newSession.setExpires(authResponse.getExpires());
            session.set(newSession);

            BookStoreAuthorizationApi.buildAuthorizationCookie(session.get());
    }
}