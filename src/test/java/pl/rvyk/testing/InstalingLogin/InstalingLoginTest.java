package pl.rvyk.testing.InstalingLogin;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class InstalingLoginTest {
    private String phpSessionID;

    @Test
    public void testLoginSuccessCredentials() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "ezinst178", "tmdsi", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertTrue(instalingLogin.isSuccess());
                assertEquals("Login Successfully", instalingLogin.getMessage());
                phpSessionID = instalingLogin.getPhpSessionID();
                future.complete(null);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testLoginFailureCredentials() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "", "", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(instalingLogin.isSuccess());
                assertEquals("Login Failed", instalingLogin.getMessage());
                future.complete(null);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testLoginFailurePHPSessionID() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.PHPSESSIONID, null, null, "not valid phpsessid", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(instalingLogin.isSuccess());
                assertEquals("Login Failed", instalingLogin.getMessage());
                future.complete(null);
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testLoginSuccessPHPSessionID() throws InterruptedException, ExecutionException, TimeoutException {
        testLoginSuccessCredentials();
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.PHPSESSIONID, null, null, phpSessionID, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertTrue(instalingLogin.isSuccess());
                assertEquals("Login Successfully", instalingLogin.getMessage());
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
