package cc.oofo.easytranslate.actions;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public class TextTransformAction extends AnAction {

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

        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new AnAction("转大写") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                replaceText(project, editor, selectedText.toUpperCase(Locale.ROOT));
            }
        });

        group.add(new AnAction("转小写") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                replaceText(project, editor, selectedText.toLowerCase(Locale.ROOT));
            }
        });

        group.add(new AnAction("首字母大写") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String result = Arrays.stream(selectedText.split("\\s+"))
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining(" "));
                replaceText(project, editor, result);
            }
        });

        group.add(new AnAction("转驼峰") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                String result = Arrays.stream(selectedText.split("[\\s_-]"))
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1).toLowerCase(Locale.ROOT))
                    .collect(Collectors.joining());
                // 首字母小写
                if (!result.isEmpty()) {
                    result = result.substring(0, 1).toLowerCase(Locale.ROOT) + result.substring(1);
                }
                replaceText(project, editor, result);
            }
        });

        group.add(new AnAction("转下划线") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                // 先处理驼峰
                String result = selectedText.replaceAll("([a-z])([A-Z])", "$1_$2");
                // 处理连续的大写字母
                result = result.replaceAll("([A-Z])([A-Z][a-z])", "$1_$2");
                // 转换为小写并替换其他分隔符为下划线
                result = result.toLowerCase(Locale.ROOT).replaceAll("[\\s-]", "_");
                // 清理多余的下划线
                result = result.replaceAll("_+", "_");
                replaceText(project, editor, result);
            }
        });

        ListPopup popup = JBPopupFactory.getInstance()
            .createActionGroupPopup(
                "文本转换",
                group,
                e.getDataContext(),
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                false
            );

        popup.showInBestPositionFor(editor);
    }

    private void replaceText(Project project, Editor editor, String newText) {
        SelectionModel selectionModel = editor.getSelectionModel();
        int start = selectionModel.getSelectionStart();
        int end = selectionModel.getSelectionEnd();
        TextRange range = new TextRange(start, end);

        WriteCommandAction.runWriteCommandAction(project, () -> {
            editor.getDocument().replaceString(range.getStartOffset(), range.getEndOffset(), newText);
        });

        // 保持文本选中状态
        selectionModel.setSelection(start, start + newText.length());
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