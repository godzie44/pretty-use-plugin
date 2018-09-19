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
import php.plugin.pretty.dict.UseStatementGroupOptions;

import javax.swing.*;
import java.util.ArrayList;


public class UseStatementGroupOptionsListForm implements Configurable {
    private JButton resetButton;



    // private TableView<UseAliasOption> tableView;



    private ListTableModel<UseStatementGroupOptions> modelList;
    private TableView<UseStatementGroupOptions> tableView;
    private boolean changed = false;

    private JPanel panel;
    private JPanel innerPanel;
    private JTextField hintUseAnother_libsAsTextField;

    public UseStatementGroupOptionsListForm() {
        this.tableView = new TableView<>();

        this.modelList = new ListTableModel<>(
                new ExpressionColumn(),
                new PriorityColumn(),
                new EnableColumn()
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

        this.modelList.addRows(ApplicationSettings.getDefaultUseStatementGroupOption());
    }

    private void initList() {
        this.modelList.addRows(ApplicationSettings.getStatementGroupOptionsWithDefaultFallback());
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
        ToolbarDecorator tablePanel = ToolbarDecorator.createDecorator(this.tableView, new ElementProducer<UseStatementGroupOptions>() {
            @Override
            public UseStatementGroupOptions createElement() {
                return null;
            }

            @Override
            public boolean canCreateElement() {
                return true;
            }
        });

        tablePanel.setEditAction(anActionButton -> {

            UseStatementGroupOptions useAliasOption = tableView.getSelectedObject();
            if(useAliasOption == null) {
                return;
            }

            UseStatementGroupOptionsForm.create(innerPanel, useAliasOption, option -> {
                tableView.getTableViewModel().fireTableDataChanged();
                changed = true;
            });
        });

        tablePanel.setAddAction(anActionButton -> UseStatementGroupOptionsForm.create(innerPanel, option -> {
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
        ApplicationSettings.getInstance().useStatementGroupOptions = new ArrayList<>(this.tableView.getListTableModel().getItems());
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

    private static class ExpressionColumn extends ColumnInfo<UseStatementGroupOptions, String> {

        ExpressionColumn() {
            super("Expression");
        }

        @Nullable
        @Override
        public String valueOf(UseStatementGroupOptions option) {
            return option.getRegex();
        }
    }

    private static class PriorityColumn extends ColumnInfo<UseStatementGroupOptions, String> {

        PriorityColumn() {
            super("Priority");
        }

        @Nullable
        @Override
        public String valueOf(UseStatementGroupOptions option) {
            return String.valueOf(option.getWeight());
        }
    }

    private class EnableColumn extends ColumnInfo<UseStatementGroupOptions, Boolean> {

        public EnableColumn() {
            super("Enable");
        }

        public Boolean valueOf(UseStatementGroupOptions option) {
            return option.isEnabled();
        }

        public void setValue(UseStatementGroupOptions option, Boolean value){
            option.setEnabled(value);
            tableView.getListTableModel().fireTableDataChanged();
            changed = true;
        }

        public int getWidth(JTable table) {
            return 50;
        }

        public boolean isCellEditable(UseStatementGroupOptions groupItem)
        {
            return true;
        }

        public Class getColumnClass()
        {
            return Boolean.class;
        }
    }
}
