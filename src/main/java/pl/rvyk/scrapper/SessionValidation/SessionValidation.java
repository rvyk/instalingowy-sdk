package pl.rvyk.scrapper.SessionValidation;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import pl.rvyk.Main;
import java.io.IOException;
import java.util.Objects;

public class SessionValidation {
    private boolean success;
    private String message;
    private String phpSessionID;
    public void validateSesion(String phpsessid, Callback callback) {
        OkHttpClient client = new OkHttpClient().newBuilder().followRedirects(false).build();
        Request validateSessionRequest = new Request.Builder()
                .url("https://instaling.pl/learning/dispatcher.php")
                .addHeader("User-Agent", Main.mozillaUserAgent)
                .addHeader("Cookie", phpsessid)
                .build();
        client.newCall(validateSessionRequest).enqueue(new Main.InstalingCallback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response validateResponse) throws IOException {
                if (Objects.requireNonNull(validateResponse.header("Location")).contains("logout.php")) {
                    success = false;
                    message = "Validation Failed";
                    phpSessionID = phpsessid;
                    callback.onResponse(call, validateResponse);
                    return;
                }
                success = true;
                message = "Validation Successfully";
                phpSessionID = phpsessid;
                callback.onResponse(call, validateResponse);
            }
        });
    }
    public String getPhpSessionID() {
        return phpSessionID;
    }
    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }
}
