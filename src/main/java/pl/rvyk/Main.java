package pl.rvyk;

import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
public class Main {
    public static final String mozillaUserAgent =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.5359.125 Safari/537.36";
    public enum Methods {
        LOGIN_PASSWORD,
        PHPSESSIONID
    }
    public static abstract class InstalingCallback implements Callback {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            try {
                throw new Exception(e);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}