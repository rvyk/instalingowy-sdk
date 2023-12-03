package pl.rvyk.testing.Homeworks;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.Homeworks.GetHomeworkDetails;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class GetHomeworkDetailsTest {
    @Test
    public void testGetHomeworksSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        InstalingLogin instalingLogin = new InstalingLogin();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "kubasz745", "wciez", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                GetHomeworkDetails homeworkDetails = new GetHomeworkDetails();
                homeworkDetails.details(instalingLogin.getPhpSessionID(), "homeworkPage.php?id=2313528&homework_id=21269", new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                        System.out.println(homeworkDetails.getTitle());
//                        System.out.println(homeworkDetails.getAnswer());
//                        System.out.println(homeworkDetails.getExercise());
//                        System.out.println(homeworkDetails.getNote());
//                        System.out.println(homeworkDetails.getGrade());
//                        System.out.println(homeworkDetails.isDone());
                        assertTrue(homeworkDetails.isSuccess());
                        future.complete(null);
                    }
                });
            }
        });
        future.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void testGetHomeworksFailure() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        GetHomeworkDetails homeworkDetails = new GetHomeworkDetails();
        homeworkDetails.details("", "", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                assertFalse(homeworkDetails.isSuccess());
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
