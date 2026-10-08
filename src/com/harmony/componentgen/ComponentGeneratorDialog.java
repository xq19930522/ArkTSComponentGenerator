package com.harmony.componentgen;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBRadioButton;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * 组件生成对话框：组件名称 + V1/V2 模式选择 + @Entry 页面 + 状态变量开关，
 * 交互参照 Flutter 的 "New Widget" 对话框。
 */
public class ComponentGeneratorDialog extends DialogWrapper {

    private final JBTextField nameField = new JBTextField("MyComponent");
    private final JBRadioButton v1Radio = new JBRadioButton("@Component (V1 状态管理)", true);
    private final JBRadioButton v2Radio = new JBRadioButton("@ComponentV2 (V2 状态管理)");
    private final JBCheckBox entryCheckBox = new JBCheckBox("生成 @Entry 页面入口", false);
    private final JBCheckBox stateCheckBox = new JBCheckBox("携带示例状态变量（@State / @Local）", true);

    public ComponentGeneratorDialog() {
        super(true);
        setTitle("New ArkTS Component");
        ButtonGroup group = new ButtonGroup();
        group.add(v1Radio);
        group.add(v2Radio);
        init();
        // 名称变化时把 OK 按钮交给标准校验；这里只做首字母大写联想之外的纯校验
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { }
            @Override public void removeUpdate(DocumentEvent e) { }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
        nameField.selectAll();
        nameField.grabFocus();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0; c.gridy = 0;
        panel.add(new JLabel("Component name:"), c);
        c.gridx = 1;
        panel.add(nameField, c);

        c.gridx = 0; c.gridy = 1;
        panel.add(new JLabel("Component mode:"), c);
        c.gridx = 1;
        JPanel radios = new JPanel(new GridBagLayout());
        GridBagConstraints rc = new GridBagConstraints();
        rc.anchor = GridBagConstraints.WEST;
        rc.gridx = 0; rc.gridy = 0;
        radios.add(v1Radio, rc);
        rc.gridy = 1;
        radios.add(v2Radio, rc);
        panel.add(radios, c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        panel.add(entryCheckBox, c);
        c.gridy = 3;
        panel.add(stateCheckBox, c);

        v1Radio.addActionListener(e -> updateStateHint());
        v2Radio.addActionListener(e -> updateStateHint());
        updateStateHint();
        return panel;
    }

    private void updateStateHint() {
        stateCheckBox.setText(v2Radio.isSelected()
                ? "携带示例状态变量（@Local）"
                : "携带示例状态变量（@State）");
    }

    @Override
    protected void doOKAction() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (!name.matches("[A-Za-z_$][A-Za-z0-9_$]*")) {
            setErrorText("组件名称必须是合法 ArkTS 标识符");
            return;
        }
        super.doOKAction();
    }

    public TemplateOptions getOptions() {
        String raw = nameField.getText().trim();
        String name = raw.substring(0, 1).toUpperCase() + raw.substring(1);
        return new TemplateOptions(name, v2Radio.isSelected(),
                entryCheckBox.isSelected(), stateCheckBox.isSelected());
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return nameField;
    }
}
