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
import java.util.Random;

public class BaiduTranslator implements Translator {
    private static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private final Project project;
    private final OkHttpClient client;

    public BaiduTranslator(Project project) {
        this.project = project;
        this.client = new OkHttpClient();
    }

    @Override
    public String translate(@NotNull String text, @NotNull String from, @NotNull String to) {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        if (!isConfigured()) {
            return "请在设置中配置百度翻译 API 凭证";
        }

        String salt = String.valueOf(new Random().nextInt(100000));
        String sign = generateSign(text, salt, settings.baiduAppId, settings.baiduSecretKey);

        RequestBody formBody = new FormBody.Builder()
            .add("q", text)
            .add("from", from)
            .add("to", to)
            .add("appid", settings.baiduAppId)
            .add("salt", salt)
            .add("sign", sign)
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

            if (jsonResponse.has("error_code")) {
                return "翻译失败: " + jsonResponse.get("error_msg").getAsString();
            }

            return jsonResponse.getAsJsonArray("trans_result")
                .get(0).getAsJsonObject()
                .get("dst").getAsString();

        } catch (IOException e) {
            return "翻译失败: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "百度翻译";
    }

    @Override
    public String getDescription() {
        return "百度翻译开放平台，支持多种语言互译";
    }

    @Override
    public boolean isConfigured() {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        return settings != null && 
               !settings.baiduAppId.isEmpty() && 
               !settings.baiduSecretKey.isEmpty();
    }

    private String generateSign(String text, String salt, String appId, String secretKey) {
        String source = appId + text + salt + secretKey;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
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