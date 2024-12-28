package cc.oofo.easytranslate.services.translator;

import cc.oofo.easytranslate.settings.TranslateSettingsState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class YoudaoTranslator implements Translator {
    private static final String API_URL = "https://openapi.youdao.com/api";
    private final Project project;
    private final OkHttpClient client;

    public YoudaoTranslator(Project project) {
        this.project = project;
        this.client = new OkHttpClient();
    }

    @Override
    public String translate(@NotNull String text, @NotNull String from, @NotNull String to) {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        if (!isConfigured()) {
            return "请在设置中配置有道翻译 API 凭证";
        }

        String salt = UUID.randomUUID().toString();
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        String input = text.length() <= 20 ? text : text.substring(0, 10) + text.length() + text.substring(text.length() - 10);
        String sign = generateSign(settings.youdaoAppKey, input, salt, curtime, settings.youdaoSecretKey);

        RequestBody formBody = new FormBody.Builder()
            .add("q", text)
            .add("from", from)
            .add("to", to)
            .add("appKey", settings.youdaoAppKey)
            .add("salt", salt)
            .add("sign", sign)
            .add("signType", "v3")
            .add("curtime", curtime)
            .build();

        Request request = new Request.Builder()
            .url(API_URL)
            .post(formBody)
            .build();

        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return "翻译失败: " + (response.body() != null ? response.body().string() : "未知错误");
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

            if (jsonResponse.has("errorCode") && !"0".equals(jsonResponse.get("errorCode").getAsString())) {
                return "翻译失败: " + jsonResponse.get("errorCode").getAsString();
            }

            return jsonResponse.getAsJsonArray("translation")
                .get(0).getAsString();

        } catch (IOException e) {
            return "翻译失败: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "有道翻译";
    }

    @Override
    public String getDescription() {
        return "有道智云翻译服务，支持多语种在线翻译";
    }

    @Override
    public boolean isConfigured() {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        return settings != null && 
               !settings.youdaoAppKey.isEmpty() && 
               !settings.youdaoSecretKey.isEmpty();
    }

    private String generateSign(String appKey, String input, String salt, String curtime, String secretKey) {
        String source = appKey + input + salt + curtime + secretKey;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(source.getBytes(StandardCharsets.UTF_8));
            StringBuilder sign = new StringBuilder();
            for (byte b : bytes) {
                sign.append(String.format("%02x", b & 0xff));
            }
            return sign.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成签名失败", e);
        }
    }
} 