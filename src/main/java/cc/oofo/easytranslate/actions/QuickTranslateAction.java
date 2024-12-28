package cc.oofo.easytranslate.actions;

import cc.oofo.easytranslate.services.TranslationService;
import cc.oofo.easytranslate.settings.TranslateSettingsState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class QuickTranslateAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) {
            return;
        }

        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        String sourceLanguage;
        String targetLanguage = settings.targetLanguage;

        if (settings.autoDetectSourceLanguage) {
            // 如果启用了自动检测，则根据文本内容判断源语言
            sourceLanguage = containsChinese(selectedText) ? "zh" : "en";
            // 如果目标语言和检测到的源语言相同，则互换
            if (sourceLanguage.equals(targetLanguage)) {
                targetLanguage = sourceLanguage.equals("zh") ? "en" : "zh";
            }
        } else {
            sourceLanguage = settings.sourceLanguage;
        }

        TranslationService translationService = project.getService(TranslationService.class);
        String translatedText = translationService.translate(selectedText, sourceLanguage, targetLanguage);

        // 替换选中的文本
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        TextRange range = new TextRange(start, end);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), translatedText);
        });

        // 保持文本选中状态
        selectionModel.setSelection(start, start + translatedText.length());
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        e.getPresentation().setEnabledAndVisible(
            project != null && 
            editor != null && 
            editor.getSelectionModel().hasSelection()
        );
    }

    private boolean containsChinese(String text) {
        return text.codePoints().anyMatch(codepoint -> 
            Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN
        );
    }
} 