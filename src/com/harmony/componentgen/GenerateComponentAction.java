package com.harmony.componentgen;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * ArkTS 组件生成 Action：
 * 在 .ets 编辑器中通过 Generate（Alt+Insert）或右键菜单唤起，
 * 弹窗收集名称 / V1 / V2 / @Entry 等选项后把代码插入光标处。
 */
public class GenerateComponentAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
        boolean ets = file != null && file.getVirtualFile() != null
                && "ets".equalsIgnoreCase(file.getVirtualFile().getExtension());
        e.getPresentation().setEnabledAndVisible(editor != null && ets);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (project == null || editor == null) {
            return;
        }

        ComponentGeneratorDialog dialog = new ComponentGeneratorDialog();
        if (!dialog.showAndGet()) {
            return;
        }

        String code = CodeTemplateFactory.build(dialog.getOptions());
        Document document = editor.getDocument();
        CaretModel caret = editor.getCaretModel();

        // 依据光标所在行的前导缩进对生成代码整体缩进；
        // 若光标前同行已有代码，则先换行再插入，避免拼接在同一行
        int offset = caret.getOffset();
        int lineNumber = document.getLineNumber(offset);
        int lineStart = document.getLineStartOffset(lineNumber);
        String beforeCaret = document.getText().subSequence(lineStart, offset).toString();

        String linePrefix = document.getText().subSequence(
                lineStart, Math.min(document.getLineEndOffset(lineNumber), document.getTextLength())).toString();
        StringBuilder indent = new StringBuilder();
        for (char ch : linePrefix.toCharArray()) {
            if (ch == ' ' || ch == '\t') {
                indent.append(ch);
            } else {
                break;
            }
        }

        boolean lineHasCodeBeforeCaret = !beforeCaret.trim().isEmpty();
        String indented = indentEachLine(code, indent.toString());
        String insertion = (lineHasCodeBeforeCaret ? "\n" : "") + indented + "\n";

        WriteCommandAction.runWriteCommandAction(project, () ->
                document.insertString(offset, insertion));
    }

    private static String indentEachLine(String code, String indent) {
        String[] lines = code.split("\n", -1);
        StringBuilder sb = new StringBuilder(code.length() + lines.length * indent.length());
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            if (!lines[i].isEmpty()) {
                sb.append(indent).append(lines[i]);
            }
        }
        return sb.toString();
    }
}
