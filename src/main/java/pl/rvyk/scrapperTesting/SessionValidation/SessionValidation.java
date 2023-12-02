package pl.rvyk.scrapper.SessionValidation;

import okhttp3.*;
import pl.rvyk.Main;

import java.io.IOException;
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

        client.newCall(validateSessionRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(Call call, Response validateResponse) throws IOException {
                System.out.println(validateResponse.headers());
                if (validateResponse.header("Location").contains("logout.php")) {
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
