package pl.rvyk.scrapper.InitSession;

import okhttp3.*;
import pl.rvyk.Main;
import java.io.IOException;
public class InitSession {
    private boolean success;
    public void init(String phpSessionId, String appId, String studentId, Callback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
        String processedStudentId = studentId;
        if (studentId.contains("=")) {
            processedStudentId = studentId.split("=")[1];
        }
        RequestBody requestBody = new FormBody.Builder()
                .add("child_id", processedStudentId)
                .add("repeat", "")
                .add("start", "")
                .add("end", "")
                .build();
        Request request = new Request.Builder()
                .url("https://instaling.pl/ling2/server/actions/init_session.php")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", Main.mozillaUserAgent)
                .header("Cookie", phpSessionId + "; " + appId)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Main.InstalingCallback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                success = response.isSuccessful();
                callback.onResponse(call, response);
            }
        });
    }
    public boolean isSuccess() {
        return success;
    }
}
