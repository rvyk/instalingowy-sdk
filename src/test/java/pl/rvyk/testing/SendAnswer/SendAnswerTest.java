package pl.rvyk.testing.SendAnswer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import pl.rvyk.Main;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;
import pl.rvyk.scrapper.SendAnswer.SendAnswer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class SendAnswerTest {
    @Test
    @DisplayName("Should return grade 1 and 4")
    public void testSendingAnswersSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "ezinst178", "tmdsi", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                SendAnswer sendAnswer = new SendAnswer();
                sendAnswer.send(instalingLogin.getPhpSessionID(), instalingLogin.getAppID(), instalingLogin.getStudentID(), "2137", "cushion", new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        assertEquals(Long.valueOf(1), sendAnswer.getGrade());
                        assertTrue(sendAnswer.isSuccess());
                        future.complete(null);
                    }
                });

                sendAnswer.send(instalingLogin.getPhpSessionID(), instalingLogin.getAppID(), instalingLogin.getStudentID(), "2137", "Cushion", new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        assertEquals(Long.valueOf(3), sendAnswer.getGrade());
                        assertTrue(sendAnswer.isSuccess());
                        future.complete(null);
                    }
                });


            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Should return success = false")
    public void testSendingAnswersFailure() throws InterruptedException, ExecutionException, TimeoutException {
        SendAnswer sendAnswer = new SendAnswer();
        CompletableFuture<Void> future = new CompletableFuture<>();
        sendAnswer.send("", "", "", "2137", "cushio", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(sendAnswer.isSuccess());
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
