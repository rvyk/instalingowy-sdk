package pl.rvyk.scrapper.InstalingLogin;

import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.rvyk.Main;
import pl.rvyk.scrapper.SessionValidation.SessionValidation;

import java.io.IOException;
import java.util.List;


public class InstalingLogin {
    private boolean success;
    private boolean todaySessionCompleted;
    private String message;
    private String studentID;
    private String buttonText;
    private String instalingVersion;
    private String phpSessionID;
    private String appID;
    private int homeworkCount = 0;

    OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();

    public void login(Main.Methods loginMethod, String email, String password, String phpsessid, Callback callback) {
        if (loginMethod.equals(Main.Methods.LOGIN_PASSWORD)) {
            Request loginRequest = createLoginRequest(email, password);

            client.newCall(loginRequest).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response loginResponse) throws IOException {
                    handleLoginResponse(call, loginResponse, callback);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onFailure(call, e);
                }
            });
        } else {
            phpSessionID = phpsessid;

            Request loginRequest = createDispatcherRequest();

            client.newCall(loginRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    SessionValidation sessionValidation = new SessionValidation();

                    sessionValidation.validateSesion(phpSessionID, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (sessionValidation.isSuccess()) {
                                handleDispatcherResponse(call, response, callback);
                            } else {
                                success = sessionValidation.isSuccess();
                                message = sessionValidation.getMessage();
                            }
                        }
                    });
                }
            });
        }
    }

    private Request createLoginRequest(String email, String password) {
        FormBody loginRequestBody = new FormBody.Builder()
                .add("action", "login")
                .add("log_email", email)
                .add("log_password", password)
                .build();

        return new Request.Builder()
                .url("https://instaling.pl/teacher.php?page=teacherActions")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", Main.mozillaUserAgent)
                .post(loginRequestBody)
                .build();
    }

    private void handleLoginResponse(Call call, Response loginResponse, Callback callback) throws IOException {
        if (!loginResponse.header("Location").startsWith("learning/dispatcher")) {
            success = false;
            message = "Login Failed";
            callback.onResponse(call, loginResponse);
            return;
        }

        List<String> setCookieHeaders = loginResponse.headers("Set-Cookie");
        phpSessionID = setCookieHeaders.get(0).split(";")[0];

        Request dispatcherRequest = createDispatcherRequest();

        client.newCall(dispatcherRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response dispatcherResponse) throws IOException {
                handleDispatcherResponse(call, dispatcherResponse, callback);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }
        });
    }

    private Request createDispatcherRequest() {
        return new Request.Builder()
                .url("https://instaling.pl/learning/dispatcher.php")
                .header("User-Agent",  Main.mozillaUserAgent)
                .header("Cookie", phpSessionID)
                .build();
    }

    private void handleDispatcherResponse(Call call, Response dispatcherResponse, Callback callback) throws IOException {
        success = true;
        message = "Login Successfully";
        appID = "app=app_84";

        if (dispatcherResponse.headers("Set-Cookie") != null) {
            appID = dispatcherResponse.header("Set-Cookie").split(";")[0];
        }

        studentID = dispatcherResponse.header("Location").split("\\?")[1];

        Request mainPageRequest = createMainPageRequest();

        client.newCall(mainPageRequest).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response mainPageResponse) throws IOException {
                handleMainPageResponse(call, mainPageResponse, callback);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }
        });
    }


    private Request createMainPageRequest() {
        return new Request.Builder()
                .url("https://instaling.pl/student/pages/mainPage.php?" + studentID)
                .header("User-Agent",  Main.mozillaUserAgent)
                .header("Cookie", phpSessionID + "; ")
                .build();
    }

    private void handleMainPageResponse(Call call, Response mainPageResponse, Callback callback) throws IOException {
        Document document = Jsoup.parse(mainPageResponse.body().string());

        buttonText = document.select(".btn-session").text();
        todaySessionCompleted = document.select("#student_panel > h4").size() > 0;

        instalingVersion = document.select("#footer > div > p.span4.text-center").text();
        String homeworkCountText = document.select("#student_panel > div.alert.alert-info > strong").text();
        String[] homeworkCountArray = homeworkCountText.split(":");

        if (homeworkCountArray.length > 1) {
            homeworkCount = Integer.parseInt(homeworkCountArray[1].replace(" ", "").trim());
        }

        callback.onResponse(call, mainPageResponse);
    }

    public boolean isSuccess() {
        return success;
    }
    public int getHomeworkCount() {
        return homeworkCount;
    }

    public boolean isTodaySessionCompleted() {
        return todaySessionCompleted;
    }

    public String getButtonText() {
        return buttonText;
    }

    public String getInstalingVersion() {
        return instalingVersion;
    }

    public String getAppID() {
        return appID;
    }

    public String getMessage() {
        return message;
    }

    public String getPhpSessionID() {
        return phpSessionID;
    }

    public String getStudentID() {
        return studentID;
    }
}
