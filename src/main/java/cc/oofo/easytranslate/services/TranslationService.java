package cc.oofo.easytranslate.services;

import cc.oofo.easytranslate.services.translator.BaiduTranslator;
import cc.oofo.easytranslate.services.translator.GoogleTranslator;
import cc.oofo.easytranslate.services.translator.Translator;
import cc.oofo.easytranslate.services.translator.YoudaoTranslator;
import cc.oofo.easytranslate.dictionary.DictionaryService;
import cc.oofo.easytranslate.settings.TranslateSettingsState;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Service(Service.Level.PROJECT)
public final class TranslationService {
    private final Project project;
    private final Map<String, Translator> translators;

    public TranslationService(Project project) {
        this.project = project;
        this.translators = Map.of(
            "baidu", new BaiduTranslator(project),
            "youdao", new YoudaoTranslator(project),
            "google", new GoogleTranslator(project)
        );
    }

    public String translate(@NotNull String text, @NotNull String from, @NotNull String to) {
        // 先应用词典替换
        DictionaryService dictionaryService = project.getService(DictionaryService.class);
        String processedText = dictionaryService.processText(text);

        // 如果词典处理后的文本与原文相同，则使用翻译服务
        if (processedText.equals(text)) {
            TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
            Translator translator = translators.get(settings.selectedTranslator);
            
            if (translator == null) {
                return "未知的翻译引擎: " + settings.selectedTranslator;
            }

            if (!translator.isConfigured()) {
                return "请在设置中配置" + translator.getName() + "的相关设置";
            }

            return translator.translate(text, from, to);
        }

        return processedText;
    }
} 