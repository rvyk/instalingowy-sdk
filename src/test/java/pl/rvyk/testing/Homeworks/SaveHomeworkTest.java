package pl.rvyk.testing.Homeworks;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.Homeworks.SaveHomework;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class SaveHomeworkTest {
    @Test
    public void testSaveHomeworkSuccess() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "kubasz745", "wciez", null, new TestCallback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                SaveHomework homework = new SaveHomework();

                // save field options: save, save_and_send
                homework.save(instalingLogin.getPhpSessionID(), "homeworkPage.php?id=2313528&homework_id=21269", "pancakes are really nice", "save", new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        assertTrue(homework.isSuccess());
                        future.complete(null);
                    }
                });
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testSaveHomeworkFailure() throws InterruptedException, ExecutionException, TimeoutException, IOException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        SaveHomework homework = new SaveHomework();

        homework.save("", "homeworkPage.php?id=not_valid_studentid&homework_id=some_random_id", "pancakes are really nice", "save", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                assertFalse(homework.isSuccess());
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
