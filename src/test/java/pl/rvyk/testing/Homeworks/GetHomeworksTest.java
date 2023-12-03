package pl.rvyk.testing.Homeworks;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import pl.rvyk.Main;
import pl.rvyk.scrapper.Homeworks.GetHomeworks;
import pl.rvyk.scrapper.InstalingLogin.InstalingLogin;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

public class GetHomeworksTest {

    private GetHomeworks.HomeworkResult result;

    @Test
    public void testGetHomeworksSuccess() throws InterruptedException, ExecutionException, TimeoutException {
        InstalingLogin instalingLogin = new InstalingLogin();
        CompletableFuture<Void> future = new CompletableFuture<>();
        instalingLogin.login(Main.Methods.LOGIN_PASSWORD, "kubasz745", "wciez", null, new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                GetHomeworks homeworks = new GetHomeworks();
                homeworks.getHomeworks(instalingLogin.getPhpSessionID(), instalingLogin.getAppID(), instalingLogin.getStudentID(), new TestCallback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        result = homeworks.getResult();

//                        List<HomeworkItem> homeworkTodo = result.getHomeworksTodo();
//                        System.out.println("TODO HOMEWORKS");
//                        for (HomeworkItem homework : homeworkTodo) {
//                            System.out.println(homework.getTitle());
//                            System.out.println(homework.getHomeworkLink());
//                            System.out.println(homework.getDeadline());
//                        }
//
//                        List<HomeworkItem> homeworkDone = result.getHomeworksDone();
//                        System.out.println("DONE HOMEWORKS");
//                        for (HomeworkItem homework : homeworkDone) {
//                            System.out.println(homework.getGrade());
//                            System.out.println(homework.getTitle());
//                            System.out.println(homework.getHomeworkLink());
//                            System.out.println(homework.getDeadline());
//                        }

                        assertTrue(result.isSuccess());
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
        GetHomeworks homeworks = new GetHomeworks();
        homeworks.getHomeworks("", "", "", new TestCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                result = homeworks.getResult();
                assertFalse(result.isSuccess());
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
