package cc.oofo.easytranslate.services.translator;

import org.jetbrains.annotations.NotNull;

public interface Translator {
    /**
     * 翻译文本
     *
     * @param text 要翻译的文本
     * @param from 源语言
     * @param to   目标语言
     * @return 翻译结果
     */
    String translate(@NotNull String text, @NotNull String from, @NotNull String to);

    /**
     * 获取翻译器名称
     */
    String getName();

    /**
     * 获取翻译器描述
     */
    String getDescription();

    /**
     * 检查配置是否有效
     */
    boolean isConfigured();
} 