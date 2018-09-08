package php.plugin.pretty.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ElementProducer;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import php.plugin.pretty.ApplicationSettings;
import php.plugin.pretty.dict.RegexOption;

import javax.swing.*;
import java.util.ArrayList;


public class RegexListForm implements Configurable {
    private JButton resetButton;



    // private TableView<UseAliasOption> tableView;



    private ListTableModel<RegexOption> modelList;
    private TableView<RegexOption> tableView;
    private boolean changed = false;

    private JPanel panel;
    private JPanel innerPanel;

    public RegexListForm() {
        this.tableView = new TableView<>();

        this.modelList = new ListTableModel<>(
                new ExpressionColumn(),
                new PriorityColumn(),
                new DisableColumn()
        );

        this.tableView.setModelAndUpdateColumns(this.modelList);

        resetButton.addActionListener(e -> {
            tableView.getTableViewModel().fireTableDataChanged();
            changed = true;
            resetList();
            try {
                apply();
                ApplicationSettings.getInstance().provideDefaults = false;
                JOptionPane.showMessageDialog(panel, "Default expression applied");
            } catch (ConfigurationException ignored) {
            }
        });

        initList();
    }

    private void resetList() {
        while(this.modelList.getRowCount() > 0) {
            this.modelList.removeRow(0);
        }

        this.modelList.addRows(ApplicationSettings.getDefaultRegexOption());
    }

    private void initList() {
        this.modelList.addRows(ApplicationSettings.getUseRegexOptionsWithDefaultFallback());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Expressions";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<RegexOption>() {
            @Override
            public RegexOption createElement() {
                return null;
            }

            @Override
            public boolean canCreateElement() {
                return true;
            }
        });

        tablePanel.setEditAction(anActionButton -> {

            RegexOption useAliasOption = tableView.getSelectedObject();
            if(useAliasOption == null) {
                return;
            }

            RegexForm.create(innerPanel, useAliasOption, option -> {
                tableView.getTableViewModel().fireTableDataChanged();
                changed = true;
            });
        });

        tablePanel.setAddAction(anActionButton -> RegexForm.create(innerPanel, option -> {
            tableView.getListTableModel().addRow(option);
            changed = true;
        }));

        tablePanel.setRemoveAction(anActionButton -> {
            modelList.removeRow(tableView.getSelectedRow());
            tableView.getTableViewModel().fireTableDataChanged();
            changed = true;
        });

        tablePanel.disableDownAction();
        tablePanel.disableUpAction();

        JPanel newPanel = tablePanel.createPanel();
        innerPanel.add(newPanel);

        return panel;
    }

    @Override
    public boolean isModified() {
        return this.changed;
    }

    @Override
    public void apply() throws ConfigurationException {
        ApplicationSettings.getInstance().regexOptions = new ArrayList<>(this.tableView.getListTableModel().getItems());
        ApplicationSettings.getInstance().provideDefaults = false;
        this.changed = false;
    }

    @Override
    public void reset() {
        while(this.modelList.getRowCount() > 0) {
            this.modelList.removeRow(0);
        }

        initList();
    }

    @Override
    public void disposeUIResources() {

    }

    private static class ExpressionColumn extends ColumnInfo<RegexOption, String> {

        ExpressionColumn() {
            super("Expression");
        }

        @Nullable
        @Override
        public String valueOf(RegexOption option) {
            return option.getRegex();
        }
    }

    private static class PriorityColumn extends ColumnInfo<RegexOption, String> {

        PriorityColumn() {
            super("Priority");
        }

        @Nullable
        @Override
        public String valueOf(RegexOption option) {
            return String.valueOf(option.getWeight());
        }
    }

    private class DisableColumn extends ColumnInfo<RegexOption, Boolean> {

        public DisableColumn() {
            super("Disable");
        }

        public Boolean valueOf(RegexOption option) {
            return option.isEnabled();
        }

        public void setValue(RegexOption option, Boolean value){
            option.setEnabled(value);
            tableView.getListTableModel().fireTableDataChanged();
            changed = true;
        }

        public int getWidth(JTable table) {
            return 50;
        }

        public boolean isCellEditable(RegexOption groupItem)
        {
            return true;
        }

        public Class getColumnClass()
        {
            return Boolean.class;
        }
    }
}
