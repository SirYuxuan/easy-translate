package cc.oofo.easytranslate.actions;

import cc.oofo.easytranslate.services.TranslationService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VariableNameAction extends AnAction {

    private enum NamingStyle {
        CAMEL_CASE("驼峰命名", true),
        PASCAL_CASE("帕斯卡命名", false),
        SNAKE_CASE("下划线命名", false),
        CONSTANT("常量命名", false);

        private final String displayName;
        private final boolean isDefault;

        NamingStyle(String displayName, boolean isDefault) {
            this.displayName = displayName;
            this.isDefault = isDefault;
        }

        public String format(String text) {
            // 先将文本转换为驼峰形式
            String[] words = text.split("[\\s_-]");
            String camelCase = Arrays.stream(words)
                .filter(s -> !s.isEmpty())
                .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase())
                .collect(Collectors.joining());

            switch (this) {
                case CAMEL_CASE:
                    return camelCase.substring(0, 1).toLowerCase() + camelCase.substring(1);
                case PASCAL_CASE:
                    return camelCase;
                case SNAKE_CASE:
                    return String.join("_", Arrays.stream(words)
                        .filter(s -> !s.isEmpty())
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()));
                case CONSTANT:
                    return String.join("_", Arrays.stream(words)
                        .filter(s -> !s.isEmpty())
                        .map(String::toUpperCase)
                        .collect(Collectors.toList()));
                default:
                    return text;
            }
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

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

        // 翻译中文为英文
        TranslationService translationService = project.getService(TranslationService.class);
        String translatedText = translationService.translate(selectedText, "zh", "en");
        
        // 生成不同风格的变量名
        List<String> suggestions = new ArrayList<>();
        for (NamingStyle style : NamingStyle.values()) {
            suggestions.add(style.format(translatedText));
        }

        // 显示建议列表
        BaseListPopupStep<String> popupStep = new BaseListPopupStep<>("选择变量命名", suggestions) {
            @Override
            public @Nullable PopupStep<?> onChosen(String selectedValue, boolean finalChoice) {
                if (finalChoice) {
                    WriteCommandAction.runWriteCommandAction(project, () -> {
                        int start = selectionModel.getSelectionStart();
                        int end = selectionModel.getSelectionEnd();
                        TextRange range = new TextRange(start, end);
                        editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), selectedValue);
                    });
                }
                return FINAL_CHOICE;
            }
        };

        JBPopupFactory.getInstance()
            .createListPopup(popupStep)
            .showInBestPositionFor(editor);
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
} 