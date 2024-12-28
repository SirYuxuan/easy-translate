package cc.oofo.easytranslate.dictionary;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DictionaryConfigurable implements Configurable {
    private final Project project;
    private JPanel mainPanel;
    private DictionaryTablePanel dictionaryPanel;
    private boolean isModified = false;

    public DictionaryConfigurable(Project project) {
        this.project = project;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "翻译词典";
    }

    @Override
    public @Nullable JComponent createComponent() {
        dictionaryPanel = new DictionaryTablePanel();
        mainPanel = FormBuilder.createFormBuilder()
            .addComponent(dictionaryPanel)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        return isModified;
    }

    @Override
    public void apply() {
        DictionaryService dictionaryService = project.getService(DictionaryService.class);
        dictionaryService.setEntries(new ArrayList<>(dictionaryPanel.getEntries()));
        isModified = false;
    }

    @Override
    public void reset() {
        DictionaryService dictionaryService = project.getService(DictionaryService.class);
        dictionaryPanel.setEntries(new ArrayList<>(dictionaryService.getEntries()));
        isModified = false;
    }

    @Override
    public void disposeUIResources() {
        mainPanel = null;
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
            add(new JScrollPane(table), BorderLayout.CENTER);
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