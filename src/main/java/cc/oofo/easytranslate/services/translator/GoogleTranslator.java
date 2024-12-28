package cc.oofo.easytranslate.services.translator;

import cc.oofo.easytranslate.settings.TranslateSettingsState;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.project.Project;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleTranslator implements Translator {
    private static final String API_URL = "https://translate.googleapis.com/translate_a/single";
    private final Project project;
    private final OkHttpClient client;

    public GoogleTranslator(Project project) {
        this.project = project;
        
        // 创建带代理的 HTTP 客户端
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        
        if (settings != null && settings.useProxy && 
            !settings.proxyHost.isEmpty() && settings.proxyPort > 0) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, 
                new InetSocketAddress(settings.proxyHost, settings.proxyPort));
            builder.proxy(proxy);
            
            // 如果有代理认证
            if (!settings.proxyUsername.isEmpty() && !settings.proxyPassword.isEmpty()) {
                builder.proxyAuthenticator((route, response) -> {
                    String credential = Credentials.basic(settings.proxyUsername, settings.proxyPassword);
                    return response.request().newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build();
                });
            }
        }
        
        this.client = builder.build();
    }

    @Override
    public String translate(@NotNull String text, @NotNull String from, @NotNull String to) {
        if (!isConfigured()) {
            return "请在设置中配置代理服务器（如果需要）";
        }

        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = String.format("%s?client=gtx&sl=%s&tl=%s&dt=t&q=%s",
                API_URL, from.equals("auto") ? "" : from, to, encodedText);

            Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", "Mozilla/5.0")
                .get()
                .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful() || response.body() == null) {
                return "翻译失败: " + (response.body() != null ? response.body().string() : "未知错误");
            }

            String responseBody = response.body().string();
            // Google翻译返回的是一个嵌套数组，第一个数组包含翻译结果
            StringBuilder translatedText = new StringBuilder();
            JsonParser.parseString(responseBody).getAsJsonArray()
                .get(0).getAsJsonArray()
                .forEach(element -> {
                    if (!element.isJsonNull() && element.getAsJsonArray().size() > 0) {
                        translatedText.append(element.getAsJsonArray().get(0).getAsString());
                    }
                });

            return translatedText.toString();

        } catch (IOException e) {
            return "翻译失败: " + e.getMessage();
        }
    }

    @Override
    public String getName() {
        return "谷歌翻译";
    }

    @Override
    public String getDescription() {
        return "谷歌翻译服务，支持多语种在线翻译（可能需要代理）";
    }

    @Override
    public boolean isConfigured() {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        // 如果设置了使用代理，则需要检查代理配置
        return settings != null && (!settings.useProxy || 
               (!settings.proxyHost.isEmpty() && settings.proxyPort > 0));
    }
} 