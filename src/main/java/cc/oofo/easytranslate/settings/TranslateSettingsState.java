package cc.oofo.easytranslate.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
    name = "cc.oofo.easytranslate.settings.TranslateSettingsState",
    storages = @Storage("EasyTranslateSettings.xml")
)
@Service(Service.Level.PROJECT)
public final class TranslateSettingsState implements PersistentStateComponent<TranslateSettingsState> {
    public String targetLanguage = "zh";
    public String sourceLanguage = "auto";
    public boolean autoDetectSourceLanguage = true;
    
    // 翻译引擎选择
    public String selectedTranslator = "baidu";  // baidu, youdao 或 google
    
    // 百度翻译 API 配置
    public String baiduAppId = "";
    public String baiduSecretKey = "";

    // 有道翻译 API 配置
    public String youdaoAppKey = "";
    public String youdaoSecretKey = "";

    // 代理服务器配置（主要用于谷歌翻译）
    public boolean useProxy = false;
    public String proxyHost = "";
    public int proxyPort = 0;
    public String proxyUsername = "";
    public String proxyPassword = "";

    public String googleApiKey = "";

    @Override
    public @Nullable TranslateSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull TranslateSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
} 