package cc.oofo.easytranslate.settings;

import cc.oofo.easytranslate.dictionary.DictionaryEntry;
import cc.oofo.easytranslate.dictionary.DictionaryService;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranslateSettingsConfigurable implements Configurable {
    private final Project project;
    private JPanel mainPanel;
    private ComboBox<String> targetLanguageComboBox;
    private ComboBox<String> sourceLanguageComboBox;
    private ComboBox<String> translatorComboBox;
    private JBCheckBox autoDetectSourceLanguageCheckBox;
    private JBCheckBox useProxyCheckBox;
    private JBTextField baiduAppIdField;
    private JBTextField baiduSecretKeyField;
    private JBTextField youdaoAppKeyField;
    private JBTextField youdaoSecretKeyField;
    private JBTextField googleApiKeyField;
    private JBTextField proxyHostField;
    private JBTextField proxyPortField;
    private JBTextField proxyUsernameField;
    private JBTextField proxyPasswordField;
    private JPanel baiduPanel;
    private JPanel youdaoPanel;
    private JPanel googlePanel;
    private JPanel proxyPanel;
    private DictionaryTablePanel dictionaryPanel;
    private boolean isModified = false;

    private static final Map<String, String> LANGUAGES = Map.of(
        "auto", "自动检测",
        "zh", "中文",
        "en", "英文",
        "ja", "日文",
        "ko", "韩文"
    );

    private static final Map<String, String> TRANSLATORS = Map.of(
        "baidu", "百度翻译",
        "youdao", "有道翻译",
        "google", "谷歌翻译"
    );

    public TranslateSettingsConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "翻译助手";
    }

    @Override
    public @Nullable JComponent createComponent() {
        targetLanguageComboBox = new ComboBox<>(LANGUAGES.keySet().toArray(new String[0]));
        sourceLanguageComboBox = new ComboBox<>(LANGUAGES.keySet().toArray(new String[0]));
        translatorComboBox = new ComboBox<>(TRANSLATORS.keySet().toArray(new String[0]));
        autoDetectSourceLanguageCheckBox = new JBCheckBox("自动检测源语言");
        useProxyCheckBox = new JBCheckBox("使用代理服务器");
        baiduAppIdField = new JBTextField();
        baiduSecretKeyField = new JBTextField();
        youdaoAppKeyField = new JBTextField();
        youdaoSecretKeyField = new JBTextField();
        googleApiKeyField = new JBTextField();
        proxyHostField = new JBTextField();
        proxyPortField = new JBTextField();
        proxyUsernameField = new JBTextField();
        proxyPasswordField = new JBTextField();
        dictionaryPanel = new DictionaryTablePanel();

        // Set display values for combo boxes
        targetLanguageComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
            new JLabel(LANGUAGES.getOrDefault(value, value)));
        sourceLanguageComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
            new JLabel(LANGUAGES.getOrDefault(value, value)));
        translatorComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
            new JLabel(TRANSLATORS.getOrDefault(value, value)));

        // Add listener to auto detect checkbox
        autoDetectSourceLanguageCheckBox.addActionListener(e -> {
            sourceLanguageComboBox.setEnabled(!autoDetectSourceLanguageCheckBox.isSelected());
            if (autoDetectSourceLanguageCheckBox.isSelected()) {
                sourceLanguageComboBox.setSelectedItem("auto");
            }
        });

        // Add listener to proxy checkbox
        useProxyCheckBox.addActionListener(e -> updateProxyFields());

        // Create translator panels
        baiduPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("百度翻译 APP ID:", baiduAppIdField)
            .addLabeledComponent("百度翻译密钥:", baiduSecretKeyField)
            .getPanel();

        youdaoPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("有道翻译 APP Key:", youdaoAppKeyField)
            .addLabeledComponent("有道翻译密钥:", youdaoSecretKeyField)
            .getPanel();

        googlePanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("谷歌翻译 API Key:", googleApiKeyField)
            .getPanel();

        // Create proxy panel
        proxyPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent("代理服务器:", proxyHostField)
            .addLabeledComponent("代理端口:", proxyPortField)
            .addLabeledComponent("代理用户名:", proxyUsernameField)
            .addLabeledComponent("代理密码:", proxyPasswordField)
            .getPanel();

        // Add listener to translator combo box
        translatorComboBox.addActionListener(e -> updatePanels());

        // Create shortcut hint panel
        JPanel shortcutHintPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        shortcutHintPanel.setBorder(JBUI.Borders.empty(4, 0));
        JBLabel shortcutLabel = new JBLabel("快捷键可以在快捷键设置中自定义");
        JButton keymapButton = new JButton("打开快捷键设置");
        keymapButton.setBorderPainted(false);
        keymapButton.setContentAreaFilled(false);
        keymapButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        keymapButton.addActionListener(e -> openKeymapSettings());
        shortcutHintPanel.add(shortcutLabel);
        shortcutHintPanel.add(keymapButton);

        // Create dictionary settings panel
        JPanel dictionarySettingsPanel = FormBuilder.createFormBuilder()
            .addSeparator()
            .addComponent(new JLabel("词典设置"))
            .addComponent(dictionaryPanel)
            .getPanel();

        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(shortcutHintPanel)
            .addSeparator()
            .addLabeledComponent("目标语言:", targetLanguageComboBox)
            .addLabeledComponent("源语言:", sourceLanguageComboBox)
            .addComponent(autoDetectSourceLanguageCheckBox)
            .addSeparator()
            .addLabeledComponent("翻译引擎:", translatorComboBox)
            .addComponent(baiduPanel)
            .addComponent(youdaoPanel)
            .addComponent(googlePanel)
            .addSeparator()
            .addComponent(useProxyCheckBox)
            .addComponent(proxyPanel)
            .addComponent(dictionarySettingsPanel)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();

        updatePanels();
        return mainPanel;
    }

    private void updatePanels() {
        String selectedTranslator = (String) translatorComboBox.getSelectedItem();
        baiduPanel.setVisible("baidu".equals(selectedTranslator));
        youdaoPanel.setVisible("youdao".equals(selectedTranslator));
        googlePanel.setVisible("google".equals(selectedTranslator));
        useProxyCheckBox.setVisible("google".equals(selectedTranslator));
        proxyPanel.setVisible("google".equals(selectedTranslator) && useProxyCheckBox.isSelected());
    }

    private void updateProxyFields() {
        proxyPanel.setVisible(useProxyCheckBox.isSelected());
    }

    private void openKeymapSettings() {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, "Keymap");
    }

    @Override
    public boolean isModified() {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        try {
            int proxyPort = proxyPortField.getText().isEmpty() ? 0 : Integer.parseInt(proxyPortField.getText());
            return !settings.targetLanguage.equals(targetLanguageComboBox.getSelectedItem()) ||
                   !settings.sourceLanguage.equals(sourceLanguageComboBox.getSelectedItem()) ||
                   !settings.selectedTranslator.equals(translatorComboBox.getSelectedItem()) ||
                   settings.autoDetectSourceLanguage != autoDetectSourceLanguageCheckBox.isSelected() ||
                   settings.useProxy != useProxyCheckBox.isSelected() ||
                   !settings.baiduAppId.equals(baiduAppIdField.getText()) ||
                   !settings.baiduSecretKey.equals(baiduSecretKeyField.getText()) ||
                   !settings.youdaoAppKey.equals(youdaoAppKeyField.getText()) ||
                   !settings.youdaoSecretKey.equals(youdaoSecretKeyField.getText()) ||
                   !settings.googleApiKey.equals(googleApiKeyField.getText()) ||
                   !settings.proxyHost.equals(proxyHostField.getText()) ||
                   settings.proxyPort != proxyPort ||
                   !settings.proxyUsername.equals(proxyUsernameField.getText()) ||
                   !settings.proxyPassword.equals(proxyPasswordField.getText()) ||
                   isModified;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        settings.targetLanguage = (String) targetLanguageComboBox.getSelectedItem();
        settings.sourceLanguage = (String) sourceLanguageComboBox.getSelectedItem();
        settings.selectedTranslator = (String) translatorComboBox.getSelectedItem();
        settings.autoDetectSourceLanguage = autoDetectSourceLanguageCheckBox.isSelected();
        settings.useProxy = useProxyCheckBox.isSelected();
        settings.baiduAppId = baiduAppIdField.getText();
        settings.baiduSecretKey = baiduSecretKeyField.getText();
        settings.youdaoAppKey = youdaoAppKeyField.getText();
        settings.youdaoSecretKey = youdaoSecretKeyField.getText();
        settings.googleApiKey = googleApiKeyField.getText();
        settings.proxyHost = proxyHostField.getText();
        try {
            settings.proxyPort = proxyPortField.getText().isEmpty() ? 0 : Integer.parseInt(proxyPortField.getText());
        } catch (NumberFormatException e) {
            throw new ConfigurationException("代理端口必须是数字");
        }
        settings.proxyUsername = proxyUsernameField.getText();
        settings.proxyPassword = proxyPasswordField.getText();

        // 保存词典设置
        DictionaryService dictionaryService = project.getService(DictionaryService.class);
        dictionaryService.setEntries(new ArrayList<>(dictionaryPanel.getEntries()));

        isModified = false;
    }

    @Override
    public void reset() {
        TranslateSettingsState settings = project.getService(TranslateSettingsState.class);
        targetLanguageComboBox.setSelectedItem(settings.targetLanguage);
        sourceLanguageComboBox.setSelectedItem(settings.sourceLanguage);
        translatorComboBox.setSelectedItem(settings.selectedTranslator);
        autoDetectSourceLanguageCheckBox.setSelected(settings.autoDetectSourceLanguage);
        useProxyCheckBox.setSelected(settings.useProxy);
        sourceLanguageComboBox.setEnabled(!settings.autoDetectSourceLanguage);
        baiduAppIdField.setText(settings.baiduAppId);
        baiduSecretKeyField.setText(settings.baiduSecretKey);
        youdaoAppKeyField.setText(settings.youdaoAppKey);
        youdaoSecretKeyField.setText(settings.youdaoSecretKey);
        googleApiKeyField.setText(settings.googleApiKey);
        proxyHostField.setText(settings.proxyHost);
        proxyPortField.setText(settings.proxyPort > 0 ? String.valueOf(settings.proxyPort) : "");
        proxyUsernameField.setText(settings.proxyUsername);
        proxyPasswordField.setText(settings.proxyPassword);

        // 重置词典设置
        DictionaryService dictionaryService = project.getService(DictionaryService.class);
        dictionaryPanel.setEntries(new ArrayList<>(dictionaryService.getEntries()));

        isModified = false;
        updatePanels();
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
        targetLanguageComboBox = null;
        sourceLanguageComboBox = null;
        translatorComboBox = null;
        autoDetectSourceLanguageCheckBox = null;
        useProxyCheckBox = null;
        baiduAppIdField = null;
        baiduSecretKeyField = null;
        youdaoAppKeyField = null;
        youdaoSecretKeyField = null;
        googleApiKeyField = null;
        proxyHostField = null;
        proxyPortField = null;
        proxyUsernameField = null;
        proxyPasswordField = null;
        baiduPanel = null;
        youdaoPanel = null;
        googlePanel = null;
        proxyPanel = null;
        dictionaryPanel = null;
    }

    private class DictionaryTablePanel extends JPanel {
        private final JBTable table;
        private final DictionaryTableModel tableModel;
        private final JButton addButton;
        private final JButton editButton;
        private final JButton removeButton;

        public DictionaryTablePanel() {
            super(new BorderLayout());
            tableModel = new DictionaryTableModel();
            table = new JBTable(tableModel);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setPreferredScrollableViewportSize(new Dimension(-1, 200));

            // 创建按钮面板
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            addButton = new JButton("添加");
            editButton = new JButton("编辑");
            removeButton = new JButton("删除");

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(removeButton);

            // 添加按钮事件
            addButton.addActionListener(e -> addEntry());
            editButton.addActionListener(e -> editEntry());
            removeButton.addActionListener(e -> removeEntry());

            // 更新按钮状态
            table.getSelectionModel().addListSelectionListener(e -> updateButtons());

            // 添加组件到面板
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBorder(JBUI.Borders.empty(5, 0));
            add(scrollPane, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);

            updateButtons();
        }

        private void updateButtons() {
            int selectedRow = table.getSelectedRow();
            editButton.setEnabled(selectedRow != -1);
            removeButton.setEnabled(selectedRow != -1);
        }

        private void addEntry() {
            DictionaryEntry entry = showEditDialog(null);
            if (entry != null) {
                tableModel.addEntry(entry);
                isModified = true;
            }
        }

        private void editEntry() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                DictionaryEntry oldEntry = tableModel.getEntryAt(selectedRow);
                DictionaryEntry newEntry = showEditDialog(oldEntry);
                if (newEntry != null) {
                    tableModel.updateEntry(selectedRow, newEntry);
                    isModified = true;
                }
            }
        }

        private void removeEntry() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeEntry(selectedRow);
                isModified = true;
                updateButtons();
            }
        }

        private DictionaryEntry showEditDialog(DictionaryEntry entry) {
            JPanel panel = new JPanel(new GridLayout(0, 1));
            JBTextField sourceField = new JBTextField();
            JBTextField targetField = new JBTextField();
            JBCheckBox caseSensitiveBox = new JBCheckBox("区分大小写");
            JBCheckBox enabledBox = new JBCheckBox("启用");

            if (entry != null) {
                sourceField.setText(entry.getSource());
                targetField.setText(entry.getTarget());
                caseSensitiveBox.setSelected(entry.isCaseSensitive());
                enabledBox.setSelected(entry.isEnabled());
            } else {
                enabledBox.setSelected(true);
            }

            panel.add(new JLabel("源文本:"));
            panel.add(sourceField);
            panel.add(new JLabel("目标文本:"));
            panel.add(targetField);
            panel.add(caseSensitiveBox);
            panel.add(enabledBox);

            int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                entry == null ? "添加词典条目" : "编辑词典条目",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );

            if (result == JOptionPane.OK_OPTION) {
                String source = sourceField.getText().trim();
                String target = targetField.getText().trim();
                if (!source.isEmpty() && !target.isEmpty()) {
                    return new DictionaryEntry(
                        source,
                        target,
                        caseSensitiveBox.isSelected(),
                        enabledBox.isSelected()
                    );
                }
            }
            return null;
        }

        public List<DictionaryEntry> getEntries() {
            return tableModel.getEntries();
        }

        public void setEntries(List<DictionaryEntry> entries) {
            tableModel.setEntries(entries);
        }
    }

    private class DictionaryTableModel extends AbstractTableModel {
        private final List<DictionaryEntry> entries = new ArrayList<>();
        private final String[] columnNames = {"源文本", "目标文本", "区分大小写", "启用"};

        @Override
        public int getRowCount() {
            return entries.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 2 || columnIndex == 3) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2 || columnIndex == 3;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            DictionaryEntry entry = entries.get(rowIndex);
            switch (columnIndex) {
                case 0: return entry.getSource();
                case 1: return entry.getTarget();
                case 2: return entry.isCaseSensitive();
                case 3: return entry.isEnabled();
                default: return null;
            }
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            DictionaryEntry entry = entries.get(rowIndex);
            switch (columnIndex) {
                case 2:
                    entry.setCaseSensitive((Boolean) value);
                    break;
                case 3:
                    entry.setEnabled((Boolean) value);
                    break;
            }
            isModified = true;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        public void addEntry(DictionaryEntry entry) {
            entries.add(entry);
            fireTableRowsInserted(entries.size() - 1, entries.size() - 1);
        }

        public void updateEntry(int index, DictionaryEntry entry) {
            entries.set(index, entry);
            fireTableRowsUpdated(index, index);
        }

        public void removeEntry(int index) {
            entries.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public DictionaryEntry getEntryAt(int index) {
            return entries.get(index);
        }

        public List<DictionaryEntry> getEntries() {
            return new ArrayList<>(entries);
        }

        public void setEntries(List<DictionaryEntry> newEntries) {
            entries.clear();
            entries.addAll(newEntries);
            fireTableDataChanged();
        }
    }
} 