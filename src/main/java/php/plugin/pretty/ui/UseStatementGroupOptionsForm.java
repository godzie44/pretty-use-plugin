package php.plugin.pretty.ui;

import org.jetbrains.annotations.NotNull;
import php.plugin.pretty.dict.UseStatementGroupOptions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class UseStatementGroupOptionsForm extends JDialog {

    private final UseStatementGroupOptions useStatementGroupOptions;
    private JPanel contentPanel;
    private JTextField regExpField;
    private JSpinner regExpPriority;
    private JCheckBox enableCheckBox;
    private JButton OKButton;
    private JButton cancelButton;

    @NotNull
    private final Callback callback;

    public UseStatementGroupOptionsForm(@NotNull UseStatementGroupOptions useStatementGroupOptions, @NotNull Callback callback)
    {
        this.useStatementGroupOptions = useStatementGroupOptions;
        this.callback = callback;

        setContentPane(contentPanel);
        setModal(true);
        getRootPane().setDefaultButton(OKButton);

        regExpField.setText(useStatementGroupOptions.getRegex());
        regExpPriority.setValue(useStatementGroupOptions.getWeight());
        enableCheckBox.setSelected(useStatementGroupOptions.isEnabled());

        OKButton.addActionListener(e -> onOK());

        cancelButton.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


    }

    private void onOK() {
//        String classText = StringUtils.strip(textClassName.getText(), "\\");
//        if(!PhpNameUtil.isValidNamespaceFullName(classText)) {
//            JOptionPane.showMessageDialog(this, "Invalid class name");
//            return;
//        }
//
//        String alias = textAlias.getText();
//        if(!PhpNameUtil.isValidNamespaceFullName(alias)) {
//            JOptionPane.showMessageDialog(this, "Invalid alias");
//            return;
//        }
//
        this.useStatementGroupOptions.setRegex(regExpField.getText());
        this.useStatementGroupOptions.setWeight((Integer) regExpPriority.getValue());
        this.useStatementGroupOptions.setEnabled(enableCheckBox.isSelected());

        this.callback.ok(this.useStatementGroupOptions);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void create(@NotNull Component component, @NotNull Callback callback) {
        create(component, new UseStatementGroupOptions("", 0, true), callback);
    }

    public static void create(@NotNull Component component, @NotNull UseStatementGroupOptions option, @NotNull Callback callback) {
        UseStatementGroupOptionsForm dialog = new UseStatementGroupOptionsForm(option, callback);
        dialog.setMinimumSize(new Dimension(400, 0));
        dialog.pack();
        dialog.setTitle("New reg exp");
        dialog.setLocationRelativeTo(component);
        dialog.setVisible(true);
    }


    public interface Callback {
        void ok(@NotNull UseStatementGroupOptions option);
    }
}
