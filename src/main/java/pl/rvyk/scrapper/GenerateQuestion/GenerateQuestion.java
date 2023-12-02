package pl.rvyk.scrapper.GenerateQuestion;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import pl.rvyk.Main;

import java.io.IOException;

public class GenerateQuestion {
    private boolean success;
    private boolean ended;
    private int instalingDays;
    private int words;
    private int correct;
    private String question;
    private String questionID;
    private String generatedWord;

    public void generate(String phpSessionId, String appId, String studentId, Callback callback) {

        String processedStudentId = studentId;
        if (studentId.contains("=")) {
            processedStudentId = studentId.split("=")[1];
        }

        RequestBody requestBody = new FormBody.Builder()
                .add("child_id", processedStudentId)
                .add("date", String.valueOf(System.currentTimeMillis()))
                .build();

        Request request = new Request.Builder()
                .url("https://instaling.pl/ling2/server/actions/generate_next_word.php")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("User-Agent", Main.mozillaUserAgent)
                .header("Cookie", phpSessionId + "; " + appId)
                .post(requestBody)
                .build();

        Main.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String generatedOutput = response.body().string();

                    JSONParser parser = new JSONParser();
                    try {
                        success = true;
                        JSONObject jsonObject = (JSONObject) parser.parse(generatedOutput);
                        questionID = (String) jsonObject.get("id");
                        if (questionID != null) {
                            ended = false;
                            question = (String) jsonObject.get("usage_example");
                            generatedWord = (String) jsonObject.get("translations");
                            callback.onResponse(call, response);
                            return;
                        }
                        instalingDays = Integer.parseInt(generatedOutput.split("\\n\\n")[0].split(": ")[1].split("\\n")[0]);
                        words = Integer.parseInt(generatedOutput.split("\\n\\n")[1].replace("\n", "").split(",")[0].split(" ")[1]);
                        correct = Integer.parseInt(generatedOutput.split("\\n\\n")[1].replace("\n", "").split(",")[1].split(" ")[1]);
                        callback.onResponse(call, response);
                    } catch (Exception e) {
                        callback.onFailure(call, new IOException("[GenerateQuestion] -> Error processing response"));
                    }
                } else {
                    success = false;
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                callback.onFailure(call, new IOException("[GenerateQuestion] -> Request failed"));
            }
        });
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isEnded() {
        return ended;
    }

    public int getInstalingDays() {
        return instalingDays;
    }

    public int getWords() {
        return words;
    }

    public int getCorrect() {
        return correct;
    }

    public String getQuestion() {
        return question;
    }

    public String getQuestionID() {
        return questionID;
    }

    public String getGeneratedWord() {
        return generatedWord;
    }

}
