package pl.rvyk.testing.SessionValidation;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;
import pl.rvyk.scrapper.SessionValidation.SessionValidation;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.Assert.*;
public class SessionValidationTest {
    @Test
    public void testSessionValidationSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "ezinst178", "tmdsi", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                SessionValidation sessionValidation = new SessionValidation();
                sessionValidation.validateSesion(instalingLogin.getPhpSessionID(), new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        assertTrue(sessionValidation.isSuccess());
                        assertEquals("Validation Successfully", sessionValidation.getMessage());
                        future.complete(null);
                    }
                });
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }
    @Test
    public void testSessionValidationFailure() throws InterruptedException, ExecutionException, TimeoutException {
        SessionValidation sessionValidation = new SessionValidation();
        CompletableFuture<Void> future = new CompletableFuture<>();
        sessionValidation.validateSesion("", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(sessionValidation.isSuccess());
                assertEquals("Validation Failed", sessionValidation.getMessage());
                future.complete(null);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }
    private static abstract class TestCallback implements Callback {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            fail("Callback onFailure called unexpectedly");
        }
    }
}
