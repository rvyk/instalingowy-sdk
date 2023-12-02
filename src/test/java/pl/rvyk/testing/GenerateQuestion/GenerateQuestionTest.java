package pl.rvyk.testing.GenerateQuestion;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.GenerateQuestion.GenerateQuestion;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class GenerateQuestionTest {
    @Test
    public void testGenerateQuestionSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "ezinst178", "tmdsi", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                GenerateQuestion generateQuestion = new GenerateQuestion();
                generateQuestion.generate(instalingLogin.getPhpSessionID(), instalingLogin.getAppID(), instalingLogin.getStudentID(), new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        assertTrue(generateQuestion.isSuccess());
                        future.complete(null);
                    }
                });
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testGenerateQuestionFailure() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        GenerateQuestion generateQuestion = new GenerateQuestion();
        generateQuestion.generate("", "", "", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(generateQuestion.isSuccess());
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
